package eu.linksmart.services.event.connectors.Observers;

import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.Deserializer;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.feeder.EventFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import sun.security.pkcs.ParsingException;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {


    public EventMqttObserver(StaticBroker broker) {
        super(broker);
    }

    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {
            EventFeeder.feed(topic,rawEvent);
        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }


    }


}
