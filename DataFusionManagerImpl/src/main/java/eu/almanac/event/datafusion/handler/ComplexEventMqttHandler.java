package eu.almanac.event.datafusion.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.broker.BrokerService;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import eu.almanac.ogc.sensorthing.api.datamodel.*;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;
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
    public class ComplexEventMqttHandler extends BaseEventHandler<Map> implements eu.linksmart.api.event.datafusion.ComplexEventMqttHandler<Map> {


    protected ArrayList<StaticBroker> brokerServices;


    private Configurator conf =  Configurator.getDefaultConfig();
    protected ObjectMapper parser = new ObjectMapper();
    @Deprecated
    protected boolean sendPerProperty;
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected String PUBLISHER_ID ;
    Gson gson;

    // configuration
    private  String STATEMENT_INOUT_BASE_TOPIC= "queries/";
    private  String TIME_ISO_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
    private String[] handlerScopes;
    static {
        List<String> hosts = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_CONF_PATH),
                ports = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_PORT_CONF_PATH),
                aliases = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_BROKER_ALIASES_CONF_PATH);
        if(hosts.size() == ports.size() && hosts.size() == aliases.size()) {
            Iterator<String> host = hosts.iterator(),port = ports.iterator(),alias = aliases.iterator();
            while (port.hasNext() && host.hasNext() && alias.hasNext()) {

                knownInstances.put(alias.next(), new AbstractMap.SimpleImmutableEntry<>(
                                host.next(),
                                port.next()
                        )
                );
            }
        }else if(ports.size()==1&& hosts.size() == aliases.size()){
            Iterator<String> host = hosts.iterator(),alias = aliases.iterator();
            String port = ports.get(0);
            while ( host.hasNext()) {

                knownInstances.put(alias.next(), new AbstractMap.SimpleImmutableEntry<>(
                                host.next(),
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

            String aux= Configurator.getDefaultConfig().getString(Const.EVENT_OUT_TOPIC_CONF_PATH);
            if(aux == null)
                aux = "/federation1/amiat/v2/cep/";

            OUTPUT_TOPIC = aux + query.getID();

            PUBLISHER_ID = DynamicConst.getId();

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
        gson = new GsonBuilder().setDateFormat(TIME_ISO_FORMAT).create();
        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
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

            if(eventMap.containsKey("SetEventPerEntity")){
                sendPerProperty = (Boolean)eventMap.get(("SetEventPerEntity"));

                eventMap.remove( ("SetEventPerEntity"));
            }
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
        try {

            if (eventMap.values().toArray()[0] instanceof Object[])
                update2((Object[]) eventMap.values().toArray()[0]);


            if (sendPerProperty)
                sendPerEntity(eventMap);
            else {

                sendEvent(eventMap);

            }


        } catch (MqttException e) {
            loggerService.error(e.getMessage(), e);
        }
    }

    @Deprecated
    public  void update(Event event) {

        loggerService.info("Updating query: " + query.getName());

        try {


            publish(event);

        }catch (Exception e){

            loggerService.error(e.getMessage(), e);
        }

    }


    @Deprecated
    public synchronized void update(Event[] events) {

        if (events[0].isGenerated())
            return;
        update2( events);

    }
    public synchronized void update(IoTEntityEvent[] events) {
        if (events[0].isGenerated())
            return;

        update2( events);

    }
    public synchronized void update2(Object[] events) {

        loggerService.info("Updating query: " + query.getName());


        try {


            String streamID = UUID.randomUUID().toString();
            for(Integer i=0; i<events.length;i++)
                packObservation(i,"Measure", streamID);

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);

        }

    }

    public Observation packObservation(Object event, String resultType, String StreamID) {
        Sensor sen = new Sensor();
        sen.setId(query.getID());
        sen.setObservations(null);
        Datastream ds = new Datastream();
        ds.setObservations(null);
        ds.setId(StreamID);
        Observation ob = new Observation();
        ob.setDatastream(ds);
        ob.setSensor(sen);
        ob.setPhenomenonTime(new Date());
        ob.setResultType(resultType);
        ob.setResultValue(event);
        ob.setFeatureOfInterest(null);


        return ob;
    }

    @Deprecated
    void sendEvent(Map eventMap, GenericCEP cepEvent) throws Exception {


        for(Object key : eventMap.keySet()) {

            try {
                if (key.toString().equals(""))

                if (eventMap.get(key) instanceof GenericCEP) {
                    // ignore this event if was generated by a CEP
                    if (((GenericCEP)eventMap.get(key)).isGenerated())
                        return;


                } else {

                    cepEvent.addValue(key.toString(),eventMap.get(key));


                }
            } catch (Exception eEntity) {


                loggerService.error(eEntity.getMessage(),eEntity);



            }
        }

        publish(cepEvent);

    }
    private Observation handleObject(Object event, String description, String streamID){
        if (event instanceof Observation) {
           return (Observation)event;

        // TODO: Esper Specific code must be moved to esper artifact
       // }else if (event instanceof EventBean){
        //    return handleObject(((EventBean)event).getUnderlying(),description,streamID);
        }else {


          return packObservation(event,description,streamID);


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


            for (Object key : eventMap.keySet()) {

                arrayList.add(handleObject(eventMap.get(key), key.toString(), "").getResultValue());
            }
            try {
                publish(handleObject(arrayList, "complex", streamID));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }

    }
    @Deprecated
    IoTEntityEvent sendPerEntity(Map eventMap){
        IoTEntityEvent cepEvent = new IoTEntityEvent("DataFusionManager");
        cepEvent.setAbout(query.getSource());


        if(eventMap.values().toArray()[0] instanceof IoTEntityEvent[]) {
            IoTEntityEvent[] events = (IoTEntityEvent[]) eventMap.values().toArray()[0];
            update(events);
        }

        for(Object key : eventMap.keySet()) {
            try {

                if (eventMap.get(key) instanceof IoTEntityEvent) {
                    IoTEntityEvent ent = (IoTEntityEvent) eventMap.get(key);


                      publish(ent);



                }

            } catch (Exception eEntity) {


                loggerService.error(eEntity.getMessage(),eEntity);


            }
        }

        return cepEvent;

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
