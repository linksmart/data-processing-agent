package eu.alamanac.event.datafusion.core;

import com.google.gson.Gson;
import eu.alamanac.event.datafusion.esper.EsperEngine;
import eu.alamanac.event.datafusion.feeder.EventFeederImpl;
import eu.alamanac.event.datafusion.logging.LoggerHandler;
import eu.linksmart.api.event.datafusion.EventFeeder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManager {
   // static String brokerURL = "tcp://localhost:1883";

    public static void main(String[] args) {
        if(args.length>=1) {
            System.out.println(args[0]);
            LoggerHandler.BROKER = args[0];
        }
        EventFeeder feeder = null;
        try {
             feeder = new EventFeederImpl(LoggerHandler.BROKER);

            feeder.dataFusionWrapperSignIn(new EsperEngine());

        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (feeder.isDown()){
            if (i == 0)
            LoggerHandler.publish("info","Data Fusion Manager is alive",null,false);
            i = (i+1)%6;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
