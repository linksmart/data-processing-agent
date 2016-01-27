package eu.linksmart.gc.utils.mqtt.subscription;




import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.types.MqttMessage;
import org.slf4j.LoggerFactory;

import java.util.Observable;


/**
 * Created by Caravajal on 27.04.2015.
 */
public class MessageDeliverer extends Observable implements Runnable {
    public synchronized MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public synchronized void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }

    private MqttMessage mqttMessage;
    protected static final LoggerService LOG= new LoggerService(LoggerFactory.getLogger(ForwardingListener.class));


    @Override
    public void run() {

        try {
            setChanged();
           notifyObservers(mqttMessage);


        }catch (Exception e){
           LOG.error(e.getMessage(),e);
        }

    }



}
