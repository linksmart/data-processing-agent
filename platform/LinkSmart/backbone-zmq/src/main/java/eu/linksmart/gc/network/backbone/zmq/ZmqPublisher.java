package eu.linksmart.gc.network.backbone.zmq;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;

public class ZmqPublisher {
	
private static Logger LOG = Logger.getLogger(ZmqPublisher.class.getName());
	
	private ZmqHandler zmqHandler = null;
	
	private ZMQ.Context pub_context = null;
	private ZMQ.Socket publisher = null;
	
	public ZmqPublisher(ZmqHandler zmqHandler) {
		this.zmqHandler = zmqHandler;
	}
	
	public void startPublisher() {
		try {
			pub_context = ZMQ.context(1);
			publisher = pub_context.socket(ZMQ.PUB);
			publisher.connect(this.zmqHandler.getXSubUri());
		} catch (Exception e) {
			LOG.error("error in initializing ZmqPublisher: " + e.getMessage(), e);
		}	
	}
	
	public void stopPublisher() {
		try {
			if(pub_context != null && publisher != null) {
				publisher.setLinger(100);
				publisher.close();
				pub_context.term();
			}
		} catch (Exception e) {
			LOG.error("error in stopping ZmqPublisher: " + e.getMessage(), e);
		}
		
	}
	
	public boolean publish(ZmqMessage zmqMessage) {
		try {
			publisher.sendMore(zmqMessage.getTopic());
			publisher.sendMore(new byte[]{zmqMessage.getProtocolVersion()});
			publisher.sendMore(new byte[]{zmqMessage.getType()});
			publisher.sendMore(ByteBuffer.allocate(8).putLong(zmqMessage.getTimeStamp()).array());
			publisher.sendMore(zmqMessage.getSender());
			publisher.sendMore(zmqMessage.getRequestID());
			publisher.send(zmqMessage.getPayload(), 0);
			return true;
		} catch (Exception e) {
			LOG.error("error in publishing message: " + e.getMessage(), e);
		}
		return false;
	}

}
