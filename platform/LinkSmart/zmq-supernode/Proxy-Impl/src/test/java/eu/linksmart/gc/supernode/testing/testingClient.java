package eu.linksmart.gc.supernode.testing;

import eu.linksmart.gc.supernode.Client;
import eu.linksmart.gc.supernode.Proxy;
import org.zeromq.ZMQ;

/**
 * Created by carlos on 04.12.14.
 */
public class testingClient {

    public static void main(String[] args) throws InterruptedException {
        ZMQ.Context context = ZMQ.context(1);
        System.out.println("versioN: "+ZMQ.getVersionString());


        //
        Proxy proxy = new Proxy();
        proxy.startProxy();

        Client c1,c2;
        c1 = new Client();
        c2 = new Client();
        c2.subscribe(c1.getPeerID());




        int i = 0;
        while(i<3){
            c1.publish("ABC".getBytes());
            Thread.sleep(2000);
            i++;
        }
        System.out.println("STOPPING CLIENTS.... ");
        c1.stopClient();
        Thread.sleep(5000);
        c2.stopClient();







    }


}
