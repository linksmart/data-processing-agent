package testing;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by José Ángel Carvajal on 10.03.2016 a researcher of Fraunhofer FIT.
 */
public class Consumer extends Counter implements MqttCallback {

    static {
        cleaner = new Thread(new Consumer.Cleaner());
        cleaner.start();
    }
    public Consumer(int n, String broker)
    {
        super();
        this.broker = broker;
        if (n>0)
            id++;

        try {

            create();
            mqttClient.subscribe("/#", 0);
            mqttClient.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        synchronized (object) {
            i++;
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }


}
