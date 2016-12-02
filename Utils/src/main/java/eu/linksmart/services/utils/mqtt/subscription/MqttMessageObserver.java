package eu.linksmart.services.utils.mqtt.subscription;

import eu.linksmart.services.utils.mqtt.types.MqttMessage;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 02.12.2016 a researcher of Fraunhofer FIT.
 */
public interface MqttMessageObserver extends Observer {
    @Override
    public default void update(Observable o, Object arg){
        update(((MqttMessage)arg).getTopic(),(MqttMessage)arg);
    }
    public void update(String topic, MqttMessage message);
}
