package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.feeder.EventFeeder;

import eu.almanac.event.datafusion.feeder.Feeder;
import eu.almanac.event.datafusion.feeder.QueryFeeder;
import eu.almanac.event.datafusion.intern.ConfigurationManagement;
import eu.almanac.event.datafusion.intern.LoggerService;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManager {

    public static void main(String[] args) {
        try {

            URI url =new URI("tcp://localhost:1883");
            ConfigurationManagement.BROKER_PORT = String.valueOf(url.getPort());
            ConfigurationManagement.BROKER_HOST = url.getHost();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        if(args.length>=1) {

            try {

                URI url =new URI(args[0]);
                ConfigurationManagement.BROKER_PORT = String.valueOf(url.getPort());
                ConfigurationManagement.BROKER_HOST = url.getHost();

            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }
        }
        if(args.length>=2){
            ConfigurationManagement.EVENT_TOPIC = args[1];
        }

        System.out.println("The Data-Fusion Manager is starting with ID:" );
        Feeder feederEvents = null,  feederQuery = null;
        try {
            feederEvents = new EventFeeder(ConfigurationManagement.BROKER_HOST, ConfigurationManagement.BROKER_PORT,ConfigurationManagement.EVENT_TOPIC);

            EsperEngine esper = new EsperEngine();

            feederEvents.dataFusionWrapperSignIn(esper);


            feederQuery = new QueryFeeder(ConfigurationManagement.BROKER_HOST, ConfigurationManagement.BROKER_PORT,ConfigurationManagement.STATEMENT_ADD_TOPIC);


            feederQuery.dataFusionWrapperSignIn(esper);

        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = 0;
        while (!feederEvents.isDown() || !feederQuery.isDown()){
            if (i == 0)
            LoggerService.publish("info", "Data Fusion Manager is alive", null, false);
            i = (i+1)%6;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
