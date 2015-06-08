package eu.linksmart.gc.network.backbone.zmq;

public class ZmqMessage {
	
	private String topic;
	private byte protocolVersion = ZmqConstants.PROTOCOL_VERSION;
	private byte type;
	private long timeStamp;
	private String sender;
	public String requestID;
	private byte[] payload;
	
	public ZmqMessage() {	
	}
	
	public ZmqMessage(String topic, byte type, long timeStamp, String sender, String requestID, byte[] payload) {
		this.topic = topic;
		this.type = type;
		this.timeStamp = timeStamp;
		this.sender = sender;
		this.requestID = requestID;
		this.payload = payload;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getTopic() {
		return this.topic;
	}
	
	public void setProtocolVersion(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
	}
	
	public byte getProtocolVersion() {
		return this.protocolVersion;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public byte getType() {
		return this.type;
	}
	
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public long getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	
	public String getRequestID() {
		return this.requestID;
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
}
