package it.ismb.pertlab.pwal.mqtt.subscriber;

import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;

import it.ismb.pertlab.pwal.api.utils.FQDNUtils;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.EventModified;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.topics.AlmanacTopics;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.utilities.EventFactory;
import it.ismb.pertlab.pwal.event.format.ogc.sensorthings.api.OGCSensorThingsAPIPayloadFactory;

import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import java.io.IOException;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;

public class PWALMqttNewDataAvailableEventSubscriber extends PWALEventSubsciber<PWALNewDataAvailableEvent>
{
	private Logger log = LoggerFactory.getLogger(PWALMqttNewDataAvailableEventSubscriber.class);
	private EventFactory eventFactory = new EventFactory();
	private MqttAsyncDispatcher mqttDispatcher;
	
	// the fully qualified domain name of the platform running this pusher
	// instance
	// TODO: spaghetti code here!!! the overall structure should be re-written.
	private String platformFQDN;
	
	public PWALMqttNewDataAvailableEventSubscriber(MqttAsyncDispatcher mqttDispatcher, String platformFQDN)
	{
		this.mqttDispatcher = mqttDispatcher;
		
		// store the platform FQDN name
		this.platformFQDN = platformFQDN;
	}
	
	public void onEvent(Event<PWALNewDataAvailableEvent> event)
	{
		try
		{
		// -------------------- OLD implementation kept for compatibility
		// purposes ----------
		
		log.debug("########## NEW DATA AVAILABLE EVENT ##########");
		log.debug("Received NewDataAvailable event from {}.", event.getSource().getSenderId());
		log.debug("Event topic is: {}", event.getTopic());
		
		EventModified toSend;
		
			toSend = this.eventFactory.createEvent(event.getSource(), "OBSERVATION");
			
			if (toSend != null)
			{
				String topic = AlmanacTopics.createAlmanacTopic("observation", "iotentity", toSend.getAbout(), "");
				log.debug("Publishing new data available event on mqtt topic: {}", topic);
				log.info(PWALJsonMapper.obj2json(toSend));
				this.mqttDispatcher.publish(topic, PWALJsonMapper.obj2json(toSend).getBytes());
			}
			
			// -------------------- New implementation
			// ------------------------------------------
			
			// the OGC payload factory
			OGCSensorThingsAPIPayloadFactory payloadFactory = OGCSensorThingsAPIPayloadFactory.getInstance();
			
			// give the event extract the list of observations to post
			Set<Observation> observations = payloadFactory.getObservationDataPayload(event.getSource(),
					this.platformFQDN);
			
			// give the event extract the list of observations to post
			Thing thing = payloadFactory.getSensorMetadataPayload(event.getSource(), this.platformFQDN);
			
			// for each observation generate a new mqtt submission
			
			// convert FQDN into topic notation
			String topic = FQDNUtils.FQDN2Topic(this.platformFQDN,1,2);
			topic += "/v2/observation"; // TODO: remove hardcoding from here, a nice
								// place
								// for such parameters shall be found
			
			// dispatch the observations
			for (Observation observation : observations)
			{
				// build the json payload
				String payload = PWALJsonMapper.obj2json(observation);
				
				//build the full topic
				String fullTopic = topic+"/"+thing.getId()+"/"+observation.getDatastream().getId();
				
				// debug
				log.info(payload);
				
				// send the data over MQTT
				this.mqttDispatcher.publish(fullTopic, payload.getBytes());
			}
		}
		catch (DatatypeConfigurationException | JAXBException | IOException  e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
