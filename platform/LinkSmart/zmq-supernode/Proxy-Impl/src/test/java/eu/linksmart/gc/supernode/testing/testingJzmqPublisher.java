package eu.linksmart.gc.supernode.testing;



import eu.linksmart.gc.supernode.Constants;
import org.zeromq.ZMQ;

public class testingJzmqPublisher {

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        ZMQ.Context context = ZMQ.context(1);
        System.out.println("versioN: "+ZMQ.getVersionString());

        // Socket to talk to server
        System.out.println("Connecting to hello world server");

        ZMQ.Socket socket = context.socket(ZMQ.PUB);
        socket.connect(Constants.mXSUB);

        while (true) {
            socket.send("Fuu");


            System.out.println("fuu sent.");
            Thread.sleep(1000);

        }




    }

}