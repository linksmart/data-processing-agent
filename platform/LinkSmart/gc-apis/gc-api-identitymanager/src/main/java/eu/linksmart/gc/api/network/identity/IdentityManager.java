package eu.linksmart.gc.api.network.identity;

import java.util.Properties;
import java.util.Set;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.utils.Part;

/**
 * The IdentityManager is responsible for creating and storing {@link VirtualAddress}.
 * 
 */
public interface IdentityManager {
	public static long SERVICE_RESOLVE_TIMEOUT = 30000;
	
	
	/**
	 * Creates a local {@link VirtualAddress} with the given attributes and stores it. It
	 * creates a random deviceID. The result will be 0.0.0.randomDeviceId.
	 * Attributes should be one of {@link ServiceAttribute}.
	 * 
	 * @param attributes
	 *            Attributes describing the VirtualAddress. E.g. description.
	 * @return the {@link VirtualAddress} that has been created.
	 */
	public Registration createServiceByAttributes(Part[] attributes);

	/**
	 * Creates a local {@link VirtualAddress} without any context and stores. It creates a
	 * random deviceID. The result will be 0.0.0.randomDeviceId.
	 * 
	 * @deprecated The more general createServiceByAttributes should be used with 
	 * a description attribute
	 * @param description
	 *            the description
	 * @return the {@link Registration} created
	 */
	@Deprecated
	public Registration createServiceByDescription(String description);

	/**
	 * Updates the attributes of the given {@link VirtualAddress}.
	 * 
	 * @param virtualAddress
	 *            The {@link VirtualAddress} for which the attributes should be changed.
	 * @param attributes
	 *            New {@link Properties} that will replace the old attributes.
	 * @return true, if successful
	 */
	public boolean updateServiceInfo(VirtualAddress virtualAddress, Properties attributes);

	/**
	 * Returns the {@link Registration} for a given {@link VirtualAddress}.
	 * @param virtualAddress
	 *            The {@link VirtualAddress} for which the attributes should be changed.
	 * @return the  {@link Registration}
	 */
	public Registration getServiceInfo(VirtualAddress virtualAddress);

	/**
	 * Returns all local and remote {@link Registration}s that are stored by this
	 * IdentityManager.
	 * 
	 * @return all {@link Registration}s
	 */
	public Set<Registration> getAllServices();

	/**
	 * Returns all local {@link Registration}s that are currently stored by this
	 * IdentityManager. Local are all {@link Registration}s that are registered at
	 * the associated NetworkManager.
	 * 
	 * @return all local {@link Registration}s.
	 */
	public Set<Registration> getLocalServices();

	/**
	 * Returns all remote {@link Registration}s that are currently stored by this
	 * IdentityManager. Remote are all {@link Registration}s that are registered at
	 * remote NetworkManagers.
	 * 
	 * @return all remote {@link Registration}s.
	 */
	public Set<Registration> getRemoteServices();

	/**
	 * Return all {@link Registration}s that match the given description. 
	 * Wildcards can be used.
	 * 
	 * @deprecated The more general getServicesByAttributes method should be used
	 * with a description attribute
	 * @param description description of service
	 * @return all {@link Registration}s that match the given description
	 */
	@Deprecated
	public Set<Registration> getServicesByDescription(String description);

	/**
	 * Returns all locally available {@link Registration}s
	 * that match the given query. 
	 * 
	 * @param query A query can contain "(",")", "&&", "||", "==", and "!=". 
	 * </br>Example: (description==service1)||(description==service2).
	 * @return all {@link Registration}s that match the given query
	 */
	public Registration[] getServicesByAttributes(String query);

	/**
	 * Removes the given {@link VirtualAddress} from the internal VirtualAddress-store. 
	 * Does nothing if the virtual address is not stored by this IdentityManager.
	 * 
	 * @param virtualAddress
	 * @return true, if {@link VirtualAddress} has been removed successfully.
	 */
	public boolean removeService(VirtualAddress virtualAddress);
	
	/**
	 * Returns the implementation name of this IdentityManager bundle.
	 * @return Class name string.
	 */
	public String getIdentifier();
	
	/**
	 * Method to exactly control gathering of services. 
	 * @param attributes The attributes the service should have
	 * @param timeOut Time to wait for discovery responses
	 * @param returnFirst If true method returns at first found service
	 * @param isStrictRequest <br/>
	 * true - only services will be discovered which possess all attributes <br/>
	 * false - attribute types which a service does not have are ignored
	 * @return all {@link Registration}s that belong to the found services
	 */
	Registration[] getServiceByAttributes(Part[] attributes, long timeOut,
			boolean returnFirst, boolean isStrict);
}
