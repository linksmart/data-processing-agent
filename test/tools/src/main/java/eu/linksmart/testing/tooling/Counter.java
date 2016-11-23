package eu.linksmart.testing.tooling;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 14.03.2016 a researcher of Fraunhofer FIT.
 */
public abstract class Counter {

    protected static Thread cleanerThread ;
    protected static Cleaner cleaner;
    protected static int i=0, total,  sharedId=0,nThreads = 0,activeThreads;
    protected static final  Object object = new Object();
    protected int id=0, qos;
    protected MqttClient mqttClient = null;
    protected String broker;
    static protected MQTTMessageValidator validator = null;


    protected Counter(){
        nThreads = (activeThreads = nThreads+1);
    }

    void create() throws MqttException {
        mqttClient =  new MqttClient("tcp://"+broker+":1883", UUID.randomUUID().toString(), new MemoryPersistence());

        mqttClient.connect();


    }
    public void publish(String topic, String payload, int i) throws MqttException {
        boolean published = false;

        while (!published) {
            try {
                mqttClient.publish(topic, payload.getBytes(), qos, false);
                validator.addMessage(topic, i);
                published=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    static protected class Cleaner implements Runnable{

        public Cleaner() {
        }


        @Override
        public void run() {
            long acc =0;
            long start= System.nanoTime();
            double messages = 0, avg=0, n =1;
            while (activeThreads>0){
                try {
                    Thread.sleep(1000);
                    acc = (System.nanoTime() - start);
                    synchronized (object) {
                        messages = i;
                        total += messages;
                        avg = (total/n)/nThreads;
                        i = 0;
                        n++;
                    }
                    System.out.println(
                            "{\"total\": "+String.valueOf(total)+
                                    ", \"messages\": "+String.valueOf(messages)+
                                    ", \"avg\": "+String.valueOf(avg)+
                                    ", \"time\":\""+ Instant.now().toString()+"\"}");
                    start = System.nanoTime();
                    acc = 0;


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        }

    }
}
