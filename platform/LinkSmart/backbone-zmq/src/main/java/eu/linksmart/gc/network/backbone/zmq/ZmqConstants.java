package eu.linksmart.gc.network.backbone.zmq;

public class ZmqConstants {
	
	public static final byte PROTOCOL_VERSION = 0x01;
	
	public static final String HEARTBEAT_TOPIC = "HEARTBEAT";
	public static final String BROADCAST_TOPIC = "BROADCAST";
	
	public static final byte MESSAGE_TYPE_UNICAST_REQUEST = 0x01;
	public static final byte MESSAGE_TYPE_UNICAST_RESPONSE = 0x02;
	public static final byte MESSAGE_TYPE_HEARTBEAT = 0x03;
	public static final byte MESSAGE_TYPE_PEER_DOWN = 0x04;
	public static final byte MESSAGE_TYPE_SERVICE_DISCOVERY = 0x05;
	
}
