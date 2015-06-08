package eu.linksmart.gc.supernode.testing;

/**
 * Created by carlos on 30.11.14.
 */
public class testMain {

    static{
        //System.loadLibrary ("libzmq");
        System.loadLibrary ("jzmq");
    }
    public static void main (String[] args) throws InterruptedException {
        System.out.println("hello main");
    }
}
