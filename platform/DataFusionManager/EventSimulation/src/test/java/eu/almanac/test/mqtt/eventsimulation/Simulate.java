package eu.almanac.test.mqtt.eventsimulation;





/**
 * Created by José Ángel Carvajal on 30.10.2015 a researcher of Fraunhofer FIT.
 */
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.almanac.event.datafusion.core.DataFusionManagerCore;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import eu.linksmart.gc.utils.mqtt.subscription.MqttMessage;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.test.annotation.type.IntegrationTest;

import java.util.*;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class Simulate implements Observer {

    @Before
    public void init(){
        //
    }
    static StaticBroker brokerService;
    static Vector<String> messages;
    static int n =0;
    @Test
    @Category(IntegrationTest.class)
    public void emptyTest1() throws Exception {
        DataFusionManagerCore.start(new String[0]);
        brokerService = new StaticBroker("localhost","1883");

        brokerService.connect();
        brokerService.addListener("/out/#", this);
        messages = new Vector<String>();

       long m = simulateEvents();
        Thread.sleep(20000);
        assertEquals(m,n);


    }
    public static long simulateEvents(){
        Long l = new Long(0);
        try {


            Sensor sen = new Sensor();
            sen.setId("1");
            sen.setObservations(null);
            Datastream ds = new Datastream();
            ds.setObservations(null);
            ds.setId("1");
            Observation ob = new Observation();
            ob.setDatastream(ds);
            ob.setSensor(sen);
            ob.setPhenomenonTime(new Date());
            ob.setResultType("Measure");
            ob.setResultValue(1);
            ob.setFeatureOfInterest(null);

            int id = 0;
            ob.setId(String.valueOf(1));
            //PersistentBean bean;
            Map<String,ArrayList<Observation>> test= new Hashtable();

            String topic= "/federation1/smat/v2/observation/1/1";
            test.put(topic, new ArrayList<Observation>());

            test.get(topic).add(ob);
            test.get(topic).add(ob);

            ObjectMapper mapper = new ObjectMapper();

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            int sleep = 1000000;
            while (sleep>=0){
                if(!brokerService.isConnected())
                    try {
                        brokerService.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                // if(l.intValue()%2 == 0)
                ob.setResultValue(sleep);
                //  else
                //     ob.setResultValue(-l);

                ob.setPhenomenonTime(new Date());
                // ob.setId(String.valueOf(id));
                if (id>10) {
                    id = 0;
                }

                try {
                    brokerService.publish(topic,(mapper.writeValueAsString(ob)).getBytes(), 0,false);
                   // System.out.println(mapper.writeValueAsString(ob));
                    //
                        sleep-=1000;
                    if(sleep>=0)
                        Thread.sleep(0,sleep);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                //if(firstLoop>1)
                l++ ;

                id++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    @Override
    public void update(Observable o, Object arg) {


        MqttMessage mqttMessage = (MqttMessage)arg;
        if(mqttMessage.getTopic().contains("out")) {
            n++;
            messages.add(new String(((MqttMessage) arg).getPayload()));
        }

    }
}
