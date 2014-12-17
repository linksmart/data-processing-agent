package eu.linksmart.resource.service;

/**
 * Service for creation of new resource instances.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 *
 */
public interface ResourceFactory<T> {

	/**
	 * Creates a new resource instance using the default, null-argument
	 * constructor of the resource class. The resource class is dynamically
	 * loaded from an exporting resource implementation bundle.
	 * 
	 * @return
	 */
	T create();

}
