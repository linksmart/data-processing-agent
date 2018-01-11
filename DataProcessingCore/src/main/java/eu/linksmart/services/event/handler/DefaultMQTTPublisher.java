package eu.linksmart.services.event.handler;

import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.apache.logging.log4j.LogManager;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultMQTTPublisher implements Publisher {
    private List<String> outputs;
    private List<String> scopes;
    private String id;

    private String will=null;
    private Map<String, StaticBroker> brokers = new Hashtable<>();
    private transient Logger loggerService = LogManager.getLogger(this.getClass());
    private transient Configurator conf = Configurator.getDefaultConfig();
    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */
    public final static Set<String> knownInstances= new HashSet<>();
    private String willTopic;

    public static boolean addKnownLocations(String statement) throws StatementException {
        // todo need to define how we add new locations into the Agent configuration
       throw new NotImplementedException();
    }

    public static boolean removeKnownLocations(String alias) throws StatementException {
        if( knownInstances.contains(alias))
            knownInstances.remove(alias);
        else
            return false;

        return true;
    }
    static {
        List<Object> alias = Configurator.getDefaultConfig().getList(Const.BROKERS_ALIAS);

        if(alias!=null)
            knownInstances.addAll(alias.stream().map(Object::toString).collect(Collectors.toList()));

    }

    public DefaultMQTTPublisher(Statement statement,String will, String willTopic) {

        init(statement.getId(),statement.getOutput(),statement.getScope(),will, willTopic);

    }
    public DefaultMQTTPublisher(Statement statement, String agentID) {

        init(id,statement.getOutput(),statement.getScope(),null, null);

    }
    public DefaultMQTTPublisher(String id, List<String> outputs, String[] scopes){
        init(id,outputs,scopes,null,null);

    }
    public DefaultMQTTPublisher(String id,List<String> outputs, String[] scopes,String will, String willTopic){
        init(id,outputs,scopes,will, willTopic);

    }
    private void init(String id,List<String> outputs,  String[] scopes, String will, String willTopic){
        this.outputs = outputs;
        this.scopes =  Arrays.asList(scopes);
        this.id = id;
        this.will = will;
        this.willTopic = willTopic;

        try {
            initScopes();
            initOutputs();
        } catch (MalformedURLException | RemoteException | StatementException e) {
            loggerService.error(e.getMessage(), e.getCause());
        }
    }

    private void initOutputs(){
        if(outputs==null ||outputs.isEmpty()){

            outputs = Arrays.asList(defaultOutput() + id+"/");
        }
    }
    static public String defaultOutput(){
        String aux = Configurator.getDefaultConfig().getString(Const.EVENT_OUT_TOPIC_CONF_PATH);
        if(aux== null)
            aux = "LS/test/test/OGC/1.0/Datastreams/";
        return AgentUtils.topicReplace(aux);

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
                if (!knownInstances.contains(scope.toLowerCase()))
                    throw new StatementException( id,"Statement", "The selected scope (" + scopes.get(0) + ") is unknown");

                brokers.put(scope, new StaticBroker(scope,will,willTopic));
            }

        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }
    private String makeTopic(String string){
        return string;
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
