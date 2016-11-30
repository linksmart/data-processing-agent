package eu.linksmart.testing.tooling;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by José Ángel Carvajal on 29.11.2016 a researcher of Fraunhofer FIT.
 */
public interface Publisher {
    default public void publish(String topic, String payload, int qos, boolean retain) throws Exception {
     publish(topic,payload.getBytes(),qos,retain);
    }
    public void publish(String topic, byte[] payload, int qos, boolean retain) throws Exception ;

    public void  disconnect() ;

    public  void close();
}
