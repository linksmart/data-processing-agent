package eu.linksmart.testing.unit.backbone.protocol.mqtt;

import eu.linksmart.gc.network.backbone.protocol.mqtt.ForwardingListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by Caravajal on 27.03.2015.
 */
public class MqttBBTest {
    private Logger mlogger = Logger.getLogger(MqttBBTest.class.getName());

    boolean finished = false;
    MqttClient cl = null;
    //@Test
    public void testSomething() {
        //  assertTrue(true);
        try {
            cl = new MqttClient("tcp://localhost:1883", "test");
            cl.connect();
            cl.subscribe("test");


        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new ForwardingListener("localhost","1883", "test", UUID.randomUUID(), new Observer(){
                @Override
                public void update(Observable o, Object arg) {

                        finished = true;

                }
            }
            );
        } catch (MqttException e) {
            e.printStackTrace();
        }

        do {
            try {

                cl.publish("test","test".getBytes(),0,false);

                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (!finished);
        try {
            cl.disconnect();
            cl.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
          assertTrue(true);

    }


}
