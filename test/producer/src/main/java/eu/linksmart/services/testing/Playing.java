package eu.linksmart.services.testing;

import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.*;

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
             client = new MqttClient("tcp://localhost:1883", UUID.randomUUID().toString(), new MemoryPersistence());
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
                           streamID.get(j)+String.valueOf(i),
                           new Date(),
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
