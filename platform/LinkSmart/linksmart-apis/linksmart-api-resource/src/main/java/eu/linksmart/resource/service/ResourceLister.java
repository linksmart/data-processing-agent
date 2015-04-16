package eu.linksmart.resource.service;

import java.util.List;

/**
 * Service for listing and pagination of particular type of managed resources.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of resources handled by this service.
 */
public interface ResourceLister<T> {

	/**
	 * Lists the unique identifiers (names) of all managed resources. The sort
	 * order of the item list is implementation specific.
	 * 
	 * @return
	 */
	List<String> getNames();

	/**
	 * Resource listing supporting pagination. The ordering is undefined per
	 * default and depends on manager implementation for a particular resource
	 * type.
	 * 
	 * @param limit
	 * @param offset
	 * @return
	 */
	List<T> list(int limit, int offset);

	/**
	 * Retrieves the number of managed resources.
	 * 
	 * @return
	 */
	int count();

}
