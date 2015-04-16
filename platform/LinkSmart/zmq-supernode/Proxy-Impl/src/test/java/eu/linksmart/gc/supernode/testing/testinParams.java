package eu.linksmart.gc.supernode.testing;

import java.util.Vector;

/**
 * Created by carlos on 04.12.14.
 */
public class testinParams {

    String item = "ITEM";

    public static void main(String[] args) throws InterruptedException {

        Vector<String> datavector = new Vector<String>();
        AddThread t1 = new AddThread(datavector);
        RemoveThread t2 = new RemoveThread(datavector);

        t1.start();
        Thread.sleep(1000);
        t2.start();

        while(true){
            Thread.sleep(500);

            System.out.println("main thread counts : "+datavector.size()+" items");
            //System.out.println("main thread item: "+datavector.get(0));



        }



    }

    public static class AddThread extends Thread{

        Vector<String> data;

        public AddThread(Vector<String> weee) {
            data = weee;

        }

        public void run(){

            while(true) {
                data.add("ITEM");
                System.out.println("added ITEM. Count : "+data.size());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    public static class RemoveThread extends Thread{

        Vector<String> data;

        public RemoveThread(Vector<String> weee) {
            data = weee;

        }

        public void run(){

            while(true) {
                data.remove(0);
                System.out.println("removed ITEM Count : "+data.size());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
