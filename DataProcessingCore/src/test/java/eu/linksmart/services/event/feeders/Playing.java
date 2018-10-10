package eu.linksmart.services.event.feeders;


import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.payloads.SenML.SenMLBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.*;

/**
 * Created by Jose Angel Carvajal on 09.03.2016 a researcher of Fraunhofer FIT.
 */
@Deprecated
public class Playing {
    public static void main(String [] arg){
        String st="{\n" +
                "    \"name\": \"test\" ,\n" +
                "    \"statement\": \"select event from Observation as event\",\n" +
                "    \"LSApiKeyName\": \"post2pub\",\n" +
                "    \"scope\":[\"appbackend\"],\n" +
                "    \"output\":[\"publish/event\"],\n" +
                "    \"publisher\":\"REST_POST\"\n" +
                "}";
        try {
            SharedSettings.getSerializerDeserializer().parse(st, ObservationImpl.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
