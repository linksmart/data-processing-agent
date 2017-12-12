package eu.linksmart.services;

import eu.linksmart.utility.mqtt.RestInit;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {


    public static void main(String[] args) {


        RestInit.init();
        while (true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }

}
