package eu.linksmart.resource.service;

import eu.linksmart.resource.ResourcePath;
import eu.linksmart.resource.ResourceUpdateException;

/**
 * This service performs partial updates on resources without a need for their
 * replacement by {@link ResourceManager#store(Object, String)}.
 * 
 * @author pullmann
 *
 */
public interface ResourceUpdater {

	/**
	 * Applies a partial update on specified resource by merging in the supplied
	 * content at given location. The interpretation of the location string and
	 * implementation of the update are out-of-the scope.
	 * 
	 * @param name
	 *            The name of the resource to be updated.
	 * @param content
	 *            The content used to partially update the resource.
	 * 
	 * @param path
	 *            The path pointing to a sub-resource region to be updated.
	 * 
	 * @throws ResourceUpdateException
	 *             If the partial update of the resource failed.
	 * 
	 */
	void update(String name, Object content, ResourcePath path)
			throws ResourceUpdateException;
}
