package eu.linksmart.gc.api.network.backbone;

import java.util.List;

import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

/**
 * A Backbone needs to be implemented if a new communication channel (e.g. P2P, JMS, ...) should be used by LinkSmart.
 * It should always have a static 1..1 service reference to the BackboneRouter to ensure that the BackboneRouter
 * gets informed about its loading.
 *
 */
public interface Backbone {

	/**
	 * Sends a message over the specific communication channel and blocks until response comes.
	 * 
	 * @param senderVirtualAddress VirtualAddress of the sender
	 * @param receiverVirtualAddress VirtualAddress of the receiver
	 * @param data data to be sent
	 * @return Response of the receiver
	 */
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);

	/**
	 * Sends a message over the specific communication channel and immediately returns.
	 * 
	 * @param senderVirtualAddress VirtualAddress of the sender
	 * @param receiverVirtualAddress VirtualAddress of the receiver
	 * @param data data to be sent
	 * @return {@link NMResponse} of the receiver
	 */
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data data to be received
	 * @return {@link NMResponse} to message including the status
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);
	
	/**
	 * Receives a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param data data to be received
	 * @return {@link NMResponse} includes status of sending attempt
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data);

	/**
	 * Broadcasts a message over the specific communication channel.
	 * 
	 * @param senderVirtualAddress
	 * @param data data to be broadcasted
	 * @return {@link NMResponse} to broadcasted data
	 */
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data);

	/**
	 * Return the destination address as string that will be used for display
	 * purposes.
	 * 
	 * @param virtualAddreass identifier of type {@link VirtualAddress} 
	 * @return the backbone address represented by the virtual address as String
	 */
	public String getEndpoint(VirtualAddress virtualAddress);

	/**
	 * Adds a new endpoint/ destination address to the backbone.
	 * 
	 * @param virtualAddress
	 *            the {@link VirtualAddress} that represents the endpoint
	 * @param endpoint
	 *            the endpoint to be reached, in a format that is specific to
	 *            the Backbone implementation, as a String
	 * @return true if adding the endpoint was successful, false otherwise
	 */
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint);

	/**
	 * Removes an endpoint from the backbone
	 * @param virtualAddress the {@link VirtualAddress} of which the endpoint should be removed
	 * @return true if removing the endpoint was successful, false otherwise
	 */
	public boolean removeEndpoint(VirtualAddress virtualAddress);
	
	/**
	 * Returns the name of the backbone implementation's class name
	 * @return Backbone implementation's class name
	 */
	public String getName();
	
	/**
	 * Returns security types required by using this backbone implementation.
	 * The security types are configured via the LS configuration interface.
	 * See resources/BBJXTA.properties for details on configuration
	 * @return a list of required {@link SecurityProperty}s 
	 */
	public List<SecurityProperty> getSecurityTypesRequired();
	
	/**
	 * Adds a new endpoint for a remote service to the backbone. 
	 * @param senderVirtualAddress the {@link VirtualAddress} of the network 
	 * 		manager.
	 * @param remoteVirtualAddress the {@link VirtualAddress} of the service that is 
	 * 		connected to the remote network manager. Endpoints of remote services 
	 * 		that are sent during the backbone advertisement are added to the backbone. 
	 * 		This is needed since the virtual addresses are packed in the message of 
	 * 		the advertisement.
	 */
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress);
}
