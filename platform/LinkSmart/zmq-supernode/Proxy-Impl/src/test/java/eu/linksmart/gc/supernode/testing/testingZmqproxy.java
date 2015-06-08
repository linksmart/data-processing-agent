package eu.linksmart.gc.supernode.testing;

/**
 * Created by carlos on 24.11.14.
 */
public class testingZmqproxy {

//
//    public static void main (String[] args) throws InterruptedException {
//            Context context = ZMQ.context(1);
//            Socket xsubSocket = context.socket(ZMQ.XSUB);
//            xsubSocket.bind(Constants.mXSUB);
//            Socket xpubSocket = context.socket(ZMQ.XPUB);
//            xpubSocket.bind(Constants.mXPUB);
//
//            SubscriberThread st = new SubscriberThread();
//            st.start();
//
//        System.out.println("starting proxy...");
//        boolean proxy = ZMQ.proxy(xsubSocket, xpubSocket, null);
//            if(proxy) {
//                System.out.println("done.");
//            }
//
//            System.out.println("closing proxy...");
//            xsubSocket.close();
//            xpubSocket.close();
//            context.term();
//    }
//
//    // checks periodically for peer hartbeat timeouts
//    private static class CleanupThread extends Thread{
//
//
//        ZMQ.Context ctx;
//        ZMQ.Socket socket;
//
//        private boolean stopCleanup = false;
//
//        ConcurrentHashMap<UUID,Long> mPeers;
//
//        public CleanupThread(ConcurrentHashMap<UUID,Long> peers){
//            mPeers = peers;
//
//        }
//        @Override
//        public void run() {
//            System.out.println("[INFO] cleanup thread starts.");
//
//            ctx = ZMQ.context(1);
//            socket = ctx.socket(ZMQ.PUB);
//            socket.connect(Constants.mXSUB);
//            System.out.println("cleanup thread starts connected to proxy");
//
//            try {
//                while (!stopCleanup) {
//
//                    for (UUID sender : mPeers.keySet()) {
//                        Long tstamp = mPeers.get(sender);
//                        if (System.currentTimeMillis() - tstamp > Constants.HEARTBEAT_TIMEOUT) {
//                            System.out.println("[INFO] cleanup thread detected timeout for :" + sender.toString());
//
//                            byte[] serializedUnixTime = Publisher.serializeTimestamp();
//                            socket.sendMore(Constants.BROADCAST_TOPIC);
//                            socket.sendMore(new byte[]{Constants.MSG_PEERDOWN});
//                            socket.sendMore(serializedUnixTime);
//                            socket.sendMore(sender.toString());
//                            socket.send("".getBytes());
//                            System.out.println("[INFO] peer down broadcasted");
//
//                            mPeers.remove(sender);
//                            System.out.println("No of peers : " + mPeers.size());
//
//                        }
//                    }
//                    Thread.sleep(1000);
//                }
//            }catch (InterruptedException ex){
//                System.out.println("Thread interrupted.");
//                socket.close();
//                ctx.term();
//                System.out.println("Socket closed & ZMQ context terminated");
//
//            }
//            System.out.println("[INFO] clean up thread stops.");
//
//        }
//
//    }
//
//    private static class SubscriberThread extends Thread{
//
//        ZMQ.Context ctx;
//        ZMQ.Socket socket;
//
//        ConcurrentHashMap<UUID, Long> hartbeatTimestamps = new ConcurrentHashMap<UUID,Long>();
//        CleanupThread cleanupThread = new CleanupThread(hartbeatTimestamps);
//
//        public SubscriberThread(){
//            ctx = ZMQ.context(1);
//            socket = ctx.socket(ZMQ.SUB);
//            socket.connect(Constants.mXPUB);
//            socket.subscribe("".getBytes());
//            System.out.println("[INFO] subscriber thread connected to :  " + Constants.mXPUB);
//            cleanupThread.start();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            cleanupThread.interrupt();
//
//        }
//        public Socket getSocket(){
//            return socket;
//        }
//        @Override
//        public void run(){
//
//            Message aMessage = new Message();
//            Calendar cal = Calendar.getInstance();
//            System.out.println("capture thread started");
//            while(!Thread.currentThread ().isInterrupted ()){
//                System.out.println("receiving topic...");
//                byte[] raw = socket.recv();
//                aMessage.topic = new String(raw);
//                if(aMessage.topic.equals(Constants.HEARTBEAT_TOPIC)){
//                    aMessage.type = socket.recv()[0];
//                    aMessage.timestamp = Message.deserializeTimestamp(socket.recv());
//                    aMessage.sender = new String(socket.recv());
//                    aMessage.payload = socket.recv();
//                    hartbeatTimestamps.put(java.util.UUID.fromString(aMessage.sender),System.currentTimeMillis());
//                    System.out.println("No of peers : "+hartbeatTimestamps.size());
//                    printMessage(aMessage);
//                }else if(aMessage.topic.equals(Constants.BROADCAST_TOPIC)) {
//                    aMessage.type = socket.recv()[0];
//                    aMessage.timestamp = Message.deserializeTimestamp(socket.recv());
//                    aMessage.sender = new String(socket.recv());
//                    aMessage.payload = socket.recv();
//                    printMessage(aMessage);
//                }else if(isUUID(aMessage.topic)){
//
//                    aMessage.type = socket.recv()[0];
//                    aMessage.timestamp = Message.deserializeTimestamp(socket.recv());
//                    aMessage.sender = new String(socket.recv());
//                    aMessage.payload = socket.recv();
//                    printMessage(aMessage);
//                }else{
//                    //TODO better handling
//                    System.out.println("[WARN] unknown topic detected.");
//                    // receive crap from unknown topic
//                    while(socket.hasReceiveMore()){
//                        socket.recv();
//                        System.out.println("unknown part ignored.");
//
//                    }
//    }
//            }
//            socket.close();
//            ctx.term();
//            System.out.println("Socket closed & ZMQ context terminated");
//            cleanupThread.interrupt();
//
//
//        }
//
//    }
//    private static boolean isUUID(String rawUUID){
//        try {
//            UUID parsed = java.util.UUID.fromString(rawUUID);
//            System.out.println("uuid: "+parsed.toString());
//            return true;
//        }catch(IllegalArgumentException ex){
//            return false;
//        }
//
//    }
//    private static void printMessage(Message msg){
//        System.out.println("*********************************************");
//        System.out.println("message topic   : "+msg.topic);
//        System.out.println("message type    : "+String.format("%02x", msg.type & 0xff));
//        System.out.println("message time    : "+msg.timestamp);
//        System.out.println("message sender  : "+msg.sender);
//        System.out.println("message payload : "+new String(msg.payload));
//        System.out.println("*********************************************");
//    }
}

