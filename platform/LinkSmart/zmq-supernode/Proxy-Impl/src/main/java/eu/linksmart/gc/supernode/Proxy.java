package eu.linksmart.gc.supernode;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by carlos on 28.11.14.
 */
public class Proxy {

    private final static Logger LOG = Logger.getLogger(Proxy.class.getName());


    private TrafficWatch trafficWatch;
    private HeartbeatWatch heartbeatWatch;
    private ProxyThread proxyThread;

    private final String mXSubAddress;
    private final String mXPubAddress;


    private final ConcurrentHashMap<UUID, Long> heartbeatTimestamps;

    public Proxy() {

        // use default values for proxy ports and address
        mXSubAddress = Constants.mXSUB;
        mXPubAddress = Constants.mXPUB;

        LOG.trace("No parameters provided. Using "+Constants.mXSUB+" and "+Constants.mXPUB);

        heartbeatTimestamps = new ConcurrentHashMap<UUID, Long>();

    }
    public Proxy(String aIP, int aXSubPort, int aXPubPort) {

        //"tcp://localhost:7000";
        //"tcp://localhost:7001";

        if(aIP.equals("0.0.0.0")) {
            LOG.trace("0.0.0.0 detected. Using all interfaces.");
            mXSubAddress = "tcp://*:" + aXSubPort;
            mXPubAddress = "tcp://*:" + aXPubPort;
        }else{
            LOG.trace("Normal IP4 detected. Using primary interface");
            mXSubAddress = "tcp://" + aIP + ":" + aXSubPort;
            mXPubAddress = "tcp://" + aIP + ":" + aXPubPort;
        }

        heartbeatTimestamps = new ConcurrentHashMap<UUID, Long>();

    }
    public void startProxy(){

        LOG.debug("starting threads...");
        // starting ZMQ proxy
        proxyThread = new ProxyThread();
        proxyThread.start();
        LOG.debug("proxy thread started.");

        // starting traffic watch thread
        trafficWatch = new TrafficWatch(heartbeatTimestamps);
        trafficWatch.start();
        LOG.debug("traffic watch thread started.");

        // starting heart beat thread
        heartbeatWatch = new HeartbeatWatch(heartbeatTimestamps);
        heartbeatWatch.start();
        LOG.debug("heartbeat watch thread started.");

    }

    // close thread and resources
    public void stopProxy() {

        try {
            LOG.debug("stopping proxy threads...");
            heartbeatWatch.interrupt();
            heartbeatWatch.join();
            trafficWatch.stopTrafficWatch();
            trafficWatch.join();
            proxyThread.stopProxyThread();
            //proxyThread.join();
            LOG.debug("all threads stopped.");
        } catch (InterruptedException ex) {
            LOG.debug("Wow !");
        }

    }
    public boolean proxyThreadAlive(){
        return proxyThread.isAlive();
    }
    public boolean heartbeatWatchAlive(){
        return heartbeatWatch.isAlive();
    }
    public boolean trafficWatchAlive(){
        return trafficWatch.isAlive();
    }

    private class ProxyThread extends Thread{

        private ZMQ.Context ctx;
        private ZMQ.Socket xsubSocket;
        private ZMQ.Socket xpubSocket;

        @Override
        public void run() {

            ctx = ZMQ.context(1);
            xsubSocket = ctx.socket(ZMQ.XSUB);
            xsubSocket.bind(mXSubAddress);
            LOG.trace("XSUB trafficSocket bound to : " + mXSubAddress);
            xpubSocket = ctx.socket(ZMQ.XPUB);
            xpubSocket.bind(mXPubAddress);
            LOG.trace("XPUB trafficSocket bound to : " + mXPubAddress);

            LOG.debug("starting blocking ZMQ proxy...");
            try {
                ZMQ.proxy(xsubSocket, xpubSocket, null);
            }catch(Exception ex){
                LOG.warn("ZMQ proxy interrupted",ex);
            }

            LOG.info("ProxyThread terminated");
        }
        public void stopProxyThread(){

            xpubSocket.setLinger(0);
            xpubSocket.unbind(mXPubAddress);
            LOG.trace("XPUB unbound..");
            xpubSocket.close();
            LOG.trace("XPUB closed.");

            xsubSocket.setLinger(0);
            xsubSocket.unbind(mXSubAddress);
            LOG.trace("XSUB unbound.");
            xsubSocket.close();
            LOG.trace("XSUB closed.");

            // TODO current JeroMQ proxy implementation hangs on during context termination. No graceful shutdown of the proxy possible
            //ctx.close();
            //LOG.trace("ZMQ ctx terminated.");

        }
    }

    // analyzes traffic of the proxy
    private class TrafficWatch extends Thread {

        final ZMQ.Context ctx;
        final ZMQ.Socket trafficSocket;

        // thread safe hash map of heartbeat timers
//        ConcurrentHashMap<UUID, Long> hartbeatTimestamps = new ConcurrentHashMap<UUID, Long>();

        private final Message aMessage;

        final ConcurrentHashMap<UUID, Long> mPeers;


        public TrafficWatch(ConcurrentHashMap<UUID, Long> peers) {
            mPeers = peers;

            LOG.trace("traffic watch : peers : "+mPeers.size());
            ctx = ZMQ.context(1);
            trafficSocket = ctx.socket(ZMQ.SUB);
            LOG.trace("traffic watch : SUB trafficSocket created");
            trafficSocket.connect(mXPubAddress);
            LOG.trace("traffic watch : connected to proxy :" + mXPubAddress);
            trafficSocket.subscribe("".getBytes());
            LOG.trace("traffic watch : subscribed to everything");

            aMessage = new Message();

        }
        public void stopTrafficWatch(){
            trafficSocket.unsubscribe("".getBytes());
            LOG.trace("traffic watch : un-subscribed from everything");
            trafficSocket.close();
            trafficSocket.setLinger(0);
            ctx.term();
            while(this.isAlive()) {
                LOG.trace("interrupting traffic watch...");
                this.interrupt();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
        }
        private void receiveMessage(){
            aMessage.version = trafficSocket.recv()[0];
            aMessage.type = trafficSocket.recv()[0];
            aMessage.timestamp = Message.deserializeTimestamp(trafficSocket.recv());
            aMessage.sender = new String(trafficSocket.recv());
            aMessage.requestID = new String(trafficSocket.recv());
            aMessage.payload = trafficSocket.recv();
            if(LOG.isTraceEnabled())Message.printMessage(aMessage);
        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                LOG.debug("receiving topic...");
                try {
                    aMessage.topic = new String(trafficSocket.recv());
                    LOG.trace("topic received : "+aMessage.topic);

                    if (aMessage.topic.equals(Constants.HEARTBEAT_TOPIC)) {
                        receiveMessage();
                        mPeers.put(java.util.UUID.fromString(aMessage.sender), System.currentTimeMillis());
                        LOG.debug("no of peers : " + mPeers.size());
                    } else if (aMessage.topic.equals(Constants.BROADCAST_TOPIC)) {
                        receiveMessage();
                    } else if (Message.isUUID(aMessage.topic)) {
                        // TODO in case the client sends a valid UUID as topic but no proper message, the routine will fail
                        // TODO better handling or format specification required
                        receiveMessage();
                    } else {
                        LOG.warn("unknown topic detected.");
                        // receive crap from unknown topic
                        while (trafficSocket.hasReceiveMore()) {
                            trafficSocket.recv();
                            LOG.trace("unknown message part ignored.");
                        }
                    }
                } catch (ZMQException ex) {

                    if (ex.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                        LOG.debug("received termination interrupt");
                        break;
                    }else{
                        LOG.error(ex);
                    }
                }
            }
            LOG.info("traffic watch thread terminated.");
        }
    }


    // checks periodically for peer heartbeat timeouts
    private class HeartbeatWatch extends Thread {

        ZMQ.Context ctx;
        ZMQ.Socket heartbeatSocket;
        final ConcurrentHashMap<UUID, Long> mPeers;

        public HeartbeatWatch(ConcurrentHashMap<UUID, Long> peers) {

            mPeers = peers;
        }

        @Override
        public void run() {
            LOG.trace("heartbeat watch : peers : "+mPeers.size());

            ctx = ZMQ.context(1);
            heartbeatSocket = ctx.socket(ZMQ.PUB);
            LOG.trace("PUB trafficSocket created");
            heartbeatSocket.connect(mXSubAddress);
            LOG.trace("connected to proxy: " + mXSubAddress);

            try {
                while (true) {
                    LOG.trace("analyzing "+mPeers.size()+" peer timers...");
                    LOG.trace("peers : "+mPeers.size());
                    for (UUID sender : mPeers.keySet()) {
                        Long tstamp = mPeers.get(sender);
                        LOG.trace("peer : " + sender.toString());
                        if (System.currentTimeMillis() - tstamp > Constants.HEARTBEAT_TIMEOUT) {
                            LOG.debug("detected timeout for peer :" + sender.toString());
                            broadcastPeerdown(sender);
                            mPeers.remove(sender);
                            LOG.debug("no. of peers : " + mPeers.size());
                        }
                    }
                    Thread.sleep(Constants.HEARTBEAT_INTERVAL/2);
                }
            } catch (InterruptedException ex) {
                LOG.info("heartbeat thread : interrupt signal received.");
            }
            heartbeatSocket.setLinger(0);
            heartbeatSocket.close();
            ctx.close();
            LOG.info("heartbeat watch thread terminated.");
        }

        private void broadcastPeerdown(UUID aSender) {
            byte[] serializedUnixTime = Message.serializeTimestamp();
            heartbeatSocket.sendMore(Constants.BROADCAST_TOPIC);
            heartbeatSocket.sendMore(new byte[]{Constants.VERSION});
            heartbeatSocket.sendMore(new byte[]{Constants.MSG_PEERDOWN});
            heartbeatSocket.sendMore(serializedUnixTime);
            heartbeatSocket.sendMore(aSender.toString());
            heartbeatSocket.sendMore("".getBytes());
            heartbeatSocket.send("".getBytes());
            LOG.info("peer down broadcasted");
        }

    }
    public int getNumberOfPeers(){
        return this.heartbeatTimestamps.size();
    }



}
