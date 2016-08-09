package eu.almanac.event.datafusion.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.almanac.event.datafusion.handler.base.BaseEventHandler;
import eu.almanac.event.datafusion.handler.base.BaseMapEventHandler;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import eu.almanac.ogc.sensorthing.api.datamodel.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by José Ángel Carvajal on 06.10.2014 a researcher of Fraunhofer FIT.
 */
    public class ComplexEventMqttHandler extends BaseMapEventHandler implements eu.linksmart.api.event.datafusion.ComplexEventMqttHandler {

    protected ArrayList<StaticBroker> brokerServices;

    private Configurator conf =  Configurator.getDefaultConfig();
    protected ObjectMapper parser = new ObjectMapper();

    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected String PUBLISHER_ID ;

    // configuration
    private  String STATEMENT_INOUT_BASE_TOPIC= "queries/";
    private  String TIME_ISO_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
    private boolean AGGREGATE_EVENTS =true;
    private String[] handlerScopes;
    static {
        List<Object> hosts = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_CONF_PATH),
                ports = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_PORT_CONF_PATH),
                aliases = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_ALIASES_CONF_PATH);
        if(hosts.size() == ports.size() && hosts.size() == aliases.size()) {
            Iterator<Object> host = hosts.iterator(),port = ports.iterator(),alias = aliases.iterator();
            while (port.hasNext() && host.hasNext() && alias.hasNext()) {

                knownInstances.put(alias.next().toString(), new AbstractMap.SimpleImmutableEntry<>(
                                host.next().toString(),
                                port.next().toString()
                        )
                );
            }
        }else if(ports.size()==1&& hosts.size() == aliases.size()){
            Iterator<Object> host = hosts.iterator(),alias = aliases.iterator();
            String port = ports.get(0).toString();
            while ( host.hasNext()) {

                knownInstances.put(alias.next().toString(), new AbstractMap.SimpleImmutableEntry<>(
                                host.next().toString(),
                                port
                        )
                );
            }
        }
    }

    private String OUTPUT_TOPIC;

    public ComplexEventMqttHandler(Statement query) throws RemoteException, MalformedURLException, StatementException {
        super(query);
        //super(ComplexEventMqttHandler.class.getSimpleName(),"Default handler for complex events", ComplexEventHandler.class.getSimpleName(), eu.linksmart.api.event.datafusion.ComplexEventMqttHandler.class.getSimpleName());
        this.query=query;
        try {
            TIME_ISO_FORMAT = conf.getString(Const.TIME_ISO_FORMAT);
            STATEMENT_INOUT_BASE_TOPIC =conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH);
            AGGREGATE_EVENTS=conf.getBoolean(Const.AGGREGATE_EVENTS_CONF);

            String aux= Configurator.getDefaultConfig().getString(Const.EVENT_OUT_TOPIC_CONF_PATH);
            if(aux == null)
                aux = "/federation1/amiat/v2/cep/";

            OUTPUT_TOPIC = aux + query.getID();

            PUBLISHER_ID = DynamicConst.getId();

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        parser.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        brokerServices = new ArrayList<>();


        loadScopes();
        executor.execute(eventExecutor);
    }

    private void loadScopes() throws StatementException, MalformedURLException, RemoteException {
        try {

            if(query.getScope().length==0){
                handlerScopes= new String[]{"local"};
            }else
                handlerScopes = query.getScope();

            if(!brokerServices.isEmpty()){
                for(StaticBroker brokerService :brokerServices)
                    try {
                        brokerService.destroy();
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(),e);
                    }
                brokerServices.clear();
            }

            for(String scope: handlerScopes) {
                if (!knownInstances.containsKey(scope.toLowerCase()))
                    throw new StatementException(STATEMENT_INOUT_BASE_TOPIC + query.getID(), "The selected scope (" + query.getScope(0) + ") is unknown");

                brokerServices.add(new StaticBroker(
                        knownInstances.get(scope.toLowerCase()).getKey(),
                        knownInstances.get(scope.toLowerCase()).getValue()
                ));
            }

        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    protected void processMessage(Map eventMap){
        try {

           // if (eventMap.values().toArray()[0] instanceof Object[])
            //    processMessage((Object[]) eventMap.values().toArray()[0]);
            sendEvent(eventMap);


        } catch (MqttException e) {
            loggerService.error(e.getMessage(), e);
        }
    }

    private Observation handleObject(Object event, String description, String streamID){
        if (event instanceof Observation) {
           return (Observation)event;

        // TODO: Esper Specific code must be moved to esper artifact
       // }else if (event instanceof EventBean){
        //    return handleObject(((EventBean)event).getUnderlying(),description,streamID);
        }else {

          return Observation.factory(event, description, streamID, query.getID());
        }
    }
    void sendEvent(Map eventMap) throws MqttException {

        String streamID = UUID.randomUUID().toString();
        ArrayList<Object> arrayList = new ArrayList<>();
        if (eventMap.size() == 1)
            try {
                publish(handleObject(eventMap.get(eventMap.keySet().toArray()[0]), eventMap.keySet().toArray()[0].toString(), streamID));

            } catch (Exception eEntity) {
                loggerService.error(eEntity.getMessage(), eEntity);
            }
        else {
            if(AGGREGATE_EVENTS){
                try {
                    publish(handleObject(eventMap,"Map",streamID));
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                }
            }else {
                eventMap.keySet().forEach(key-> {
                            try {
                                publish(handleObject(eventMap.get(key), key.toString(), streamID));
                            } catch (Exception ex) {
                                loggerService.error(ex.getMessage(), ex);
                            }
                        }
            );
            }
        }

    }

    private synchronized void  publish( Object ent) throws Exception {

        if(handlerScopes!=query.getScope()){
            loadScopes();
        }

        for(StaticBroker brokerService: brokerServices)

            if (query.haveOutput())
                for (String output : query.getOutput()) {
                    //if(output.lastIndexOf(0)!='/')
                    //    output+='/';
                    brokerService.publish(output,parser.writeValueAsString(ent).getBytes());
                }
            else
                brokerService.publish(OUTPUT_TOPIC+"/"+PUBLISHER_ID, parser.writeValueAsString(ent).getBytes());
    }

    @Override
    public  synchronized void destroy(){

            try {
                for(StaticBroker brokerService: brokerServices)
                    brokerService.destroy();

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }
    }

}
