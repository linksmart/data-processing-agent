package eu.alamanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.alamanac.event.datafusion.core.DataFusionManager;
import eu.alamanac.event.datafusion.esper.EsperQuery;
import eu.alamanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Caravajal on 06.10.2014.
 */
public  class EventFeederImpl extends Thread implements EventFeeder, EventFeederLogic, MqttCallback {
    private MqttClient client;
    private Gson parser;
    private Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<String, DataFusionWrapper>();
    private String BROKER_URL;
    private final String DFM_QUERY_TOPIC = "/almanac/observation/iotentity/dataFusionManager";
    private final String EVENT_TOPIC = "/almanac/observation/#";
    private final String ERROR_TOPIC = "/almanac/error/json/dataFusionManager";
    private final String INFO_TOPIC = "/almanac/info/json/dataFusionManager";
    private Boolean toShutdown = false;

    private Boolean down =false;

    public EventFeederImpl(String broker){
        BROKER_URL = broker;

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        String clientid =nowAsISO+"-"+String.valueOf((new Random()).nextDouble());
        try {

           //            client = new MqttClient("tcp://130.192.86.227:1883","EsperStandalone3");


            client = new MqttClient(BROKER_URL, clientid);


        } catch (MqttException e) {
            e.printStackTrace();
        }

        parser = new Gson();

        try {
            client.setCallback(this);
            client.connect();
            //client.subscribe(DFM_QUERY_TOPIC);


            client.subscribe(EVENT_TOPIC);

        } catch (MqttException e) {
            e.printStackTrace();
        }

        start();

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

        String msg = new String(mqttMessage.getPayload(),"UTF-8");


        IoTEntityEvent event =null;

        try {

            event = parser.fromJson(msg, IoTEntityEvent.class);

            if (topic.equals(DFM_QUERY_TOPIC)) {


                try {
                    Statement query = new EsperQuery(event);

                    if (query != null) {
                        if (query.getStatement().toLowerCase().equals("shutdown")){
                            synchronized (toShutdown) {
                                toShutdown = true;
                            }
                        }else {

                            for (DataFusionWrapper i : dataFusionWrappers.values())
                                i.addStatement(query);
                        }
                    }
                }catch (Exception e){
                    LoggerHandler.report("error",e);
                }

            } else {

                for (DataFusionWrapper i: dataFusionWrappers.values())
                    i.addEvent(topic, event);
                // addEvent(topic,je.getProperties()[0].toMap());
            }
        }catch (JsonParseException e) {

            LoggerHandler.report("JsonParseException", "No IoTEvent received instead received :" + msg, e.getStackTrace().toString());


            return;

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
