package eu.linksmart.gc.network.backbone.zmq;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

public class HeartBeat extends Thread {
	
	private static Logger LOG = Logger.getLogger(HeartBeat.class.getName());
	
	private ZmqHandler zmqHandler = null;

	private boolean isRunning = false;
	
	private String peerID = null;
	private int heartbeatInterval = 5000;
	
	ZMQ.Context context = null;
	ZMQ.Socket publisher = null;
	
	public HeartBeat(ZmqHandler zmqHandler) {
		this.zmqHandler = zmqHandler;
		this.peerID = this.zmqHandler.getPeerID();
		this.heartbeatInterval = this.zmqHandler.getHeartBeatInterval();
		this.isRunning = true;
	}
	
    public void run() {
    
    	try {
			
        	//
    		// prepare context & publisher to send heartbeat messages to proxy
    		//
        	context = ZMQ.context(1);
        	publisher = context.socket(ZMQ.PUB);
    		publisher.connect(zmqHandler.getXSubUri()); 
    		LOG.debug("[" + peerID + "] initialized publisher to send heartbeat to proxy");
    		
        	while(this.isRunning) {
        		LOG.trace("[" + peerID + "] is sending heartbeat to "+zmqHandler.getXSubUri());
    			publisher.sendMore(ZmqConstants.HEARTBEAT_TOPIC);
    			publisher.sendMore(new byte[]{ZmqConstants.PROTOCOL_VERSION});
    			publisher.sendMore(new byte[]{ZmqConstants.MESSAGE_TYPE_HEARTBEAT});
    			publisher.sendMore("" + System.currentTimeMillis());
    			publisher.sendMore(peerID);
    			publisher.sendMore("");
    			publisher.send("".getBytes(), 0);
    			Thread.sleep(heartbeatInterval);
        	}
        	
		} catch (InterruptedException ex) {
            LOG.error("ZmqHeartbeat: interrupt signal received.", ex);
        } catch (Exception e) {
        	LOG.error("ZmqHeartbeat: exception: " + e.getMessage(), e);
		}
    	
    	closeZmqContext();
    	
    	LOG.info("[" + peerID + "] ZmqHeartbeat is stopped");
    }
    
    public void setIsRunning(boolean flag) {
    	this.isRunning = flag;
    }
    
    private void closeZmqContext() {
    	if(context != null && publisher != null) {
    		publisher.setLinger(1);
        	publisher.close();
    		context.term();
    		LOG.debug("[" + peerID + "] ZmqHeartbeat context is closed");
    	}
    }
}
