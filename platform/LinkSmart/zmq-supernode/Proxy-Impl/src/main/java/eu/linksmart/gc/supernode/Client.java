package eu.linksmart.gc.supernode;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

import java.util.Observable;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by carlos on 28.11.14.
 */
public class Client extends Observable{

    private static Logger LOG = Logger.getLogger(Client.class.getName());

    private final String peerID = UUID.randomUUID().toString();

    ZMQ.Context ctx;
    ZMQ.Socket pubSocket;
    ZMQ.Socket subSocket;

    HeartbeatThread heartbeatThread;
    SubscriberThread subscriberThread;

    Vector<String> subscriptions;


    public Client() {

        subscriptions = new Vector<String>();

        ctx = ZMQ.context(1);

        pubSocket = ctx.socket(ZMQ.PUB);
        pubSocket.connect(Constants.mXSUB);
        LOG.trace("client connected to XSUB trafficSocket : " + Constants.mXSUB);
        subSocket = ctx.socket(ZMQ.SUB);
        subSocket.connect(Constants.mXPUB);
        LOG.trace("client connected to XPUB trafficSocket : " + Constants.mXPUB);

        heartbeatThread = new HeartbeatThread();
        heartbeatThread.start();

        // subscriber thread, lazy start later on first subscribtion
        subscriberThread = new SubscriberThread();

        LOG.info("client "+this.peerID+" initialized.");

    }

    // close thread and resources
    public void finalize() throws Throwable {

        // stop heartbeat
        heartbeatThread.interrupt();

        // unsubscribe topics
        for(String aTopic : subscriptions){
            unsubscribe(aTopic);
        }
        // stop subscriber thread
        subscriberThread.interrupt();

        // clean up the IO
        pubSocket.close();
        subSocket.close();
        ctx.term();
        super.finalize();
    }
    public String getPeerID(){
        return this.peerID;
    }
    public void stopClient(){
        // stop heartbeat
        heartbeatThread.interrupt();

        // unsubscribe topics
        LOG.trace("iteration over subscribtion list of : "+subscriptions.size());
        // unsubscribe topics
        /*for(String aTopic : subscriptions){
            unsubscribe(aTopic);
        }*/
        for(int i=0; i < subscriptions.size() ; i++){
            unsubscribe(subscriptions.get(i));
        }
//        for(String aTopic : subscriptions){
//
//        }
        // stop subscriber thread
        //subscriberThread.interrupt();

        // clean up the IO
        pubSocket.close();
        LOG.trace("PUB trafficSocket closed.");
        subSocket.close();
        LOG.trace("SUB trafficSocket closed.");
        ctx.term();
        LOG.trace("ZMQ context terminated.");
    }

    public void subscribe(String topic){

        // start subscriber thread if not running
        if(!subscriberThread.isAlive()){
            subscriberThread.start();
        }
        subSocket.subscribe(topic.getBytes());
        subscriptions.add(topic);
        LOG.info("subscribed to : " + topic);

    }
    public void unsubscribe(String topic){

        if(subscriptions.contains(topic)){
            subSocket.unsubscribe(topic.getBytes());
            LOG.info("un-subscribed : "+topic);
        }
        subscriptions.clear();
    }


    public void publish(byte[] payload) {
        byte[] serializedUnixTime = Message.serializeTimestamp();
        pubSocket.sendMore(peerID);
        pubSocket.sendMore(new byte[]{Constants.VERSION});
        pubSocket.sendMore(new byte[]{Constants.MSG_UNICAST});
        pubSocket.sendMore(serializedUnixTime);
        pubSocket.sendMore(peerID);
        pubSocket.sendMore(UUID.randomUUID().toString());
        pubSocket.send(payload);
        LOG.debug("message published");
    }

    private void heartbeat() {
        byte[] serializedUnixTime = Message.serializeTimestamp();
        pubSocket.sendMore(Constants.HEARTBEAT_TOPIC);
        pubSocket.sendMore(new byte[]{Constants.VERSION});
        pubSocket.sendMore(new byte[]{Constants.MSG_HEARTBEAT});
        pubSocket.sendMore(serializedUnixTime);
        pubSocket.sendMore(peerID);
        pubSocket.sendMore("".getBytes());
        pubSocket.send("".getBytes());
        LOG.debug("heartbeat send");
    }
    public void discovery() {

        byte[] serializedUnixTime = Message.serializeTimestamp();
        pubSocket.sendMore(Constants.BROADCAST_TOPIC);
        pubSocket.sendMore(new byte[]{Constants.VERSION});
        pubSocket.sendMore(new byte[]{Constants.MSG_PEER_DISCOVERY});
        pubSocket.sendMore(serializedUnixTime);
        pubSocket.sendMore(peerID);
        pubSocket.sendMore(UUID.randomUUID().toString());
        pubSocket.send("".getBytes());
        LOG.debug("discovery send");
    }

    private class HeartbeatThread extends Thread {
        @Override
        public void run() {
            LOG.debug("heartbeat thread started.");
            try {
                while (true) {
                    heartbeat();
                    Thread.sleep(Constants.HEARTBEAT_INTERVAL);
                }
            } catch (InterruptedException ex) {
                LOG.debug("heartbeat thread interrupted.");
            }
            LOG.debug("heartbeat thread terminated.");
        }
    }
    private class SubscriberThread extends Thread {


        private void receiveMessage(Message someMessage){
            someMessage.version = subSocket.recv()[0];
            someMessage.type = subSocket.recv()[0];
            someMessage.timestamp = Message.deserializeTimestamp(subSocket.recv());
            someMessage.sender = new String(subSocket.recv());
            someMessage.requestID = new String(subSocket.recv());
            someMessage.payload = subSocket.recv();
            if(LOG.isTraceEnabled()){Message.printMessage(someMessage);}

        }
        @Override
        public void run() {
            subSocket.subscribe(Constants.BROADCAST_TOPIC.getBytes());
            subscriptions.add(Constants.BROADCAST_TOPIC);
            LOG.debug("subscribed to broadcast");

            Message aMessage = new Message();
            LOG.debug("subscriber thread started.");
            while (!Thread.currentThread ().isInterrupted ()) {
                    try {
                        aMessage.topic = new String(subSocket.recv());
                    }catch(Exception ex){
                        LOG.warn(ex);
                        // ZMQ context terminated. Exiting thread
                        break;
                    }
                    LOG.trace("client subscriber thread received topic : " + aMessage.topic);
                    if(aMessage.topic.equals(Constants.BROADCAST_TOPIC)){
                        LOG.trace("BROADCAST topic received");
                        receiveMessage(aMessage);
                        // remove subscription on PEER DOWN
                        if(aMessage.type==Constants.MSG_PEERDOWN){
                            LOG.debug("received PEER DOWN for : "+aMessage.sender);
                            if(subscriptions.contains(aMessage.sender)){
                                unsubscribe(aMessage.sender);
                            }
                        }else{
                            LOG.warn("unknown broadcast type : "+aMessage.type);
                        }
                    }
                    else if(Message.isUUID(aMessage.topic)){
                        LOG.trace("UNICAST topic received");
                        // TODO in case the client sends a valid UUID as topic but no proper message, the routine will fail
                        // TODO better handling or format specification required
                        receiveMessage(aMessage);
                        // notify observers about new message from subscribed topics
                        setChanged();
                        notifyObservers(aMessage);
                    }
                    else{
                        LOG.warn("unknown topic detected.");
                        // receive crap from unknown topic
                        while(subSocket.hasReceiveMore()){
                            subSocket.recv();
                            LOG.trace("unknown message part ignored.");
                        }
                    }
            }
            LOG.info("subscriber thread terminated");
        }
    }
}
