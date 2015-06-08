package eu.linksmart.gc.supernode.testing;

import eu.linksmart.gc.supernode.Proxy;

/**
 * Created by carlos on 28.11.14.
 */
public class testingWorkflow {


    public static void main (String[] args) throws InterruptedException {

        Proxy proxy = new Proxy();

        while(true) {
            System.out.println("STARTING PROXY....");
            proxy.startProxy();
            System.out.println("DONE.");


            //Client client = new Client();


            Thread.sleep(8000);
            System.out.println("YEEAH! WAKE UP!");
            System.out.println("STOPING PROXY.............");
            proxy.stopProxy();
            System.out.println("PROXY STOPED");
            Thread.sleep(3000);
        }

        //System.exit(0);



    }
}
