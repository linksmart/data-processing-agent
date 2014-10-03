package it.ismb.pertlab.pwal.pusher.mqtt;

import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;
import it.ismb.pertlab.pwal.mqtt.subscriber.PWALMqttNewDataAvailableEventSubscriber;
import it.ismb.pertlab.pwal.mqtt.subscriber.PWALMqttNewDeviceAddedEventSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Topic;

/**
 * Mqtt data and event pusher
 * 
 */
public class MqttPusher
{
    private String mqttBrokerEndpoint;
    private MqttAsyncDispatcher mqttDispatcher;
    private Logger log = LoggerFactory.getLogger(MqttPusher.class);

    public MqttPusher(String mqttBrokerEndpoint)
    {
        this.mqttBrokerEndpoint = mqttBrokerEndpoint;
        this.mqttDispatcher = new MqttAsyncDispatcher(this.mqttBrokerEndpoint,
                "pwal", "", "");
        connectToMqttBroker();
    }

    private void connectToMqttBroker()
    {
        log.debug("Connecting...");
        PWALEventDispatcher
                .getInstance()
                .getDispatcher()
                .subscribe(Topic.match("newdata/devices/**"),
                        PWALNewDataAvailableEvent.class,
                        new PWALMqttNewDataAvailableEventSubscriber(this.mqttDispatcher));
        PWALEventDispatcher
                .getInstance()
                .getDispatcher()
                .subscribe(
                        Topic.match(PWALTopicsUtility.newDeviceAddedTopic("**")),
                        PWALNewDeviceAddedEvent.class,
                        new PWALMqttNewDeviceAddedEventSubscriber(this.mqttDispatcher));
        this.mqttDispatcher.syncConnect();
    }

    @SuppressWarnings("unused")
    private void disconnectToMqttBroker()
    {
        log.debug("Disconnecting...");
    }
    
    public MqttAsyncDispatcher getDispatcher()
    {
        return this.mqttDispatcher;
    }
}
