package it.ismb.pertlab.pwal.pusher.mqtt;

import it.ismb.pertlab.pwal.api.events.base.PWALDeviceRemovedEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;
import it.ismb.pertlab.pwal.mqtt.subscriber.PWALMqttDeviceRemovedEventSubscriber;
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
    
    //the fully qualified domain name of the platform running this pusher instance
    //TODO: spaghetti code here!!! the overall structure should be re-written.
    private String platformFQDN;
    
    private Logger log = LoggerFactory.getLogger(MqttPusher.class);

    public MqttPusher(String mqttBrokerEndpoint, String clientId, String platformFQDN)
    {
        this.mqttBrokerEndpoint = mqttBrokerEndpoint;
        
        //store the platform FQDN
        this.platformFQDN = platformFQDN;
        
        this.mqttDispatcher = new MqttAsyncDispatcher(this.mqttBrokerEndpoint,
               clientId , "", "");
        connectToMqttBroker();
    }

    private void connectToMqttBroker()
    {
        log.debug("Connecting...");
        PWALEventDispatcher
                .getInstance()
                .getDispatcher()
                .subscribe(Topic.match(PWALTopicsUtility.createNewDataFromDeviceTopic("**", "**")),
                        PWALNewDataAvailableEvent.class,
                        new PWALMqttNewDataAvailableEventSubscriber(this.mqttDispatcher, this.platformFQDN));
        PWALEventDispatcher
                .getInstance()
                .getDispatcher()
                .subscribe(
                        Topic.match(PWALTopicsUtility.createNewDeviceAddedTopic("**")),
                        PWALNewDeviceAddedEvent.class,
                        new PWALMqttNewDeviceAddedEventSubscriber(this.mqttDispatcher, this.platformFQDN));
        PWALEventDispatcher
        .getInstance()
        .getDispatcher()
        .subscribe(
                Topic.match(PWALTopicsUtility.createDeviceRemovedTopic("**")),
                PWALDeviceRemovedEvent.class,
                new PWALMqttDeviceRemovedEventSubscriber(this.mqttDispatcher, this.platformFQDN));
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
