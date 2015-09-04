package it.ismb.pertlab.pwal.mqtt.subscriber;

import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;

import it.ismb.pertlab.pwal.api.utils.FQDNUtils;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTEntity;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.topics.AlmanacTopics;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.utilities.IoTEntityFactory;
import it.ismb.pertlab.pwal.event.format.ogc.sensorthings.api.OGCSensorThingsAPIPayloadFactory;

import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;

public class PWALMqttNewDeviceAddedEventSubscriber extends PWALEventSubsciber<PWALNewDeviceAddedEvent>
{
	private Logger log = LoggerFactory.getLogger(PWALMqttNewDeviceAddedEventSubscriber.class);
	private IoTEntityFactory iotEntityFactory = new IoTEntityFactory();
	// private PWALXmlMapper xmlMapper = new PWALXmlMapper();
	private MqttAsyncDispatcher mqttDispatcher;
	
	// the fully qualified domain name of the platform running this pusher
	// instance
	// TODO: spaghetti code here!!! the overall structure should be re-written.
	private String platformFQDN;
	
	public PWALMqttNewDeviceAddedEventSubscriber(MqttAsyncDispatcher mqttDispatcher, String platformFQDN)
	{
		this.mqttDispatcher = mqttDispatcher;
		
		// store the platform FQDN name
		this.platformFQDN = platformFQDN;
	}
	
	@Override
	public void onEvent(Event<PWALNewDeviceAddedEvent> event) throws Exception
	{
		// -------------------- OLD implementation kept for compatibility
		// purposes ----------
		log.debug("########## NEW DEVICE ADDED EVENT ##########");
		log.debug("Received NewDeviceAdded event from {}.", event.getSource().getSenderId());
		log.debug("Event topic is: {}", event.getTopic());
		
		IoTEntity toSend = iotEntityFactory.device2IoTEntity(event.getSource().getSender());
		// this.xmlMapper.toXml(IoTEntity.class, toSend);
		if (toSend != null)
		{
			String topic = AlmanacTopics.createAlmanacTopic("metadata", "iotentity", toSend.getAbout(), "");
			log.debug("Publishing new device added event on mqtt topic: {}", topic);
			log.info(PWALJsonMapper.obj2json(toSend));
			this.mqttDispatcher.publish(topic, PWALJsonMapper.obj2json(toSend).getBytes());
		}
		
		// -------------------- New implementation
		// ------------------------------------------
		
		// the OGC payload factory
		OGCSensorThingsAPIPayloadFactory payloadFactory = OGCSensorThingsAPIPayloadFactory.getInstance();
		
		// give the event extract the list of observations to post
		Thing thing = payloadFactory.getSensorMetadataPayload(event.getSource(), this.platformFQDN);
		
		// for each observation generate a new mqtt submission
		
		// convert FQDN into topic notation
		String topic = FQDNUtils.FQDN2Topic(this.platformFQDN,1,2);
		topic += "/v2/metadata/"; // TODO: remove hardcoding from here, a nice place
							// for such parameters shall be found
		
		//build the json payload
		String payload = PWALJsonMapper.obj2json(thing);
		
		//debug
		log.info(payload);
		
		//send the data over MQTT
		this.mqttDispatcher.publish(topic+thing.getId(), payload.getBytes());
		
	}

}
