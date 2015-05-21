package eu.almanac.event.datafusion.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import eu.almanac.event.datafusion.core.DataFusionManager;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTProperty;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTValue;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class ComplexEventHandlerImpl implements ComplexEventHandler{
    private MqttClient CEPHandler;
    private final Statement query;
    private Event response;
    private ObjectMapper parser = new ObjectMapper();
    private Boolean sendPerProperty = false;


    Class PAYLOAD_TYPE = IoTEntityEvent.class;

    private final String DFM_TOPIC = "/almanac/observation/iotentity/dataFusionManager/";
    private final String EVENT_TOPIC = "/federation1/fit/v2/cep/";
    private final String ERROR_TOPIC = "/almanac/error/json/dataFusionManager/";
    private final String INFO_TOPIC = "/almanac/info/json/dataFusionManager/";

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
            this.CEPHandler= new MqttClient(knownInstances.get(query.getScope(0).toLowerCase()),query.getName());
        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public void update(Map eventMap) {

        LoggerHandler.report("info", "Updating query: " + query.getName());

        try {

            if(eventMap.containsKey((Object)(new String("SetEventPerEntity")))){
                sendPerProperty = (Boolean)eventMap.get((Object)(new String("SetEventPerEntity")));

                eventMap.remove((Object) (new String("SetEventPerEntity")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            // constructing the date formatter


            // checking if handler still connected to Broker
            if (!CEPHandler.isConnected())
                CEPHandler.connect();
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
    GenericCEP getCEPEnvelope(){
        GenericCEP ret =null;
        if(PAYLOAD_TYPE == Event.class)
            ret = new Event();
        if(PAYLOAD_TYPE == IoTEntityEvent.class)
            ret =  new IoTEntityEvent();

       ret =addMetaData(ret);

        return ret;
    }
    GenericCEP addMetaData(GenericCEP cep){
        cep.addValue(GenericCEP.GENERATED, DataFusionManager.ID.toString());
        cep.addValue(GenericCEP.TIMESTAMP,getDateNowString());

        return cep;
    }
    public void update(Event event) {

        LoggerHandler.report("info", "Updating query: " + query.getName());

        try {


            if (!CEPHandler.isConnected())
                CEPHandler.connect();

            publish(event);

        }catch (Exception e){

        }

    }


    public void update(Event[] events) {

        if (events[0].isGenerated())
            return;
        update2((Object[]) events);

    }
    public void update(IoTEntityEvent[] events) {
        if (events[0].isGenerated())
            return;

        update2((Object[]) events);

    }
    public void update2(Object[] events) {

        LoggerHandler.report("info", "Updating query: " + query.getName());


        try {


            for(Integer i=0; i<events.length;i++)
                packObservation(i,"Measure", i.toString());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void packObservation(Object event, String resultType, String StreamID) {
        Sensor sen = new Sensor();
        sen.setId(query.getName());
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


    String getDateNowString(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(tz);
        // creating DateTimeNow string
        return df.format(new Date());
    }



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




        Integer i=0;
        for(Object key : eventMap.keySet()) {

            try {


                    if (eventMap.get(key) instanceof Observation) {
                        // ignore this event if was generated by a CEP

                        Observation ob = (Observation) eventMap.get(key);
                        Datastream ds = ob.getDatastream();
                        ds.setId(DataFusionManager.ID.toString());
                        publish(eventMap.get(key));


                    } else {

                        packObservation(eventMap.get(key),key.toString(),i.toString());


                    }

                i++;
            } catch (Exception eEntity) {


                eEntity.printStackTrace();


            }
        }

      //  publish(cepEvent);

    }
    IoTEntityEvent sendPerEntity(Map eventMap){
        IoTEntityEvent cepEvent = new IoTEntityEvent("DataFusionManager");
        cepEvent.setAbout(query.getSource());
        int n=0;

        if(eventMap.values().toArray()[0] instanceof IoTEntityEvent[]) {
            IoTEntityEvent[] events = (IoTEntityEvent[]) eventMap.values().toArray()[0];
            update(events);
        }

        for(Object key : eventMap.keySet()) {
            try {

                if (eventMap.get(key) instanceof IoTEntityEvent) {
                    IoTEntityEvent ent = (IoTEntityEvent) eventMap.get(key);


                      publish(ent.getAbout(),ent);

                        continue;

                }

            } catch (Exception eEntity) {


                eEntity.printStackTrace();


            }
        }

        return cepEvent;

    }
    private void publish(String endTopic, Object ent) throws Exception {
        if(!CEPHandler.isConnected())
            CEPHandler.connect();
        if (query.haveOutput())
            for (String output : query.getOutput()) {
                CEPHandler.publish(output + "/" + endTopic,   parser.writeValueAsString(ent).getBytes(), 0, false);
            }
        else
            CEPHandler.publish(EVENT_TOPIC + endTopic, parser.writeValueAsString(ent).getBytes(), 0, false);

    }


    private void publish( Object ent) throws Exception {
       publish(query.getName(),ent);

    }


    @Override
    public boolean publishError(String errorMessage) {

        LoggerHandler.publish("query/"+query.getName(),errorMessage,null,true);

        return true;
    }

    @Override
    public void destroy(){
        try {
            if(CEPHandler.isConnected())
                CEPHandler.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            CEPHandler.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
