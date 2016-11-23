package eu.linksmart.testing.tooling;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
        if(mqttClient==null)
            mqttClient =  new MqttClient("tcp://"+broker+":1883", UUID.randomUUID().toString(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMaxInflight(100);
        options.setCleanSession(true);
        
        mqttClient.connect(options);


    }
    public void publish(String topic, String payload, int i) throws MqttException {
        boolean published = false;

        while (!published) {
            try {
                mqttClient.publish(topic, payload.getBytes(), qos, false);
                validator.addMessage(topic, i);
                published=true;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                }catch (Exception e1){
                    System.err.println(e1.getMessage());
                }
            }
        }
    }
    static protected class Cleaner implements Runnable{

        public Cleaner() {
        }


        @Override
        public void run() {
            double messages , avg, n =1;
            while (activeThreads>0){
                try {
                    Thread.sleep(1000);

                    synchronized (object) {
                        messages = i;
                        total += messages;
                        avg = total/n;
                        i = 0;
                        n++;
                    }
                    System.out.println(
                            "{\"total\": "+String.valueOf(total)+
                                    ", \"messages\": "+String.valueOf(messages)+
                                    ", \"avg\": "+String.valueOf(avg)+
                                    ", \"time\":\""+ Instant.now().toString()+"\"}");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }



        }

    }
}
