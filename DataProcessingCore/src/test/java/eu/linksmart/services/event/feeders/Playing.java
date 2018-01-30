package eu.linksmart.services.event.feeders;


import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
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

import java.util.*;

/**
 * Created by José Ángel Carvajal on 09.03.2016 a researcher of Fraunhofer FIT.
 */
@Deprecated
public class Playing {
    public static void main(String [] arg){

        UUID uuid = UUID.randomUUID();
        MqttClient client;
        Random random = new Random();
        Serializer serializer = new DefaultSerializer();
        Deserializer deserializer = new DefaultDeserializer();


        try {
             client = new MqttClient("tcp://localhost:1883",uuid.toString(), new MemoryPersistence());
             client.connect();

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int i=0;
        while (true){
            try {
                EventEnvelope observation = (EventEnvelope) (new SenMLBuilder()).factory(
                        uuid.toString(),
                        uuid.toString(),
                        i++,
                        new Date(),
                        new HashMap<>()

                );
                //deserializer.deserialize(serializer.serialize(observation),ObservationImpl.class);
                client.publish("LS/test/1/SenML/10/Event/1",serializer.serialize(observation),0,false);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
