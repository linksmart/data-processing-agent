package testing;

import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.gc.utils.configuration.Configurator;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class Application  {


    public static void main(String[] args) {


    }
    static void testing(String[] args){
        int producer=0;
        int nThread =1, n=1;
        String topic="/federation1/amiat/v2/", broker ="localhost" ;
        if(args.length>0)
            producer = Integer.valueOf(args[0]);
        if(args.length>1)
            nThread = Integer.valueOf(args[1]);

        if(args.length>2)
            n = Integer.valueOf(args[2]);


        if(args.length>3)
            topic = args[3];

        if(args.length>4)
            broker = args[4];

        ArrayList arrayList = new ArrayList();
        for (int i=0; i<nThread;i++)
            switch (producer) {
                case 2:
                    arrayList.add(new Consumer(n,broker));
                    break;
                case 3:
                    try {
                        Configurator.addConfFile("conf.cfg");
                        new UtilsConsumer(n,broker, "1883", "/#",Application.class.getCanonicalName(),"tester");
                    } catch (MalformedURLException | MqttException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    new  FeederTester();
                    arrayList.add(new Thread( new  FeederTester()));
                    ((Thread)arrayList.get(arrayList.size()-1)).start();
                    break;
                default:
                    arrayList.add(new Thread(new Producer(n, topic, broker)));
                    ((Thread)arrayList.get(arrayList.size()-1)).start();

            }
        //new Thread(new Application()).start();
        while (true)
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

}
