package eu.linksmart.gc.api.network.networkmanager.core;

import java.rmi.RemoteException;
import java.util.List;

import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

/**
 * Internal NetworkManager interface used by internal components such as the backbone router etc.
 **/
public interface NetworkManagerCore extends NetworkManager {

	/**
	 * Broadcast a message to all other known LinkSmart nodes.
	 * @param message the Message to broadcast; receiver VirtualAddress will be ignored.
	 * @return {@link NMResponse} of the broadcast
	 * @throws RemoteException
	 */
	public NMResponse broadcastMessage(Message message);

	/**
	 * Send message from one LinkSmart node to another node.
	 * @param message {@link Message} to be send
	 * @param synch indicating if message should be send synchronously
	 * @return {@link NMResponse} regarding the sent message
	 * @throws RemoteException
	 */
	public NMResponse sendMessage(Message message, boolean synch);

	
	/**
	 * Receive data from one LinkSmart node to another node synchronously.
	 * @param senderVirtualAddress the {@link VirtualAddress} of the sender. 
	 * @param remoteVirtualAddress the {@link VirtualAddress} of the remote service.
	 * @param data data if no connection already exists, message needed for handshake
	 * @return {@link NMResponse} includes the response to the message
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte [] data);
	
	/**
	 * Receive data from one LinkSmart node to another node asynchronously.
	 * @param senderVirtualAddress the {@link VirtualAddress} of the sender. 
	 * @param remoteVirtualAddress the {@link VirtualAddress} of the remote service.
	 * @param data if no connection already exists, message needed for handshake
	 * @return {@link NMResponse} includes only status of delivery attempt
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte [] data);

	
	/**
	 * Adds a VirtualAddress of a remote service.  
	 * @param senderVirtualAddress the {@link VirtualAddress} of the sender. Usually a remote NetworkManager
	 * @param remoteVirtualAddress the {@link VirtualAddress} of the remote service.
	 */
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress);

	/**
	 * Informs if the security properties of any VirtualAddress changed
	 * because of change in the Backbone.
	 * @param virtualAddressesToUpdate {@link VirtualAddress} which changed
	 * @param properties {@link SecurityProperty}s of the virtual address
	 */
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate, List<SecurityProperty> properties);	
}
