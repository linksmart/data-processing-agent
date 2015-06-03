package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.feeder.EventFeeder;

import eu.almanac.event.datafusion.feeder.Feeder;
import eu.almanac.event.datafusion.feeder.QueryFeeder;
import eu.almanac.event.datafusion.logging.LoggerHandler;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManager {
    public static UUID ID = UUID.randomUUID();
   // static String brokerURL = "tcp://localhost:1883";
    public static void main(String[] args) {
        String eventTopic = "/+/+/v2/observation/#";
        if(args.length>=1) {
            System.out.println(args[0]);
            //LoggerHandler.BROKER = args[0];
            try {

                URI url =new URI(args[0]);
                LoggerHandler.BROKER_PORT = String.valueOf(url.getPort());
                LoggerHandler.BROKER_HOST = url.getHost();

            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
        }
        if(args.length>=2){
            eventTopic = args[1];
        }
        Feeder feederEvents = null,  feederQuery = null;
        try {
            feederEvents = new EventFeeder(LoggerHandler.BROKER_HOST,LoggerHandler.BROKER_PORT,eventTopic);

            EsperEngine esper = new EsperEngine();

            feederEvents.dataFusionWrapperSignIn(esper);


            feederQuery = new QueryFeeder(LoggerHandler.BROKER_HOST,LoggerHandler.BROKER_PORT,"queries/add");


            feederQuery.dataFusionWrapperSignIn(esper);

        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (!feederEvents.isDown() || !feederQuery.isDown()){
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
