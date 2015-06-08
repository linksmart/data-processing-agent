package eu.linksmart.gc.api.network.routing;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

/**
 * The BackboneRouter is responsible for selecting the correct channel to send the message 
 * according to the receiverVirtualAddress.
 * 
 * A list of services is maintained that stores the communication channel. This list will 
 * always be updated which communication channel was used the last time for a specific 
 * VirtualAddress. Even if a VirtualAddress supports multiple communication channels the 
 * one will be used that this VirtualAddress sent data.
 * 
 */
public interface BackboneRouter {

	/**
	 * This method checks by which channel the receiver is reachable and sends
	 * the message.
	 * 
	 * @param senderVirtualAddress {@link VirtualAddress} of the sender
	 * @param receiverVirtualAddress {@link VirtualAddress} of the receiver
	 * @param message message to be send
	 * @return {@link NMResponse} object containing the send status
	 */
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] protectedData);
	
	/**
	 * This method checks by which channel the receiver is reachable and sends
	 * the message.
	 * 
	 * @param senderVirtualAddress {@link VirtualAddress} of the sender
	 * @param receiverVirtualAddress {@link VirtualAddress} of the receiver
	 * @param message message to be send
	 * @return {@link NMResponse} object containing the send status
	 */
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] protectedData);

	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of services and which backbone
	 * they use.
	 * 
	 * @param senderVirtualAddress {@link VirtualAddress} of the sender
	 * @param receiverVirtualAddress {@link VirtualAddress} of the receiver
	 * @param data to be received
	 * @param originatingBackbone backbone from which this backbone receives the 
	 * 		message
	 * @return {@link NMResponse} object containing the send status
	 */
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data,
			Backbone originatingBackbone);

	/**
	 * Receives a message which also specifies the communication channel used by
	 * the sender. This will then update the list of services and which backbone
	 * they use.
	 * 
	 * @param senderVirtualAddress {@link VirtualAddress} of the sender
	 * @param receiverVirtualAddress {@link VirtualAddress} of the receiver
	 * @param data to be received
	 * @param originatingBackbone backbone from which this backbone receives the 
	 * 		message
	 * @return {@link NMResponse} object containing the send status
	 */
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data,
			Backbone originatingBackbone);

	/**
	 * Adds a new route to the BackboneRouter. This will succeed if (and only
	 * if) a route to the VirtualAddress does not exist yet. If the backbone is 
	 * unavailable a potential route is registered, which becomes active as soon 
	 * as the indicated backbone could be bound. 
	 * 
	 * @param virtualAddress
	 *            the {@link VirtualAddress} of which the route is added
	 * @param backbone
	 *            the Backbone through which the VirtualAddress can be reached
	 * @return whether adding the route was successful (at the moment it is only 
	 * 			  possible to have one route for a VirtualAddress.
	 * @see {@link getAvailableCommunicationChannels()}
	 */
	public boolean addRoute(VirtualAddress virtualAddress, String backbone);
	
	/**
	 * Adds the backbone route for a remote VirtualAddress. Uses the backbone of 
	 * the senderVirtualAddress, which should be a remote NetworkManager. 
	 * 
	 * @param senderVirtualAddress the {@link VirtualAddress} of the sender. Usually 
	 * 			a remote NetworkManager
	 * @param remoteVirtualAddress the {@link VirtualAddress} of a remote service.
	 */
	public void addRouteForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress);

	/**
	 * Adds a new route to the BackboneRouter. In addition, the endpoint is
	 * propagated to the Backbone.
	 * 
	 * @param virtualAddress
	 *            the {@link VirtualAddress} of which the route is added
	 * @param backbone
	 *            the Backbone through which the VirtualAddress can be reached
	 * @param endpoint
	 * @return whether adding the route was successful
	 */
	public boolean addRouteToBackbone(VirtualAddress virtualAddress, String backbone, String endpoint);

	/**
	 * Removes a route from the BackboneRouter, if the VirtualAddress was reached through
	 * the given backbone
	 * 
	 * @param virtualAddress 
	 *            The VirtualAddress of which the route should be removed
	 * @param backbone
	 *            The name of the backbone through which the VirtualAddress was reached
	 *            If null the route is removed independent from the backbone
	 * @return whether removing the route was successful
	 */
	public boolean removeRoute(VirtualAddress virtualAddress, String backbone);

	/**
	 * Returns a list of backbones available to the network manager.
	 * 
	 * @return list of backbone names (IDs)
	 */
	public List<String> getAvailableBackbones();

	/**
	 * this method is invoked by NMCore to broadcast services.
	 * 
	 * @param sender {@link VirtualAddress} of the sender
	 * @param data data to be broadcasted
	 * @return {@link NMResponse} object containing the status of the broadcast
	 */
	public NMResponse broadcastData(VirtualAddress sender, byte[] data);

	/**
	 * update the backbone configuration
	 * @param backbone configuration from the status page.
	 */
	public void applyConfigurations(Hashtable updates);
	
	/**
	 * Returns a list of security properties available via a given backbone. This is 
	 * necessary because we do not know what backbones we have, and there is no point 
	 * in creating backbones on the fly to ask them about the security types they provide
	 * @param backbone A string with the (class)name of the backbone we are interested 
	 * 			in. This must be loaded already
	 * @return a list of security parameters configured for that backbone. See the 
	 * 			backbone's parameters file and/or the configuration interface for more details
	 */
	public List<SecurityProperty> getBackboneSecurityProperties(String backbone);

	/**
	 * Returns the route to a virtual address. This method is needed by the network 
	 * manager status page. 
	 * @param virtualAddress {@link VirtualAddress} for which the route is required
	 * @return String of the form: "BackboneType:BackboneAddress"
	 */
	public String getRoute(VirtualAddress virtualAddress);

	/**
	 * Returns a copy of the active route map.
	 * @return copy of the active route map
	 */
	Map<VirtualAddress, Backbone> getCopyOfActiveRouteMap();

	/**
	 * Returns a copy of the potential route map
	 * @return copy of the the potential route map
	 */
	Map<VirtualAddress, List<RouteEntry>> getCopyOfPotentialRouteMap();
	
	/**
	 * This method returns information about the backbone of the VirtualAddress.
	 * The return format is backbone class name.
	 * 
	 * @param virtualAddress VirtualAddress of the node to request the route from
	 * @return Backbone name
	 */
	String getRouteBackbone(VirtualAddress virtualAddress);
	
}
