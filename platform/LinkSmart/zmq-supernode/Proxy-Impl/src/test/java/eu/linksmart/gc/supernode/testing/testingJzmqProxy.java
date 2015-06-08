package eu.linksmart.gc.supernode.testing;



        import org.zeromq.ZMQ;

public class testingJzmqProxy {

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        int i = 0;
        while(true) {
            ProxyThread p = new ProxyThread();
            p.start();
            System.out.println("proxy thread started : "+i);
            Thread.sleep(1000);
            p.stopProxyThread();
            p.join();
            System.out.println("proxy thread stopped.");
            i++;
        }

        //System.exit(0);


    }
    public static class ProxyThread extends Thread{
                private ZMQ.Context ctx;
        private ZMQ.Socket xsubSocket;
        private ZMQ.Socket xpubSocket;

        @Override
        public void run() {

            ctx = ZMQ.context(1);
            xsubSocket = ctx.socket(ZMQ.XSUB);
            xsubSocket.bind("tcp://127.0.0.1:7000");

            xpubSocket = ctx.socket(ZMQ.XPUB);
            xpubSocket.bind("tcp://127.0.0.1:7001");


            try {
                System.out.println("running proxy...");
                ZMQ.proxy(xsubSocket, xpubSocket, null);

            }catch(Exception ex){

            }

        }
        public void stopProxyThread(){


            xpubSocket.setLinger(0);
            xpubSocket.unbind("tcp://127.0.0.1:7001");

            xpubSocket.close();


            xsubSocket.setLinger(0);
            xsubSocket.unbind("tcp://127.0.0.1:7000");

            xsubSocket.close();

            ctx.close();


        }

    }

}