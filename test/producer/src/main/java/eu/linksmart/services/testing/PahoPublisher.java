package eu.linksmart.services.testing;

import eu.linksmart.testing.tooling.Publisher;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 24.11.2016 a researcher of Fraunhofer FIT.
 */
public class PahoPublisher implements Publisher {
    protected MqttClient client;
    MqttConnectOptions options;
    public PahoPublisher(String broker, boolean validation) {
            try {
                this.client = new MqttClient("tcp://"+broker+":1883", UUID.randomUUID().toString(), new MemoryPersistence());

                MqttConnectOptions options = new MqttConnectOptions();
                options.setMaxInflight(10000);
                options.setCleanSession(false);
                client.connect(options);

            } catch (MqttException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retain) throws Exception {
        synchronized (this){
            while (!client.isConnected())
                client.connect(options);
        }
        client.publish(topic,payload,qos,retain);
    }

    @Override
    public synchronized void  disconnect() {
        try {

            while (client.isConnected()) {
                client.disconnect();
                Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void close() {
        try {
            client.close();

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
