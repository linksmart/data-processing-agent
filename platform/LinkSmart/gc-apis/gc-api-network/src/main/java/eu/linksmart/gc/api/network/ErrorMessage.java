package eu.linksmart.gc.api.network;

public class ErrorMessage extends Message {
	
	/**
	 * Indicates error receiving message
	 */
	public final static String RECEPTION_ERROR = "ReceptionError";
	/**
	 * Indicates error consuming message
	 */
	public final static String ERROR = "ERROR";
	
	public ErrorMessage(String topic, VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data) {
		super(topic, senderVirtualAddress, receiverVirtualAddress, data);
	}
}
