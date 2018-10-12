package eu.linksmart.services.testing;

import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.Instant;
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
             client = new MqttClient("tcp://magna:1883", UUID.randomUUID().toString(), new MemoryPersistence());
             client.connect();

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int[] ids= new int[]{0,1,2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 15, 16, 17, 18, 19, 12, 55};
        int i=0,n=0;
        Map<String, Object> aux= new HashMap<>();
        while (true){
            try {
                Long before = (new Date()).getTime();
                for(int j=0; j<32;j++){
                    for (int id : ids)
                        for (int k = 1; k < 4; k++,i++,n++) {
                            EventEnvelope observation = (new OGCEventBuilder()).factory(
                                    String.valueOf(id),
                                    "ds_" + String.valueOf(k) + "-" + String.valueOf(id),
                                     "_" + String.valueOf(i)+"_"+ String.valueOf(n++)+"___" + "_ds_" + String.valueOf(k) + "-" + String.valueOf(id),
                                    new Date(),
                                    null,
                                    aux

                            );

                            //deserializer.deserialize(serializer.serialize(observation),ObservationImpl.class);
                            client.publish(observation.getClassTopic() + observation.getAttributeId(), serializer.serialize(observation), 0, false);
                        }

                    i = 0;
                }
                n=0;
                System.out.println("Took: " + String.valueOf(((new Date()).getTime() - before) / 1000.0) + " sec");
                Thread.sleep(6000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
