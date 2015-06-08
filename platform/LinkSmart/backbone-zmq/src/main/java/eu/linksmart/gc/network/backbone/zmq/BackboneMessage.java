package eu.linksmart.gc.network.backbone.zmq;

import eu.linksmart.gc.api.network.VirtualAddress;

public class BackboneMessage {
	
	private VirtualAddress senderVirtualAddress = null;
	private VirtualAddress receiverVirtualAddress = null;
	private byte[] data = null;
	
	public String messageType = null;
	
	private boolean isSync = true;
	
	public BackboneMessage(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		this.senderVirtualAddress = senderVirtualAddress;
		this.receiverVirtualAddress = receiverVirtualAddress;
		this.data = data;
	}
	
	public BackboneMessage(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data, boolean isSync) {
		this.senderVirtualAddress = senderVirtualAddress;
		this.receiverVirtualAddress = receiverVirtualAddress;
		this.data = data;
		this.isSync = isSync;
	}
	
	public VirtualAddress getSenderVirtualAddress() {
		return this.senderVirtualAddress;
	}
	
	public VirtualAddress getReceiverVirtualAddress() {
		return this.receiverVirtualAddress;
	}
	
	public byte[] getPayload() {
		return this.data;
	}
	
	public void setPayload(byte[] newPayload) {
		this.data = newPayload;
	}
	
	public boolean isSync() {
		return this.isSync;
	}
	
	public void setBBMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public String getBBMessageType() {
		return this.messageType;
	}
	
}
