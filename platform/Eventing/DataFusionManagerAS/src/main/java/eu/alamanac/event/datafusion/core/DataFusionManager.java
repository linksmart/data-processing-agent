package eu.alamanac.event.datafusion.core;

import com.google.gson.Gson;
import eu.alamanac.event.datafusion.esper.EsperEngine;
import eu.alamanac.event.datafusion.feeder.EventFeederImpl;
import eu.linksmart.api.event.datafusion.EventFeeder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManager {
    static MqttClient errorReporter;
    static String brokerURL = "tcp://localhost:1883";
    public static void main(String[] args) {
        if(args.length==1)
            brokerURL = args[0];

        try {
            errorReporter = new MqttClient(brokerURL,"errorReporter");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        try {
            EventFeeder feeder = new EventFeederImpl(brokerURL);

            feeder.dataFusionWrapperSignIn(new EsperEngine());

        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void reportError(Map error){
            try {
                if(!errorReporter.isConnected())
                    errorReporter.connect();
                errorReporter.publish("/alamanac/error/json/dataFusionManager/"+error.get("ErrorTopic"),new Gson().toJson(error).getBytes(),0,false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
    }
    public static void reportError(String errorTopic, String message, String trace ){
        HashMap<String,String> error = new HashMap<String, String>();
        error.put("ErrorTopic",errorTopic);
        error.put("Message",message);

        if (trace!=null)
            error.put("ErrorTrace",trace);

        reportError(error);
    }
}
