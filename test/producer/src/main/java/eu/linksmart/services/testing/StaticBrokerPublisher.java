package eu.linksmart.services.testing;

import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.testing.tooling.Publisher;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 24.11.2016 a researcher of Fraunhofer FIT.
 */
public class StaticBrokerPublisher implements Publisher {
    protected StaticBroker client;
    public StaticBrokerPublisher(boolean validation) {
        try {
            client = new StaticBroker("testing","tester","/will");
        } catch (MalformedURLException | MqttException e) {
            e.printStackTrace();
        }


    }



    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retain) throws Exception {

        client.publish(topic,payload,qos,retain);
    }

    @Override
    public void disconnect() {
        try {
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        try {
            client.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
