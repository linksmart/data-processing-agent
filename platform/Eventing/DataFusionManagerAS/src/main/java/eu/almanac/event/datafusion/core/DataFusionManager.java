package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.feeder.EventFeederImpl;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.linksmart.api.event.datafusion.EventFeeder;

import java.util.UUID;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManager {
    public static UUID ID = UUID.randomUUID();
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
