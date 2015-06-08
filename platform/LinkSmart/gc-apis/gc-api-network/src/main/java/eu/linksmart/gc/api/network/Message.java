package eu.linksmart.gc.api.network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * An OO representation of a message received over the network. This class is
 * used for internal processing by the modules of NetworkManagerCore.
 * 
 * @author Vinkovits
 * 
 */
public class Message {

	private Properties properties = new Properties();
	private String topic = null;
	private byte[] data = null;
	private VirtualAddress senderVirtualAddress = null;
	private VirtualAddress receiverVirtualAddress = null;

	public final static String TOPIC_APPLICATION = "eu.linksmart.application";
	public final static String TOPIC_CONNECTION_HANDSHAKE = "eu.linksmart.network.connection.handshake";
	public final static String SESSION_ID_KEY = "sessionId";


	/**
	 * 
	 * @param topic the topic of this message
	 * @param senderVirtualAddress VirtualAddress of the sender of this message
	 * @param receiverVirtualAddress VirtualAddress of the receiver of this message. Can be null e.g. in the case of a {@link BroadcastMessage}
	 * @param data payload of the message. TODO Marco-2012-02-02: Can payload be empty? Like for advertisements
	 */
	public Message(String topic, VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		if (StringUtils.isEmpty(topic) || senderVirtualAddress == null || data == null)
			throw new IllegalArgumentException(
					"Message cannot have null for required fields");
		this.topic = topic;
		this.senderVirtualAddress = senderVirtualAddress;
		this.receiverVirtualAddress = receiverVirtualAddress;
		this.data = data;
	}

	/**
	 * Sets a property of the message which will be included in the data to be
	 * sent.
	 * 
	 * @param key
	 *            Key to the property
	 * @param value
	 *            Value of the property in a serialized way in which it can be
	 *            sent over network
	 * @return The previous value of the property or null if there was none
	 */
	public String setProperty(String key, String value) {
		return (String) properties.setProperty(key, value);
	}

	/**
	 * Returns actual value of property.
	 * 
	 * @param key
	 *            Key of the property
	 * @return Value of the property
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Returns the properties saved in the message
	 * 
	 * @return Set with the keys
	 */
	public Set<String> getKeySet() {
		Set<String> stringSet = new HashSet<String>();
		Iterator<Object> i = properties.keySet().iterator();
		while (i.hasNext()) {
			stringSet.add((String) i.next());
		}
		return stringSet;
	}

	public String getTopic() {
		return this.topic;
	}

	public VirtualAddress getSenderVirtualAddress() {
		return senderVirtualAddress;
	}

	public VirtualAddress getReceiverVirtualAddress() {
		return receiverVirtualAddress;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Topic: ").append(getTopic()).append(", Sender: ").append(
				getSenderVirtualAddress()).append(", Receiver: ").append(getReceiverVirtualAddress())
				.append(", Data: ").append(new String(data));
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!obj.getClass().equals(this.getClass())) return false;
		Message msg = (Message)obj;
		boolean fieldsEqual = this.getTopic().equals(msg.getTopic()) &&
				Arrays.equals(this.getData(), msg.getData()) &&
				this.getSenderVirtualAddress().equals(msg.getSenderVirtualAddress());
		if(!fieldsEqual) return false;
		//receiver could be null in case of broadcast so we check it separately
		if(this.getReceiverVirtualAddress() != null && msg.getReceiverVirtualAddress() != null) {
			return  this.getReceiverVirtualAddress().equals(msg.getReceiverVirtualAddress());
		} else if (this.getReceiverVirtualAddress() != msg.getReceiverVirtualAddress() ){
			return false;
		} else {
			//this case means both should be null but we check it
			return this.getReceiverVirtualAddress() == null && msg.getReceiverVirtualAddress() == null;
		}
	}
}
