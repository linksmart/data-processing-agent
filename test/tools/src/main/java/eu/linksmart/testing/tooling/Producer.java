package eu.linksmart.testing.tooling;


import java.util.Date;

/**
 * Created by José Ángel Carvajal on 10.03.2016 a researcher of Fraunhofer FIT.
 */
public class Producer extends Counter implements Runnable{

    static {
        cleaner = new Thread(new Producer.Cleaner());
        cleaner.start();
    }
    final String baseTopic;
    final int max;
    static Boolean informed = false;
    final String basePayload;
    final boolean basePayloadWithWildcard;
    final long lot;
    final boolean shareIndex;
    int localI =0;
    public Producer( int index,String baseTopic, String broker, int max, String basePayload, long lot,boolean shareIndex) {
        super();
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



    }




    @Override
    public void run() {
        try {
            create();
            String sid =String.valueOf(id);

            boolean shouldPublish;

            for (long j =lot;j!=0;j-- )
                try {
                    synchronized (object){

                        shouldPublish = i<max||max<0;
                    }
                    if(shouldPublish) {
                        String aux;
                        if (shareIndex)
                            aux= basePayload.replace("<i>",String.valueOf(i)).replace("<epoch>", String.valueOf((new Date()).getTime()));
                        else
                            aux= basePayload.replace("<i>",String.valueOf(localI)).replace("<epoch>", String.valueOf((new Date()).getTime()));

                        mqttClient.publish(baseTopic.replace("<sid>",String.valueOf(sid)), aux.getBytes(), 0, false);
                        synchronized (object) {

                            i ++;
                            localI++;
                            informed =false;
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
