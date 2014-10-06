package it.ismb.pertlab.pwal.mqtt.subscriber;

import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.linksmart.cnet.jaxb.IoTEntity;
import it.ismb.pertlab.pwal.linksmart.cnet.topics.AlmanacTopics;
import it.ismb.pertlab.pwal.linksmart.cnet.utilities.IoTEntityFactory;
import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;

public class PWALMqttNewDeviceAddedEventSubscriber extends
        PWALEventSubsciber<PWALNewDeviceAddedEvent>
{
    private Logger log = LoggerFactory
            .getLogger(PWALMqttNewDeviceAddedEventSubscriber.class);
    private IoTEntityFactory iotEntityFactory = new IoTEntityFactory();
    // private PWALXmlMapper xmlMapper = new PWALXmlMapper();
    private MqttAsyncDispatcher mqttDispatcher;

    public PWALMqttNewDeviceAddedEventSubscriber(
            MqttAsyncDispatcher mqttDispatcher)
    {
        this.mqttDispatcher = mqttDispatcher;
    }

    @Override
    public void onEvent(Event<PWALNewDeviceAddedEvent> event) throws Exception
    {
        log.debug("########## NEW DEVICE ADDED EVENT ##########");
        log.debug("Received NewDeviceAdded event from {}.", event.getSource()
                .getSenderId());
        log.debug("Event topic is: {}", event.getTopic());

        IoTEntity toSend = iotEntityFactory.device2IoTEntity(event.getSource()
                .getSender());
        // this.xmlMapper.toXml(IoTEntity.class, toSend);
        if (toSend != null)
        {
            String topic = AlmanacTopics.createAlmanacTopic("metadata",
                    "iotentity", event.getSource().getSender().getPwalId(), "");
            log.debug("Publishing new device added event on mqtt topic: {}",
                    topic);
            this.mqttDispatcher.publish(topic, PWALJsonMapper.obj2json(toSend)
                    .getBytes());
        }
    }

}
