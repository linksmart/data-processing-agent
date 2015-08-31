package eu.linksmart.gc.utils.mqtt.subscription;




import org.apache.log4j.Logger;

import java.util.Observable;


/**
 * Created by Caravajal on 27.04.2015.
 */
public class MessageDeliverer implements Runnable {
    private MqttMessage mqttMessage;
    protected final Logger LOG= Logger.getLogger(ForwardingListener.class.getName());
    private Observable fwListener;
    private   MessageDeliverer(MqttMessage message, Observable fwListener){
        super();
        this.mqttMessage = message;

        this.fwListener = fwListener;

    }
    @Override
    public void run() {

        try {

            fwListener.notifyObservers(new MqttMessage(mqttMessage.getTopic(),mqttMessage.getPayload(),mqttMessage.getQoS(),mqttMessage.isRetained(),mqttMessage.getSequence(),mqttMessage.getOriginProtocol()));
        }catch (Exception e){
           LOG.error(e.getMessage(),e);
        }

    }

    public static synchronized MessageDeliverer createMessageDeliverer(MqttMessage message, Observable fwListener){
        return new MessageDeliverer(message,fwListener);
    }

}
