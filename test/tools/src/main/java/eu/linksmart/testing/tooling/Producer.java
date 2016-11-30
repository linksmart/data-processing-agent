package eu.linksmart.testing.tooling;


import java.time.Instant;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 10.03.2016 a researcher of Fraunhofer FIT.
 */
public class Producer extends Counter implements Runnable{

    static {
        cleaner = new Producer.Cleaner();
        cleanerThread = new Thread(cleaner);
        cleanerThread.start();
    }
    final String baseTopic;
    final int max;
    static Boolean informed = false, retainPolicy=false;
    final String basePayload;
    final boolean basePayloadWithWildcard;
    final long lot;
    final boolean shareIndex;
    int localI =0;
    public Producer(int index, String baseTopic, String broker, int max, String basePayload, long lot, boolean shareIndex, int qos,int sleeping,boolean retain, Publisher publisher) {
        super(publisher);
        this.max=max;
        if (index<0)
            id++;
        else
            id= index;
        this.baseTopic=baseTopic;
        this.broker = broker;
        this.basePayload = basePayload;
        this.basePayloadWithWildcard = basePayload.contains("<?>");
        this.lot = lot;
        this.shareIndex = shareIndex;
        this.qos = qos;
        this.sleeping =sleeping;
        retainPolicy =retain;
        if(validator==null)
            validator = new MessageValidator(this.getClass(),String.valueOf(id),(int)lot);

    }




    @Override
    public void run() {
        try {

            String  auxTopic=baseTopic.replace("<sid>", String.valueOf(id));
            boolean shouldPublish, next=true;
            Date before = new Date();

            String auxPayload, now;
            int payloadInt;

            now = String.valueOf((new Date()).getTime());
            auxPayload = basePayload.replace("<i>", "-1").replace("<epoch>", now);
            publish(auxTopic, auxPayload, -1);
            while (next)
                try {
                    synchronized (object){

                        shouldPublish = i<max||max<0;
                    }
                    if(shouldPublish) {

                        synchronized (object) {
                            if (shareIndex)
                                payloadInt = i;
                            else
                                payloadInt = localI;


                            now = String.valueOf((new Date()).getTime());
                            i++;
                            localI++;
                            informed = false;

                        }
                        auxPayload = basePayload.replace("<i>", String.valueOf(payloadInt)).replace("<epoch>", now);
                        publish(auxTopic, auxPayload, payloadInt);

                        next = lot > -1 && localI < lot || lot < 0;
                    }else {

                        synchronized (object) {
                            if(!informed) {
                                informed =true;
                                System.out.println("{\"info\":\"limit reached\"}");
                                Thread.sleep(10);

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            Date after= new Date();
            Thread.sleep(2000);
            boolean exited = false;
            while (!exited)
                try {
                    synchronized (object) {
                        activeThreads--;
                    }
                    exited=true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            System.out.println(
                    "{\"total\": "+String.valueOf(total)+
                            ", \"totalTime\": "+(after.getTime()-before.getTime())/1000.0+
                            ", \"sid\": "+String.valueOf(id)+
                            ", \"localI\": "+String.valueOf(localI)+
                            ", \"shared\": "+String.valueOf(i)+
                            ", \"messages\": "+String.valueOf(0)+
                            ", \"time\":\""+ Instant.now().toString()+"\"}");

            mqttClient.disconnect();
            mqttClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


       //cleaner.count =false;

    }

}
