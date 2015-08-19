package eu.almanac.event.datafusion.handler;

import com.espertech.esper.client.EventBean;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.almanac.event.datafusion.esper.utils.Tools;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.ComplexEventMqttHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.10.2014 a researcher of Fraunhofer FIT.
 */
public class ComplexEventHandlerImpl implements ComplexEventMqttHandler {
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected StaticBrokerService brokerService;
    protected final Statement query;
    protected ObjectMapper parser = new ObjectMapper();
    @Deprecated
    protected boolean sendPerProperty;
    Gson gson;


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
    public ComplexEventHandlerImpl(Statement query) throws RemoteException, MalformedURLException {
        if(!knownInstances.containsKey("local"))

            knownInstances.put("local",new  AbstractMap.SimpleImmutableEntry<>("almanac","1883"));



        if(!knownInstances.containsKey("ismb_public") )
            knownInstances.put("local",new  AbstractMap.SimpleImmutableEntry<>("130.192.86.227","1883"));

        this.query=query;
        gson = new GsonBuilder().setDateFormat(Tools.getIsoTimeFormat()).create();
        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);



        try {

            brokerService =  StaticBrokerService.getBrokerService(
                    this.getClass().getCanonicalName(),
                    knownInstances.get(query.getScope(0).toLowerCase()).getKey(),
                    knownInstances.get(query.getScope(0).toLowerCase()).getValue()
            );

        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }


    @Override
    public void update(Map eventMap) {

        loggerService.info( Tools.getDateNowString() + " Updating query: " + query.getName());

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
    public void update(Event event) {

        loggerService.info("Updating query: " + query.getName());

        try {


            publish(event);

        }catch (Exception e){

            loggerService.error(e.getMessage(), e);
        }

    }


    @Deprecated
    public void update(Event[] events) {

        if (events[0].isGenerated())
            return;
        update2( events);

    }
    public void update(IoTEntityEvent[] events) {
        if (events[0].isGenerated())
            return;

        update2( events);

    }
    public void update2(Object[] events) {

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
        sen.setId(query.getHash());
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


        }else if (event instanceof EventBean){
            return handleObject(((EventBean)event).getUnderlying(),description,streamID);
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



        if (query.haveOutput())
            for (String output : query.getOutput()) {
                brokerService.publish(output + "/" + query.getHash(),   parser.writeValueAsString(ent).getBytes());
            }
        else
            brokerService.publish(Configurator.getDefaultConfig().getString(Const.EVENT_OUT_TOPIC_CONF_PATH) + query.getHash(), parser.writeValueAsString(ent).getBytes());



    }






    @Override
    public void destroy(){

        try {
            brokerService.destroy(this.getClass().getCanonicalName());

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }
    }


}
