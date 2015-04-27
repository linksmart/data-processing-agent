package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.types.MqttTunnelledMessage;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by Caravajal on 27.04.2015.
 */
public class MessageDeliverer extends Thread {
    private MqttTunnelledMessage mqttMessage;
    private Observer observer;
    private Observable fwListener;
    public  MessageDeliverer(MqttTunnelledMessage message, Observer observer, Observable fwListener){
        super();
        this.mqttMessage = message;
        this.observer = observer;
        this.fwListener = fwListener;

    }
    @Override
    public void run() {
        observer.update(fwListener, new MqttTunnelledMessage(mqttMessage.getTopic(),mqttMessage.getPayload(),mqttMessage.getQoS(),mqttMessage.isRetained(),mqttMessage.getSequence(),mqttMessage.getOriginProtocol()));
    }

}
