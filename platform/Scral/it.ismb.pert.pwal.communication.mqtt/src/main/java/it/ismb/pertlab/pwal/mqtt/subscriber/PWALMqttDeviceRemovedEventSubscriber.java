package it.ismb.pertlab.pwal.mqtt.subscriber;

import it.ismb.pertlab.pwal.api.events.base.PWALDeviceRemovedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;

import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTEntity;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.topics.AlmanacTopics;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.utilities.IoTEntityFactory;

import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;

public class PWALMqttDeviceRemovedEventSubscriber extends PWALEventSubsciber<PWALDeviceRemovedEvent>
{

private Logger log = LoggerFactory.getLogger(PWALMqttDeviceRemovedEventSubscriber.class);
private MqttAsyncDispatcher mqttDispatcher;
private IoTEntityFactory iotEntityFactory;

// the fully qualified domain name of the platform running this pusher
// instance
// TODO: spaghetti code here!!! the overall structure should be re-written.
private String platformFQDN;

    public PWALMqttDeviceRemovedEventSubscriber(
            MqttAsyncDispatcher mqttDispatcher)
    {
        this.mqttDispatcher = mqttDispatcher;
        this.iotEntityFactory = new IoTEntityFactory();
    }

	public PWALMqttDeviceRemovedEventSubscriber(MqttAsyncDispatcher mqttDispatcher, String platformFQDN)
	{
		this.mqttDispatcher = mqttDispatcher;
		this.iotEntityFactory = new IoTEntityFactory();
		
		// store the platform FQDN name
		this.platformFQDN = platformFQDN;
	}
	
	public void onEvent(Event<PWALDeviceRemovedEvent> event) throws Exception
	{
		log.debug("########## DEVICE REMOVED EVENT ##########");
		log.debug("Received DeviceRemoved event from {}.", event.getSource().getSenderId());
		log.debug("Event topic is: {}", event.getTopic());
		
		IoTEntity toRemove = iotEntityFactory.device2IoTEntity(event.getSource().getSender());
		// this.xmlMapper.toXml(IoTEntity.class, toSend);
		if (toRemove != null)
		{
			String topic = AlmanacTopics.createAlmanacTopic("scral", "iotentity", "removed", event.getSource()
					.getSender().getPwalId());
			log.debug("Publishing device removed event on mqtt topic: {}", topic);
			log.info(PWALJsonMapper.obj2json(toRemove));
			this.mqttDispatcher.publish(topic, PWALJsonMapper.obj2json(toRemove).getBytes());
		}
	}
}
