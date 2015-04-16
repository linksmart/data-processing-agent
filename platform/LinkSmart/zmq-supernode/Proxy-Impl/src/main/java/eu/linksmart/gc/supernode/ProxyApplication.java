package eu.linksmart.gc.supernode;

import org.apache.log4j.Logger;

/**
 * Created by carlos on 03.12.14.
 */
public class ProxyApplication {

    private static Logger LOG = Logger.getLogger(ProxyApplication.class.getName());

    public static void main (String[] args) throws InterruptedException {

        int xSubPort = 7000;
        int xPubPort = 7001;
        String address = "127.0.0.1";

        // check if all parameters are provided
        if(args.length>0) {
            // parse the IP string
            if (!validIP(args[0])) {
                LOG.warn("Provided IP address invalid. Using IP        :" + address);
            } else {
                LOG.debug("using provided IP        :" + args[0]);
                address = args[0];
            }
            // parse the port strings
            if(args.length==3){
                try {
                    xSubPort = Integer.valueOf(args[1]);
                    xPubPort = Integer.valueOf(args[2]);
                    // check port range
                    if( (xSubPort<1024) || (xSubPort>65535)){
                        LOG.warn("XSUB port not in allowed range.");
                        xSubPort = 7000;
                    }
                    if( (xPubPort<1024) || (xPubPort>65535)){
                        LOG.warn("XPUB port not in allowed range.");
                        xPubPort = 7001;
                    }
                    if(xSubPort==xPubPort){
                        LOG.warn("XPUB and XSUB identical.");
                        xSubPort = 7000;
                        xPubPort = 7001;
                    }
                }catch(java.lang.NumberFormatException ex){
                    LOG.warn("Provided port(s) invalid.",ex);
                }
            }else{
                LOG.info("No ports specified.");
            }
            LOG.debug("using XSUB port :"+xSubPort);
            LOG.debug("using XPUB port :"+xPubPort);

        }


        // initialize proxy
        Proxy proxy = new Proxy(address, xSubPort, xPubPort);
        // attach shutdown hook
        ShutdownHook sh = new ShutdownHook(proxy);
        Runtime.getRuntime().addShutdownHook(sh);
        LOG.debug("shutdown hook registered.");
        // start proxy
        LOG.info("starting ZMQ backbone...");
        proxy.startProxy();

        while(true){
            Thread.sleep(30000);
            LOG.trace("ZMQ proxy thread alive       : "+proxy.proxyThreadAlive());
            LOG.trace("traffic watch thread alive   : "+proxy.trafficWatchAlive());
            LOG.trace("heartbeat watch thread alive : "+proxy.heartbeatWatchAlive());
        }
    }
    public static class ShutdownHook extends Thread{
            Proxy mProxy;
            public ShutdownHook(Proxy aProxy){
                mProxy = aProxy;
            }
            @Override
            public void run() {
                    LOG.warn("CTRL-C intercepted");
                    mProxy.stopProxy();
                    LOG.info("ZMQ backbone terminated");
            }
    }
    public static boolean validIP (String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }
            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }
            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}

