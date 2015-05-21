package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.esper.EsperQuery;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.OGCSensorThing.ObservationNumber;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.eclipse.paho.client.mqttv3.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Caravajal on 06.10.2014.
 */
public  class EventFeederImpl extends Thread implements EventFeeder, EventFeederLogic, MqttCallback {
    private MqttClient client;
    private Gson parser;
    private Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<String, DataFusionWrapper>();
    private final String MQTTConnectionID;
    private final String BROKER_URL;
    private final String DFM_QUERY_TOPIC = "#";
    private final String EVENT_TOPIC = "/federation1/trn/v2/observation/#";
    private final String ERROR_TOPIC = "/almanac/error/json/dataFusionManager";
    private final String INFO_TOPIC = "/almanac/info/json/dataFusionManager";
    private Boolean toShutdown = false;
    private ObjectMapper mapper = new ObjectMapper();

    private Boolean down =false;

    public EventFeederImpl(String broker){
        BROKER_URL = broker;


        MQTTConnectionID = "~"+UUID.randomUUID().toString();
        try {


            client = new MqttClient(BROKER_URL, MQTTConnectionID, new MemoryPersistence());


        } catch (MqttException e) {
            e.printStackTrace();
        }

        parser = new Gson();

        try {
            client.setCallback(this);
            client.connect();
            client.subscribe(DFM_QUERY_TOPIC);


            client.subscribe(EVENT_TOPIC);

        } catch (MqttException e) {
            e.printStackTrace();
        }

        start();


    }

    @Override
    protected void finalize() throws Throwable {

        shutdown();
        super.finalize();

    }

    private void shutdown(){
        removePersistenceFiles();

    }
    private void removePersistenceFiles(){
        File folder = new File(".");
        final File[] files = folder.listFiles( new FilenameFilter() {
            @Override
            public boolean accept(final File dir,
                                  final String name) {
                return name.matches("~*");
            }
        });
        for ( final File file : files ) {
            if ( !file.delete() ) {
                LoggerHandler.report("error", "Can't remove " + file.getAbsolutePath() );
            }
        }
    }

    @Override
    public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {
        dataFusionWrappers.put(dfw.getName(), dfw);

        //TODO: add code for the OSGi future
        return false;
    }

    @Override
    public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {
        dataFusionWrappers.remove(dfw.getName());

        //TODO: add code for the OSGi future
        return false;
    }

   public boolean isDown(){
       synchronized (down) {
           return down;
       }
   }

    @Override
    public void run(){
        while (!down) {
            synchronized (toShutdown) {
                if (toShutdown  ) {

                    try {
                        client.disconnect();
                    } catch (MqttException e) {
                        LoggerHandler.report("error", e);
                    }

                    try {
                        client.close();
                    } catch (MqttException e) {
                        LoggerHandler.report("error", e);
                    }


                    for (DataFusionWrapper i : dataFusionWrappers.values())
                        i.destroy();

                    LoggerHandler.report("info",this.getClass().getSimpleName()+" logged off");

                    synchronized (down) {
                        down = true;
                    }

                }
            }
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean suscribeToTopic(String topic) {
        try {
            if (!client.isConnected())
                client.connect();

            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

        LoggerHandler.report("info","message arrived with topic: "+topic);




        if (!topic.startsWith("/")) {
            String msg = new String(mqttMessage.getPayload(),"UTF-8");
            mangeDFMEvents(msg);

        }else{

            mangeEvents(mqttMessage.getPayload(),topic);
        }

    }
    private void mangeEvents(byte[] rawEvent, String topic){
        try {
            ObservationNumber event = mapper.readValue(rawEvent,ObservationNumber.class);

            LoggerHandler.report("info","message arrived with ID: "+event.getSensor().getId());
            if(event.getResultValue() == null) {
                Observation event1 = mapper.readValue(rawEvent,Observation.class);

                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event1, event1.getClass());
            }else
                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event, event.getClass());
        }catch(Exception e){
            e.printStackTrace();

        }

    }
    private void mangeDFMEvents(String rawEvent){
        try {

            Event event = new Event( parser.fromJson(rawEvent, IoTEntityEvent.class));


            if (event.getBaseName() != null) {
                if (/*topic.equals(DFM_QUERY_TOPIC)*/ event.getBaseName().equals("DataFusionManager")) {


                    try {
                        Statement query = new EsperQuery(event);

                        if (query != null) {
                            if (query.getStatement().toLowerCase().equals("shutdown")) {
                                synchronized (toShutdown) {
                                    toShutdown = true;
                                }
                            } else {

                                for (DataFusionWrapper i : dataFusionWrappers.values())
                                    i.addStatement(query);
                            }
                        }
                    } catch (Exception e) {
                        LoggerHandler.report("error", e);
                    }

                }
            } else {
                LoggerHandler.report("JsonParseWarning", "No IoTEvent received instead received :" + rawEvent, null);
            }
        }catch(JsonParseException e){

            LoggerHandler.report("JsonParseError", "No IoTEvent received instead received :" + rawEvent, e.getStackTrace().toString());


        }catch(Exception e){
            e.printStackTrace();

        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
