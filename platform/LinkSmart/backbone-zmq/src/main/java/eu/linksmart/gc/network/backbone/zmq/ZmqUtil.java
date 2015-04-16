package eu.linksmart.gc.network.backbone.zmq;

import org.apache.commons.lang.ArrayUtils;

import eu.linksmart.gc.api.network.VirtualAddress;

public class ZmqUtil {
	
	public static byte[] addSenderVADToPayload(BackboneMessage bbMessage) {
		return ArrayUtils.addAll(bbMessage.getSenderVirtualAddress().getBytes(),bbMessage.getPayload());
	}
	
	public static VirtualAddress getSenderVAD(byte[] origData) {
		byte[] ret = new byte[VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH];
		System.arraycopy(origData, 0, ret, 0, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH - 0);
		return new VirtualAddress(ret);
	}
	
	public static byte[] removeSenderVAD(byte[] origData) {
		int lengthWithoutVirtualAddress = origData.length - VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH;
		byte[] ret = new byte[lengthWithoutVirtualAddress];
		System.arraycopy(origData, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH, ret, 0, origData.length - VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH);
		return ret;
	}
	
	public static byte[] addVADsToPayload(BackboneMessage bbMessage) {
		byte[] payloadWithReceiverVAD = ArrayUtils.addAll(bbMessage.getReceiverVirtualAddress().getBytes(),bbMessage.getPayload());
		byte[] payloadWithVADs = ArrayUtils.addAll(bbMessage.getSenderVirtualAddress().getBytes(),payloadWithReceiverVAD);
		return payloadWithVADs;
	}
	
	public static byte[] removeVADsFromPayload(byte[] payload) {
		int virtualAddressesLength = VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH * 2;
		int lengthWithoutVirtualAddress = payload.length - virtualAddressesLength;
		byte[] ret = new byte[lengthWithoutVirtualAddress];
		System.arraycopy(payload, virtualAddressesLength, ret, 0, payload.length - virtualAddressesLength);
		return ret;
	}
	
	public static VirtualAddress getReceiverVAD(byte[] origData) {
		//
		// receiver virtual address is after sender virtual address in payload
		// 
		byte[] ret = new byte[VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH];
		System.arraycopy(origData, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH, ret, 0, VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH - 0);
		return new VirtualAddress(ret);
	}
	
	public static ZmqMessage createBroadcastMessage(String peerID, byte[] payload) {
		return new ZmqMessage(ZmqConstants.BROADCAST_TOPIC, ZmqConstants.MESSAGE_TYPE_SERVICE_DISCOVERY, System.currentTimeMillis(), peerID, "", payload);
	}
	
	public static ZmqMessage createRequestMessage(String senderPeerID, String receiverPeerID, String requestID, byte[] payload) {
		return new ZmqMessage(receiverPeerID, ZmqConstants.MESSAGE_TYPE_UNICAST_REQUEST, System.currentTimeMillis(), senderPeerID, requestID, payload);
	}
	
	public static ZmqMessage createResponseMessage(String senderPeerID, String receiverPeerID, String requestID, byte[] payload) {
		return new ZmqMessage(receiverPeerID, ZmqConstants.MESSAGE_TYPE_UNICAST_RESPONSE, System.currentTimeMillis(), senderPeerID, requestID, payload);
	}
	
}
