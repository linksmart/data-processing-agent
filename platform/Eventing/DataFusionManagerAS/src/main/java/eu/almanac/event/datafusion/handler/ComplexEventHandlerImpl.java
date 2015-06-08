package eu.almanac.event.datafusion.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.almanac.event.datafusion.intern.ConfigurationManagement;
import eu.almanac.event.datafusion.intern.LoggerService;
import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.10.2014 a researcher of Fraunhofer FIT.
 */
public class ComplexEventHandlerImpl implements ComplexEventHandler{
    private MqttClient mqttClient;
    private final Statement query;
    private ObjectMapper parser = new ObjectMapper();
    @Deprecated
    private Event response;
    @Deprecated
    private Boolean sendPerProperty = false;


    public ComplexEventHandlerImpl(Statement query) throws RemoteException {
        if(!knownInstances.containsKey("local"))
            knownInstances.put("local","tcp://localhost:1883");


        if(!knownInstances.containsKey("ismb_public") )
            knownInstances.put("ismb_public","tcp://130.192.86.227:1883");
        this.query=query;
       // parser = new Gson();
        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        response = new Event();
        response.setBaseName(query.getName());
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);


        try {
            this.mqttClient = new MqttClient(knownInstances.get(query.getScope(0).toLowerCase()),query.getName(), new MemoryPersistence());
        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public void update(Map eventMap) {

        LoggerService.report("info", "Updating query: " + query.getName());

        try {

            if(eventMap.containsKey("SetEventPerEntity")){
                sendPerProperty = (Boolean)eventMap.get(("SetEventPerEntity"));

                eventMap.remove( ("SetEventPerEntity"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            // constructing the date formatter


            // checking if handler still connected to Broker
            if (!mqttClient.isConnected())
                mqttClient.connect();
            // creating Event Response object
            //GenericCEP cepEvent = getCEPEnvelope();

            // if the events is an array of events then handle the event as array
            if (eventMap.values().toArray()[0] instanceof Object[])
                update2((Object[]) eventMap.values().toArray()[0]);


            if (sendPerProperty)
                sendPerEntity(eventMap);
            else {

                sendEvent(eventMap);

            }
//            publish(query.getSource(), cepEvent);


        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public void update(Event event) {

        LoggerService.report("info", "Updating query: " + query.getName());

        try {


            if (!mqttClient.isConnected())
                mqttClient.connect();

            publish(event);

        }catch (Exception e){

            LoggerService.report("Error",e);
        }

    }


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

        LoggerService.report("info", "Updating query: " + query.getName());


        try {


            String streamID = UUID.randomUUID().toString();
            for(Integer i=0; i<events.length;i++)
                packObservation(i,"Measure", streamID);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void packObservation(Object event, String resultType, String StreamID) {
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


        try {
            publish(ob);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    ((GenericCEP)eventMap.get(key)).aggregateToAnEvent(cepEvent);



                } else {

                    cepEvent.addValue(key.toString(),eventMap.get(key));


                }
            } catch (Exception eEntity) {


                eEntity.printStackTrace();


            }
        }

        publish(cepEvent);

    }
    void sendEvent(Map eventMap) throws MqttException {




        String streamID = UUID.randomUUID().toString();
        for(Object key : eventMap.keySet()) {

            try {


                    if (eventMap.get(key) instanceof Observation) {
                        // ignore this event if was generated by a CEP

                   /*     Observation ob = (Observation) eventMap.get(key);
                        Datastream ds = ob.getDatastream();
                        ds.setId(streamID.toString());
                        ob.getSensor().setId(DataFusionManager.ID.toString());*/
                        publish(eventMap.get(key));


                    } else {

                        packObservation(eventMap.get(key),key.toString(),streamID);


                    }

            } catch (Exception eEntity) {


                eEntity.printStackTrace();


            }
        }

      //  publish(cepEvent);

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


                eEntity.printStackTrace();


            }
        }

        return cepEvent;

    }
    private void publish( Object ent) throws Exception {
        if(!mqttClient.isConnected())
            mqttClient.connect();
        if (query.haveOutput())
            for (String output : query.getOutput()) {
                mqttClient.publish(output + "/" + query.getHash(),   parser.writeValueAsString(ent).getBytes(), 0, false);
            }
        else
            mqttClient.publish(ConfigurationManagement.FUSED_TOPIC + query.getHash(), parser.writeValueAsString(ent).getBytes(), 0, false);

    }




    @Override
    public boolean publishError(String errorMessage) {

        LoggerService.publish("query/" + query.getName(), errorMessage, null, true);

        return true;
    }

    @Override
    public void destroy(){
        try {
            if(mqttClient.isConnected())
                mqttClient.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
