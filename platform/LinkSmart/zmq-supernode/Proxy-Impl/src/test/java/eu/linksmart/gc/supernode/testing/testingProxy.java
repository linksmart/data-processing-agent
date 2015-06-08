package eu.linksmart.gc.supernode.testing;

/**
 * Created by carlos on 28.11.14.
 */
public class testingProxy {

//    private static Logger LOG = Logger.getLogger(testingProxy.class.getName());
//
//    private ZMQ.Context context;
//    private ZMQ.Socket xsubSocket;
//    private ZMQ.Socket xpubSocket;
//
//    private TrafficWatch trafficWatch;
//
//    public static void main (String[] args) throws InterruptedException {
//
//        ConcurrentHashMap<UUID, Long> heartbeatTimestamps = new ConcurrentHashMap<UUID, Long>();
//
//
//        ZMQ.Context context = ZMQ.context(1);
//        ZMQ.Socket xsubSocket = context.socket(ZMQ.XSUB);
//        xsubSocket.bind(Constants.mXSUB);
//        LOG.trace("XSUB trafficSocket bound to : " + Constants.mXSUB);
//        ZMQ.Socket xpubSocket = context.socket(ZMQ.XPUB);
//        xpubSocket.bind(Constants.mXPUB);
//        LOG.trace("XPUB trafficSocket bound to : " + Constants.mXPUB);
//
//
//
//        // starting traffic watch thread
//        TrafficWatch trafficWatch = new TrafficWatch(heartbeatTimestamps);
//        trafficWatch.start();
//
//        System.out.println("starting proxy....");
//        boolean proxy = ZMQ.proxy(xsubSocket, xpubSocket, null);
//
//        if(!proxy) {
//            System.out.println("fuuuu !");
//        }else{
//            System.out.println("started");
//        }
//
//        Thread.sleep(60000);
//        trafficWatch.stopTrafficWatch();
//
//        System.exit(0);
//
//    }
//    // analyzes traffic of the proxy
//    private static class TrafficWatch extends Thread {
//
//        ZMQ.Context ctx;
//        ZMQ.Socket trafficSocket;
//
//        // thread safe hash map of heart beat timers
//        ConcurrentHashMap<UUID, Long> hartbeatTimestamps = new ConcurrentHashMap<UUID, Long>();
//
//        private Message aMessage;
//
//        ConcurrentHashMap<UUID, Long> mPeers;
//
//
//        public TrafficWatch(ConcurrentHashMap<UUID, Long> peers) {
//
//            mPeers = peers;
//            ctx = ZMQ.context(1);
//            trafficSocket = ctx.socket(ZMQ.SUB);
//            LOG.trace("traffic watch : SUB trafficSocket created");
//            trafficSocket.connect(Constants.mXPUB);
//            LOG.trace("traffic watch : connected to proxy :" + Constants.mXPUB);
//            trafficSocket.subscribe("".getBytes());
//            LOG.trace("traffic watch : subscribed to everything");
//
//            aMessage = new Message();
//
//        }
//        public void stopTrafficWatch(){
//            trafficSocket.unsubscribe("".getBytes());
//            trafficSocket.close();
//            ctx.term();
//            this.interrupt();
//        }
//
//        @Override
//        public void run() {
//
//            LOG.info("traffic watch thread started.");
//            while (!Thread.currentThread().isInterrupted()) {
//                LOG.debug("receiving topic...");
//                try {
//                    byte[] raw = trafficSocket.recv();
//                    aMessage.topic = new String(raw);
//                    LOG.trace("topic received : "+aMessage.topic);
//
//                    if (aMessage.topic.equals(Constants.HEARTBEAT_TOPIC)) {
//                        aMessage.type = trafficSocket.recv()[0];
//                        aMessage.timestamp = Message.deserializeTimestamp(trafficSocket.recv());
//                        aMessage.sender = new String(trafficSocket.recv());
//                        aMessage.payload = trafficSocket.recv();
//                        hartbeatTimestamps.put(java.util.UUID.fromString(aMessage.sender), System.currentTimeMillis());
//                        LOG.debug("no of peers : " + hartbeatTimestamps.size());
//                        Message.printMessage(aMessage);
//                    } else if (aMessage.topic.equals(Constants.BROADCAST_TOPIC)) {
//                        aMessage.type = trafficSocket.recv()[0];
//                        aMessage.timestamp = Message.deserializeTimestamp(trafficSocket.recv());
//                        aMessage.sender = new String(trafficSocket.recv());
//                        aMessage.payload = trafficSocket.recv();
//                        Message.printMessage(aMessage);
//                    } else if (Message.isUUID(aMessage.topic)) {
//                        // TODO in case the client sends a valid UUID as topic but no proper message, the routine will fail
//                        // TODO better handling or format specification required
//                        aMessage.type = trafficSocket.recv()[0];
//                        aMessage.timestamp = Message.deserializeTimestamp(trafficSocket.recv());
//                        aMessage.sender = new String(trafficSocket.recv());
//                        aMessage.payload = trafficSocket.recv();
//                        Message.printMessage(aMessage);
//                    } else {
//                        LOG.warn("unknown topic detected.");
//                        // receive crap from unknown topic
//                        while (trafficSocket.hasReceiveMore()) {
//                            trafficSocket.recv();
//                            LOG.trace("unknown message part ignored.");
//                        }
//                    }
//                } catch (ZMQException ex) {
//
//                    if (ex.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
//                        LOG.debug("received termination interrupt");
//                        break;
//                    }else{
//                        LOG.error(ex);
//                    }
//                }
//
//
//            }
//            LOG.info("traffic watch thread terminated.");
//
//        }
//
//    }
}
