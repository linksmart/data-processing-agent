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
 * Created by Jose Angel Carvajal on 09.03.2016 a researcher of Fraunhofer FIT.
 */
@Deprecated
public class Playing {
    public static void main(String [] arg){

        List<String> sensorID = new ArrayList<>(), streamID = new ArrayList<>();
        MqttClient client;
        Random random = new Random();
        Serializer serializer = new DefaultSerializer();
        Deserializer deserializer = new DefaultDeserializer();
        for(int i =0; i<60; i++) {
            sensorID.add("S"+String.valueOf(i));
            streamID.add("D"+String.valueOf(i));
        }

        try {
             client = new MqttClient("tcp://magna:1883",UUID.randomUUID().toString(), new MemoryPersistence());
             client.connect();

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int i=0;
        Map<String, Object> aux= new HashMap<>();
        while (true){
            try {
               for (int j=0; j<60;j++){
                   EventEnvelope observation = (new OGCEventBuilder()).factory(
                           sensorID.get(j),
                           streamID.get(j),
                           i,
                           new Date(),
                           null,
                           aux

                   );
                   //deserializer.deserialize(serializer.serialize(observation),ObservationImpl.class);
                   client.publish(observation.getClassTopic()+observation.getAttributeId(),serializer.serialize(observation),0,false);
               }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }


    }

}
