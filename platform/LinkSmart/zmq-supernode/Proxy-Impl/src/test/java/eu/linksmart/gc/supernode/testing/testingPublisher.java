package eu.linksmart.gc.supernode.testing;

import eu.linksmart.gc.supernode.Constants;
import org.zeromq.ZMQ;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by carlos on 25.11.14.
 */

public class testingPublisher {

    private static final String senderID = UUID.randomUUID().toString();


    public static void main (String[] args) throws InterruptedException {

        PubThread t = new PubThread();
        t.start();
        Thread.sleep(30000);



    }
    private static class PubThread extends Thread{
        @Override
        public void run(){


            System.out.println("pub thread started");
            ZMQ.Context ctx = ZMQ.context(1);
            System.out.println("versioN: "+ZMQ.getVersionString());

            ZMQ.Socket socket = ctx.socket(ZMQ.PUB);

            socket.connect("tcp://localhost:7000");
            //socket.bind("tcp://*:7001");
            System.out.println("pub thread connected to :"+"tcp://localhost:7000");

            discovery(socket);

            while(!Thread.currentThread ().isInterrupted ()){


                // ping
                hartbeat(socket);
                System.out.println("Ping !");

                // ping
                publish(socket, "ABC".getBytes());
                System.out.println("published");



                //socket.sendMore("A-C-F");
                //socket.sendMore("AAAA");
                //socket.send("BBBB");
                //System.out.println("Random topic");



                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            socket.close();
            ctx.term();


        }

    }
    private static void hartbeat(ZMQ.Socket socket){
        byte[] serializedUnixTime = serializeTimestamp();
        socket.sendMore(Constants.HEARTBEAT_TOPIC);
        socket.sendMore(new byte[]{Constants.MSG_HEARTBEAT});
        socket.sendMore(serializedUnixTime);
        socket.sendMore(senderID);
        socket.send("".getBytes());

    }
    private static void discovery(ZMQ.Socket socket){
        byte[] serializedUnixTime = serializeTimestamp();
        socket.sendMore(Constants.BROADCAST_TOPIC);
        socket.sendMore(new byte[]{Constants.MSG_PEER_DISCOVERY});
        socket.sendMore(serializedUnixTime);
        socket.sendMore(senderID);
        socket.send("".getBytes());

    }
    private static void publish(ZMQ.Socket socket, byte[] payload){
        byte[] serializedUnixTime = serializeTimestamp();
        socket.sendMore(senderID);
        socket.sendMore(new byte[]{Constants.MSG_UNICAST});
        socket.sendMore(serializedUnixTime);
        socket.sendMore(senderID);
        socket.send(payload);

    }
    public static byte[] serializeTimestamp(){
        long timestamp = System.currentTimeMillis();
        return ByteBuffer.allocate(8).putLong(timestamp).array();
    }


}
