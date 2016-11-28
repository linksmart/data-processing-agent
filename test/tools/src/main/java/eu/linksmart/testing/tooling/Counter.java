package eu.linksmart.testing.tooling;


import java.time.Instant;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 14.03.2016 a researcher of Fraunhofer FIT.
 */
public abstract class Counter {

    protected static Thread cleanerThread ;
    protected static Cleaner cleaner;
    protected static int i=0, total,  sharedId=0,nThreads = 0,activeThreads;
    protected static final  Object object = new Object();
    protected int id=0, qos, sleeping =0;
    protected Publisher mqttClient = null;
    protected String broker;
    static protected MessageValidator validator = null;


    protected Counter(Publisher publisher){
        mqttClient =publisher;
        nThreads = (activeThreads = nThreads+1);
    }


    public void publish(String topic, String payload, int i) throws Exception {
        boolean published = false;

        while (!published) {
            try {
                mqttClient.publish(topic, payload.getBytes(),qos, true);
                validator.addMessage(topic, i);
                published=true;
                try {
                    Thread.sleep(sleeping);
                }catch (Exception e1){
                    System.err.println(this.getClass().getName() + "(" + id + ") "+e1.getMessage());
                }
            } catch (Exception e) {
                System.err.println(this.getClass().getName() + "(" + id + ") "+e.getMessage());
                //e.printStackTrace();

            }
        }
    }
    static protected class Cleaner implements Runnable{

        public Cleaner() {
        }


        @Override
        public void run() {
            double messages , avg, n =1;
            Date before = new Date(), after;
            do {
                try {
                    Thread.sleep(1000);

                    synchronized (object) {
                        messages = i;
                        total += messages;
                        avg = total/n;
                        i = 0;
                        n++;
                    }
                    after= new Date();
                    System.out.println(
                            "{\"total\": "+String.valueOf(total)+
                                   // ", \"lapsed\": "+(after.getTime()-before.getTime())/1000.0+
                                    ", \"messages\": "+String.valueOf(messages)+
                                    ", \"avg\": "+String.valueOf(avg)+
                                    ", \"time\":\""+ Instant.now().toString()+"\"}");
                    before = new Date();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (activeThreads>0);



        }

    }
}
