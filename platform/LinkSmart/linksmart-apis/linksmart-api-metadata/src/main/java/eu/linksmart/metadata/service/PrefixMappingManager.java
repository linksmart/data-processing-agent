package eu.linksmart.metadata.service;

import eu.linksmart.metadata.PrefixMapping;
import eu.linksmart.resource.ResourceManagementException;
import eu.linksmart.resource.service.ResourceManager;

/**
 * Manages {@link PrefixMapping} resources.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface PrefixMappingManager extends ResourceManager<PrefixMapping> {

	/**
	 * Stores the supplied {@link PrefixMapping} only if neither namespace nor
	 * prefix are already defined.
	 * 
	 * @param prefix
	 * @param namespaceURI
	 * @return The generated name of the persisted {@link PrefixMapping}.
	 */
	@Override
	public String store(PrefixMapping t) throws ResourceManagementException;

	/**
	 * Simplified version of the generic {@link #store(PrefixMapping)} method.
	 * 
	 * @param prefix
	 * @param namespaceURI
	 * @return The generated name of the persisted {@link PrefixMapping}.
	 */
	public String store(String prefix, String namespaceURI);

	/**
	 * Stores the supplied {@link PrefixMapping} at given name when neither
	 * namespace nor prefix are already defined.
	 * 
	 * @param prefix
	 * @param namespaceURI
	 */
	@Override
	public void store(PrefixMapping t, String name)
			throws ResourceManagementException;

	/**
	 * Removes the specified prefix mapping only if the namespace is not in use.
	 */
	@Override
	public void delete(String name) throws ResourceManagementException;

}
