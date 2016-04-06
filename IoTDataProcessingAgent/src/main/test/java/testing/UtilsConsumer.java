package testing;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 14.03.2016 a researcher of Fraunhofer FIT.
 */
public class UtilsConsumer implements Observer {
    protected static Thread cleaner ;
    protected static int i=0;
    protected static final  Object object = new Object();
    protected static int id=0;
    private final StaticBroker brokerService;
    static protected int nThreads = 0;

    private ObjectMapper mapper = new ObjectMapper();
    static {
        cleaner = new Thread(new UtilsConsumer.Cleaner());
        cleaner.start();
    }
    public UtilsConsumer(int n, String brokerName, String brokerPort, String topic, String implName, String desc, String... implOf) throws MalformedURLException, MqttException {
        brokerService = new StaticBroker(brokerName,brokerPort);
        brokerService.addListener(topic,this);
        nThreads++;
        if (n>0)
            id++;
    }

    protected void mangeEvent(String s, byte[] bytes) {
        synchronized (object) {
            i++;
        }
      /*  if(mapper==null)
            mapper = new ObjectMapper();
        Observation event=null;

        try {
            event =mapper.readValue(bytes,Observation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }

    @Override
    public void update(Observable o, Object arg) {
        synchronized (object) {
            i++;
        }
      /*  if(mapper==null)
            mapper = new ObjectMapper();
        Observation event=null;

        try {
            event =mapper.readValue(bytes,Observation.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    static protected class Cleaner implements Runnable{

        public Cleaner() {
        }

        @Override
        public void run() {
            long acc =0;
            long start= System.nanoTime();
            double messages = 0, total=0,avg=0;
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
