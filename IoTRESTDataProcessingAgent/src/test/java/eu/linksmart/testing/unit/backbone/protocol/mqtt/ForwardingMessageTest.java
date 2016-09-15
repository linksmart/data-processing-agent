package eu.linksmart.testing.unit.backbone.protocol.mqtt;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.almanac.ogc.sensorthing.api.datamodel.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 24.04.2015 a researcher of Fraunhofer FIT.
 */
public class ForwardingMessageTest implements Observer {

    static StaticBroker brokerService =null;

    public static void main(String [] args) {

        try {
            gprt();
        } catch (Exception e) {
            e.printStackTrace();
        }

       simulateEvents(100);


    }

    public static void gprt(){


        String payload = "{\"value\":1,\"timestamp\":\"2015-11-16T17:45:52.23Z\"}", payload2 = "{\"value\":30.0,\"timestamp\":\"2015-11-16T17:45:52.23Z\"}", topic = "/storage/devices/46/variables/5/measurements", topic2 = "/storage/devices/6/variables/0/measurements";
        try {
            StaticBroker broker = new StaticBroker("almanac","1883" );
            broker.connect();
            while (true) {
                broker.publish(topic, payload.getBytes());
                broker.publish("/storage/devices/6/variables/0/measurements", payload2.getBytes());
                broker.publish("/storage/devices/11/variables/0/measurements", payload2.getBytes());
                broker.publish("/storage/devices/12/variables/0/measurements", payload2.getBytes());
                broker.publish("/storage/devices/15/variables/0/measurements", payload2.getBytes());
                broker.publish("/storage/devices/13/variables/0/measurements", payload2.getBytes());
                broker.publish("/storage/devices/53/variables/0/measurements", payload2.getBytes());
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        IA ob= null;
        try {
            ob = (IA) Class.forName(CB.class.getCanonicalName()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ob.method();*/
/*
        Gson gson = new Gson();

        Foo dataStructure = new Foo();

        String[] al= {"test"};
        dataStructure.setUsedBy(al);

        System.out.println(gson.toJson(dataStructure));

        ArrayList<String> str=new ArrayList<String>();
        Attribute attribute1=new Attribute("b"), attribute2=new Attribute("a");
        Attribute test = new Attribute("att");

        dataStructure = gson.fromJson("{    \"usedBy\":[\"lerning\"],\n" +
                        "    \"attributesStructures\":[\n" +
                        "            {\n" +
                        "                \"attributeName\":\"temperature\",\n" +
                        "                \"id\":\"11\"\n" +
                        "                \n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"attributeName\":\"current\",\n" +
                        "                \"id\":\"21\"\n" +
                        "                \n" +
                        "            }\n" +
                        "            \n" +
                        "        ]\n" +
                        "        \n" +
                        "        \n" +
                        "    \n" +
                        "}"
                , Foo.class);
        System.out.println((new Gson()).toJson(test));
        ArrayList<Attribute> at= new ArrayList<Attribute>();
        at.add(test);
        Instances inst = new Instances("test",at,10);
        inst.setClassIndex(0);

        Instance in = new DenseInstance(1);
        in.setDataset(inst);

// Set instance's values for the attributes "length", "weight", and "position"
        in.setValue(at.get(0), 5);


// Set instance's dataset to be the dataset "race"

        SGD sgd= new SGD();
        sgd.setLossFunction(new SelectedTag(SGD.SQUAREDLOSS, SGD.TAGS_SELECTION));

        try {
   //         sgd.buildClassifier(inst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sgd.updateClassifier(in);

        }catch (NullPointerException e) {
            try {
                sgd.buildClassifier(inst);
                sgd.updateClassifier(in);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

// Print the instance
        System.out.println("The instance: " + inst);*/
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void update(Observable o, Object arg) {
/*
        try {
            StopWatch watch = new StopWatch(),watch2 = new StopWatch();
           // MqttMe data = (MqttTunnelledMessage)arg;
             watch.start();

            watch.stop();
            watch2.start();
            Observation d2 = mapper.readValue(data.getPayload(),Observation.class);
            watch2.stop();
            System.out.println(watch.getTime()<watch2.getTime());
            watch.reset();
            watch2.reset();

        } catch (Exception e) {
        e.printStackTrace();
    }*/
    }
    public static void readJson(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            ArrayList<Thing> thingList = new ArrayList<Thing>();
            File file =new File("metadata-2.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

                for(String line; (line = br.readLine()) != null; ) {
                    mapper.readValue(line,Thing.class);
                }


                // line is not visible here.



        }catch(Exception e){
            System.out.println(e);
        }




    }
    public static void readAll(){
        try {

            File file = new File("metadata-2.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String str = new String(data, "UTF-16");
            String [] lines = str.split("\n");
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            ArrayList<Thing> thingList = new ArrayList<Thing>();
            for(int i=0; i<lines.length-1; i++)
               thingList.add( mapper.readValue(lines[i],Thing.class));

            org.geojson.LngLatAlt point =((org.geojson.Point) ((eu.almanac.ogc.sensorthing.api.datamodel.Location) thingList.get(0).getLocations().toArray()[0]).getGeometry()).getCoordinates();
            System.out.println(point.getLatitude());
            System.out.println(point.getLongitude());
            System.out.println();
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public static void simulateEvents(int numEvents){
        try {
            // MqttClient c =new MqttClient("tcp://almanac-showcase.ismb.it:1883","test", new MemoryPersistence());



         /*    ForwardingListener fm = new ForwardingListener("almanac-showcase.ismb.it","1883", "/eu/almanac-project/federation1/trn/scral/v2.0/#", UUID.randomUUID(),new ForwardingMessageTest());
           try {
                fm.setBroker("almanac-showcase.ismb.it","1883");
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
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
            Long l = new Long(0);
            int id = 0;
            ob.setId(String.valueOf(1));
            //BootstrappingBean bean;
            Map<String,ArrayList<Observation>> test= new Hashtable<>();

            String topic= "/federation1/smat/v2/observation/00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b/00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b";
            test.put(topic, new ArrayList<Observation>());

            test.get(topic).add(ob);
            test.get(topic).add(ob);

            ObjectMapper mapper = new ObjectMapper();

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            int sleep = numEvents   ;
            while (true){
                if(!brokerService.isConnected())
                    try {
                        brokerService.connect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                // if(l.intValue()%2 == 0)
                ob.setResultValue(l);
                //  else
                //     ob.setResultValue(-l);

                ob.setPhenomenonTime(new Date());
               // ob.setId(String.valueOf(id));
                if (id>10) {
                    id = 0;
                }

                try {
                    brokerService.publish(topic,(mapper.writeValueAsString(ob)).getBytes(), 0,false);
                    System.out.println(mapper.writeValueAsString(ob));
                    if(sleep>=0)
                        sleep--;

                    if(sleep>=0)
                        Thread.sleep(100);
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
    }
    public static void twoSafeEvents(String broker, String topic){
        try {
            MqttClient c =new MqttClient(broker,"test", new MemoryPersistence());
            c.connect();


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
            ob.setResultType("Initialization");
            ob.setResultValue(0.0);
            ob.setFeatureOfInterest(null);

            ob.setId(null);
            ObjectMapper mapper = new ObjectMapper();

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            try {
                c.publish("/federation2/"+topic+"/v2/observation/00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b/1",(mapper.writeValueAsString(ob)).getBytes(), 0,false);
                System.out.println(mapper.writeValueAsString(ob));
                c.publish("/federation2/" + topic + "/v2/observation/00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b/1", (mapper.writeValueAsString(ob)).getBytes(), 0, false);
                System.out.println(mapper.writeValueAsString(ob));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
