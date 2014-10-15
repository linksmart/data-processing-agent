package eu.alamanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.alamanac.event.datafusion.core.DataFusionManager;
import eu.alamanac.event.datafusion.esper.EsperQuery;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Caravajal on 06.10.2014.
 */
public  class EventFeederImpl implements EventFeeder, EventFeederLogic, MqttCallback {
    private MqttClient client;
    private Gson parser;
    private Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<String, DataFusionWrapper>();
    private String BROKER_URL;
    private final String DFM_QUERY_TOPIC = "/almanac/dataFusionManager/observation/iotentity";
    private final String EVENT_TOPIC = "/almanac/observation/#";
    private final String ERROR_TOPIC = "/almanac/error/json/dataFusionManager";
    private final String INFO_TOPIC = "/almanac/info/json/dataFusionManager";


    public EventFeederImpl(String broker){
        BROKER_URL = broker;
        try {
           //            client = new MqttClient("tcp://130.192.86.227:1883","EsperStandalone3");
            client = new MqttClient(BROKER_URL, String.valueOf((new Random()).nextDouble()));
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

        System.out.println(topic);
        String msg = new String(mqttMessage.getPayload(),"UTF-8");


        IoTEntityEvent event =null;

        try {

            event = parser.fromJson(msg, IoTEntityEvent.class);

            if (topic.equals(DFM_QUERY_TOPIC)) {


                try {
                    Statement query = new EsperQuery(event);

                    if (query != null) {
                        for (DataFusionWrapper i : dataFusionWrappers.values())
                            i.addStatement(query);
                    }
                }catch (Exception e){}

            } else {

                for (DataFusionWrapper i: dataFusionWrappers.values())
                    i.addEvent(topic, event);
                // addEvent(topic,je.getProperties()[0].toMap());
            }
        }catch (JsonParseException e) {

            DataFusionManager.reportError("JsonParseException", "No IoTEvent received instead received :"+msg, e.getStackTrace().toString());


            return;

        }catch (Exception e){
            e.printStackTrace();

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
