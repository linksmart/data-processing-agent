package eu.linksmart.services;

import de.fraunhofer.fit.event.ceml.api.MqttCemlAPI;
import eu.almanac.event.datafusion.core.DataFusionManagerCore;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {
    public static void main(String[] args) {

        try {
            DataFusionManagerCore.start(args);
            MqttCemlAPI api = new MqttCemlAPI();

        } catch (Exception e) {
            e.printStackTrace();
        }
        while (DataFusionManagerCore.isActive()){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
}
