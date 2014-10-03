package it.ismb.pertlab.pwal.mqtt.subscriber;

import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.linksmart.cnet.topics.AlmanacTopics;
import it.ismb.pertlab.pwal.linksmart.cnet.utilities.EventFactory;
import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;

public class PWALMqttNewDataAvailableEventSubscriber extends
        PWALEventSubsciber<PWALNewDataAvailableEvent>
{
    private Logger log = LoggerFactory
            .getLogger(PWALMqttNewDataAvailableEventSubscriber.class);
    private EventFactory eventFactory = new EventFactory();
    private MqttAsyncDispatcher mqttDispatcher;

    public PWALMqttNewDataAvailableEventSubscriber(
            MqttAsyncDispatcher mqttDispatcher)
    {
        this.mqttDispatcher = mqttDispatcher;
    }

    public void onEvent(Event<PWALNewDataAvailableEvent> event)
            throws Exception
    {
        log.debug("########## NEW DATA AVAILABLE EVENT ##########");
        log.debug("Received NewDataAvailable event from {}.", event.getSource()
                .getSenderId());
        log.debug("Event topic is: {}", event.getTopic());

        it.ismb.pertlab.pwal.linksmart.cnet.jaxb.Event toSend = this.eventFactory
                .createEvent(event.getSource(), "OBSERVATION");
        if (toSend != null)
        {
            String topic = AlmanacTopics.createAlmanacTopic("observation",
                    "iotentity", event.getSource().getSender().getPwalId(), "");
            log.debug("Publishing new data available event on mqtt topic: {}",
                    topic);
            this.mqttDispatcher.publish(topic, PWALJsonMapper.obj2json(toSend)
                    .getBytes());
        }
    }
}
