package testing;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 14.03.2016 a researcher of Fraunhofer FIT.
 */
public abstract class Counter {

    protected static Thread cleaner ;
    protected static int i=0;
    protected static final  Object object = new Object();
    protected static int id=0;
    protected MqttClient mqttClient = null;
    protected String broker;
    static protected int nThreads = 0;

    protected Counter(){
        nThreads++;
    }

    void create() throws MqttException {
        mqttClient =  new MqttClient("tcp://"+broker+":1883", UUID.randomUUID().toString(), new MemoryPersistence());

        mqttClient.connect();


    }
    static protected class Cleaner implements Runnable{

        public Cleaner() {
        }

        @Override
        public void run() {
            long acc =0;
            long start= System.nanoTime();
            double messages = 0, total=0, avg=0;
            while (true){
                try {
                    Thread.sleep(1000);
                    acc = (System.nanoTime() - start);
                    synchronized (object) {
                        messages = i;
                    }
                    total = (messages * 1000000000.0) / acc;
                    avg = total/nThreads;
                    System.out.println("Total: "+String.valueOf(total)+ " avg: "+String.valueOf(avg));
                    start = System.nanoTime();
                    acc = 0;

                    synchronized (object) {
                        i = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
