package eu.alamanac.event.datafusion.logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Caravajal on 16.10.2014.
 */
public class LoggerHandler {
    public static final String LOG_TOPIC ="/eu/alamanac/event/datafusion/";
    public static String BROKER ="tcp://localhost:1883";
    public static void report(Map<String,String> info){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
       System.out.println(gson.toJson(info));
    }
    public static void report(String source, String text){
        System.out.println(text);
    }

    public static void report(String source, Exception error){
        error.printStackTrace();
    }
    public static void report(String topic, String message, String trace ){
        HashMap<String,String> error = new HashMap<String, String>();
        error.put("Topic",topic);
        error.put("Message",message);

        if (trace!=null)
            error.put("Source",trace);

        report(error);
    }
    public static void publish(Map<String,String> info){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        boolean published =false;
        MqttClient client =null;
        try {
             client = new MqttClient(BROKER,"LoggerHandler");
            client.connect();
            String subTopic = info.get("Topic");
            info.remove("Topic");

            client.publish(LOG_TOPIC+subTopic,gson.toJson(info).getBytes(),0,false);

            published =true;

        } catch (MqttException e) {
            report(info);
            e.printStackTrace();

        }

        try {
            if(published){
                client.disconnect();
                client.close();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    public static void publish(String errorTopic, String message, String trace ){
        HashMap<String,String> error = new HashMap<String, String>();
        error.put("Topic",errorTopic);
        error.put("Message",message);

        if (trace!=null)
            error.put("Source",trace);

        publish(error);
    }
}