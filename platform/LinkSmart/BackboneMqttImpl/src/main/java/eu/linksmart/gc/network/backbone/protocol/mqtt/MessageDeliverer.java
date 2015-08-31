package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.types.MqttTunnelledMessage;

import java.util.Observable;
import java.util.Observer;


/**
 * Created by Caravajal on 27.04.2015.
 */
public class MessageDeliverer implements Runnable {
    private MqttTunnelledMessage mqttMessage;
    private Observer observer;
    private Observable fwListener;
    private   MessageDeliverer(MqttTunnelledMessage message, Observer observer, Observable fwListener){
        super();
        this.mqttMessage = message;
        this.observer = observer;
        this.fwListener = fwListener;

    }
    @Override
    public void run() {

        try {

            observer.update(fwListener, new MqttTunnelledMessage(mqttMessage.getTopic(),mqttMessage.getPayload(),mqttMessage.getQoS(),mqttMessage.isRetained(),mqttMessage.getSequence(),mqttMessage.getOriginProtocol()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static synchronized MessageDeliverer createMessageDeliverer(MqttTunnelledMessage message, Observer observer, Observable fwListener){
        return new MessageDeliverer(message,observer,null);
    }

}
