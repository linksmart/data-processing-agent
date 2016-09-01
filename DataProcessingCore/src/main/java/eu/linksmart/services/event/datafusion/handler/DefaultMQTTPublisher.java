package eu.linksmart.services.event.datafusion.handler;

import eu.linksmart.services.event.datafusion.intern.Const;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultMQTTPublisher implements Publisher {
    private List<String> outputs;
    private List<String> scopes;
    private String id;
    private Map<String, StaticBroker> brokers = new Hashtable<>();
    private transient Logger loggerService = Utils.initLoggingConf(this.getClass());
    private transient Configurator conf = Configurator.getDefaultConfig();
    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */
    public final static Map<String,Map.Entry<String,String>> knownInstances= new Hashtable<>();
    public static boolean addKnownLocations(String statement) throws StatementException {
        String[] nameURL = statement.toLowerCase().replace("add instance", "").trim().split("=");
        if (nameURL.length == 2) {
            String namePort[] = nameURL[1].split(":");

            knownInstances.put(nameURL[0], new AbstractMap.SimpleImmutableEntry<>(namePort[0], namePort[1]));

        } else {
            return false;
        }
        return true;
    }

    public static boolean removeKnownLocations(String alias) throws StatementException {
        if( knownInstances.containsKey(alias))
            knownInstances.remove(alias);
        else
            return false;

        return true;
    }
    static {
        List<Object> alias = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_ALIASES_CONF_PATH);
        List<Object> brokerHostname = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_CONF_PATH);
        List<Object> brokerPort = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_PORT_CONF_PATH);
        if(alias.size()!=brokerHostname.size()&& alias.size()!=brokerPort.size())
            Utils.initLoggingConf(DefaultMQTTPublisher.class).error("Inconsistent configuration in "+
                            Const.EVENTS_OUT_BROKER_ALIASES_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_BROKER_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_BROKER_CONF_PATH
            );
        else {
            for(int i=0;i<alias.size();i++)
                knownInstances.put(
                        alias.get(i).toString(),
                        new AbstractMap.SimpleEntry<>(
                            brokerHostname.get(i).toString(),
                            brokerPort.get(i).toString()
                        )
                );
        }
    }
    public DefaultMQTTPublisher(Statement statement) {
        outputs = statement.getOutput()!=null ? Arrays.asList(statement.getOutput()) : new ArrayList<>();
        scopes =  statement.getScope()!=null  ? Arrays.asList(statement.getScope())  : new ArrayList<>();
        id = statement.getID();

        try {
            initScopes();
            initOutputs();
        } catch (MalformedURLException | RemoteException | StatementException e) {
            loggerService.error(e.getMessage(), e.getCause());
        }

    }
    public DefaultMQTTPublisher(String id,String[] outputs, String[] scopes){
        this.outputs =  Arrays.asList(outputs);
        this.scopes =  Arrays.asList(scopes);
        this.id = id;

        try {
            initScopes();
            initOutputs();
        } catch (MalformedURLException | RemoteException | StatementException e) {
            loggerService.error(e.getMessage(), e.getCause());
        }

    }

    private void initOutputs(){
        if(outputs==null ||outputs.isEmpty()){
            String aux= Configurator.getDefaultConfig().getString(Const.EVENT_OUT_TOPIC_CONF_PATH);
            if(aux == null)
                aux = "/federation1/amiat/v2/cep/";

            outputs = Arrays.asList(aux + id);
        }
    }

    private void initScopes() throws StatementException, MalformedURLException, RemoteException {
        try {

            if(scopes.size()==0){
                scopes =  Arrays.asList("local");
            }

            if(!brokers.isEmpty()){
                for(StaticBroker brokerService :brokers.values())
                    try {
                        brokerService.destroy();
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(),e);
                    }
                brokers.clear();
            }

            for(String scope: scopes) {
                if (!knownInstances.containsKey(scope.toLowerCase()))
                    throw new StatementException( id,"Statement", "The selected scope (" + scopes.get(0) + ") is unknown");

                brokers.put(scope, new StaticBroker(
                        knownInstances.get(scope.toLowerCase()).getKey(),
                        knownInstances.get(scope.toLowerCase()).getValue()
                ));
            }

        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }
    private String makeTopic(String string){
        return string+"/"+id;
    }
    @Override
    public synchronized boolean publish(byte[] payload) {

        brokers.values().stream().forEach(b-> outputs.forEach(o -> {
            try {
                b.publish(makeTopic(o), payload);
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }
        }));
        return true;
    }

    @Override
    public boolean publish(byte[] payload, String output, String scope) {
        try {
            brokers.get(scope).publish(makeTopic(output), payload);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    @Override
    public boolean publish(byte[] payload, String output) {
        brokers.values().stream().forEach(b->{
            try {
                b.publish(makeTopic(output), payload);
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }
        });
        return true;
    }


    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void close() {
        brokers.values().forEach(b -> {
            try {
                b.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
