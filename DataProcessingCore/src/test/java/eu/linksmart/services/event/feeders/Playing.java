package eu.linksmart.services.event.feeders;


import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
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
        while (true){
            try {
                Observation observation = (Observation) (new OGCEventBuilder()).factory(
                        uuid.toString(),
                        uuid.toString(),
                        random.nextInt(100)+1,
                        new Date(),
                        new HashMap<>()

                );
                //deserializer.deserialize(serializer.serialize(observation),ObservationImpl.class);
                client.publish("LS/test/1/OGC/1.0/Datastreams/1",serializer.serialize(observation),0,false);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
