package eu.linksmart.gc.network.connection;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.VirtualAddress;

public class HandshakeConnection extends Connection{	
	private boolean resolved = false;
	private boolean failed = false;
	private ConnectionManager conMgr;

	public HandshakeConnection(VirtualAddress clientVirtualAddress, VirtualAddress serverVirtualAddress, ConnectionManager conMgr) {
		super(clientVirtualAddress, serverVirtualAddress);
		this.conMgr = conMgr;
	}

	@Override
	public Message processData(VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress, byte[] data) {
		Message msg = MessageSerializerUtiliy.unserializeMessage(
				data,
				true,
				senderVirtualAddress,
				receiverVirtualAddress, true);
		if(msg instanceof ErrorMessage) {
			return msg;
		}
		if(msg.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)) {
			return msg;
		} else {
			synchronized(this) {
				while(!resolved) {
					try {
						this.wait(10000);
					} catch (InterruptedException e) {
						//nothing to handle
					}
				}
			}
			if(!failed) {
				//get the newly created properly set up connection to process the messages
				return conMgr.
						getConnection(
								this.getServerVirtualAddress(),
								this.getClientVirtualAddress()).
								processData(senderVirtualAddress, receiverVirtualAddress, data);
			} else {
				return null;
			}
		}
	}

	@Override
	public byte[] processMessage(Message msg) throws Exception {
		if(msg.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)) {
			return MessageSerializerUtiliy.serializeMessage(msg, true, true);
		} else {
			synchronized(this) {
				while(!resolved) {
					try {
						this.wait(10000);
					} catch (InterruptedException e) {
						//nothing to handle
					}
				}
			}
			if(!failed) {
				//get the newly created properly set up connection to process the messages
				return conMgr.
						getConnection(
								this.getServerVirtualAddress(),
								this.getClientVirtualAddress())
								.processMessage(msg);
			} else {
				return null;
			}
		}
	}

	public synchronized void setStateResolved() {
		resolved = true;
		notifyAll();
	}

	public synchronized void setFailed() {
		failed = true;
		setStateResolved();
	}
}
