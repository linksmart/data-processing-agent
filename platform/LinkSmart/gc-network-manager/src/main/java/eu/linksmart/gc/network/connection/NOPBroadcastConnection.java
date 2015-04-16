package eu.linksmart.gc.network.connection;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.Message;

/**
 * A connection which is a broadcast connection but does nothing with the packages.
 * @author Vinkovits
 *
 */
public class NOPBroadcastConnection extends BroadcastConnection {
	public NOPBroadcastConnection(VirtualAddress serverVirtualAddress) {
		super(serverVirtualAddress);
	}

	public byte[] processMessage(Message msg) throws Exception{
		return msg.getData();
	}
	
	public Message processData(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data){
		return new Message(Message.TOPIC_APPLICATION, senderVirtualAddress, receiverVirtualAddress, data);
	}
}
