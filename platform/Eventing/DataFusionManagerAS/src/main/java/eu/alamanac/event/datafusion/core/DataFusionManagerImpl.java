package eu.alamanac.event.datafusion.core;

import eu.alamanac.event.datafusion.esper.EsperEngine;
import eu.alamanac.event.datafusion.feeder.EventFeederImpl;
import eu.linksmart.api.event.datafusion.EventFeeder;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class DataFusionManagerImpl {
    public static void main(String[] args) {

        try {
            EventFeeder feeder = new EventFeederImpl();

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
}
