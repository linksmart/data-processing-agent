package eu.linksmart.services.testing;

import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.Sensor;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.SensorImpl;
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
             client = new MqttClient("tcp://localhost:1883", UUID.randomUUID().toString(), new MemoryPersistence());
             client.connect();

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int[] readingID= new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 15, 16, 17, 18, 19, 12, 55};

        ArrayList<String> ids = new ArrayList<>(241);
        for(int i=0; i<readingID.length;i++)
            for(int j=1; j<3;j++)
                ids.add(j+"-"+readingID[i]);
        for (int i=40;i<235;i++)
            ids.add("6-"+i);
        for (int i=235;i<240;i++)
            ids.add("5-"+i);



        int i=0,n=0;
        Map<String, Object> aux= new HashMap<>();
        Sensor sensor = new SensorImpl();
        while (true){
            try {
                Long before = (new Date()).getTime();
                for(int j=0; j<32;j++) {
                    for (String id : ids) {
                        sensor.setId(id);
                        Observation observation = (Observation) (new OGCEventBuilder()).factory(
                                String.valueOf(id),
                                "ds_" + id,
                                 id+"__(" + j + ")",
                                new Date(),
                                null,
                                aux

                        );
                        observation.getDatastream().setSensor(sensor);

                        //deserializer.deserialize(serializer.serialize(observation),ObservationImpl.class);
                        client.publish(observation.getClassTopic().replace(".","_") + observation.getAttributeId(), serializer.serialize(observation), 0, false);
                        Thread.sleep(50);
                    }

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
