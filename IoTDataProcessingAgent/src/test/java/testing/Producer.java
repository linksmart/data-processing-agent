package testing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.almanac.ogc.sensorthing.api.datamodel.Datastream;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.almanac.ogc.sensorthing.api.datamodel.Sensor;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 10.03.2016 a researcher of Fraunhofer FIT.
 */
public class Producer extends Counter implements Runnable{

    static {
        cleaner = new Thread(new Producer.Cleaner());
        cleaner.start();
    }
    final String topic;
    public Producer(int n, String topic, String broker) {
        super();
        if (n>0)
            id++;
        this.topic=topic;
        this.broker = broker;

    }




    @Override
    public void run() {
        try {
            create();
            String sid =String.valueOf(id);

            Sensor sen = new Sensor();
            sen.setId(sid);
            sen.setObservations(null);
            Datastream ds = new Datastream();
            ds.setObservations(null);
            ds.setId(sid);
            Observation ob = new Observation();
            ob.setDatastream(ds);
            ob.setSensor(sen);
            ob.setPhenomenonTime(new Date());
            ob.setResultType("simulation");
            ob.setResultValue(1);
            ob.setFeatureOfInterest(null);


            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            byte[] bytes = mapper.writeValueAsBytes(ob);


            while (true)
                try {
                    mqttClient.publish(topic+sid+"/"+sid,bytes,0,false);

                    synchronized (object) {
                        i ++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
