package eu.linksmart.gc.network.backbone.zmq;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public class ZmqReceiver extends Thread {
	
	private static Logger LOG = Logger.getLogger(ZmqReceiver.class.getName());
	
	private ZmqHandler zmqHandler = null;
	
	private String peerID = null;
	private boolean isRunning = false;
	
	private ZMQ.Context context = null;
	private ZMQ.Socket subscriber = null;

	public ZmqReceiver(ZmqHandler zmqHandler) {
		this.zmqHandler = zmqHandler;
		this.peerID = this.zmqHandler.getPeerID();
		isRunning = true;
	}
	
	public void run() {
		
		try {
					
			//
			// prepare context & subscriber(s) to receive messages from other peers
			//
			context = ZMQ.context(1);
			subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(zmqHandler.getXPubUri()); 
			
			subscriber.subscribe(ZmqConstants.BROADCAST_TOPIC.getBytes());
			LOG.debug("[" + peerID + "] is subscribed to topic: BROADCAST");
			
			subscriber.subscribe(peerID.getBytes());
			LOG.debug("[" + peerID + "] is subscribed to own topic");
			
	    	while(this.isRunning) {
	    		try {
	    			String topic = subscriber.recvStr();
		    		subscriber.recv(); // for byte[] protocol
					byte type = subscriber.recv()[0];
					byte[] timeStamp = subscriber.recv();
					String sender = subscriber.recvStr();
					String requestID = subscriber.recvStr();
					byte[] payload = subscriber.recv();
					zmqHandler.notify(new ZmqMessage(topic, type, ByteBuffer.wrap(timeStamp).getLong(), sender, requestID, payload));
				} catch (ZMQException e) {
					if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
		                LOG.debug("ZmqReceiver: termination interrupt received");
		            } else {
		                LOG.error("ZmqReceiver: ZmqException: " + e.getMessage(), e);
		            }
				} catch (Exception e) {
					LOG.error("ZmqReceiver: exception: " + e.getMessage(), e);
				}	
	    	}
	    	
		} catch (Exception e) {
			LOG.error("ZmqReceiver-1: exception: " + e.getMessage(), e);
		}
		
		if(this.isRunning)
			stopReceiver();
		
		LOG.info("[" + peerID + "] ZmqReceiver is stopped");
    }

    public void stopReceiver() {
    	this.isRunning = false;
    	if(context != null && subscriber != null) {
    		subscriber.close();
    		context.term();
    		LOG.info("[" + peerID + "] ZmqReceiver context is closed");
    	}
    }
}
