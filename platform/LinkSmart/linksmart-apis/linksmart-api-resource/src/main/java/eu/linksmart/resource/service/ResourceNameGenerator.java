package eu.linksmart.resource.service;

/**
 * Shared service to generate unambiguous resource names (identifiers). These
 * are expected to be unique in scope of this service (e.g. it guarantees to not
 * duplicate a previously generated name).
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 *
 */
public interface ResourceNameGenerator {

	/**
	 * Generates a unique resource name (resource URN).
	 * 
	 * @return
	 */
	String generateName();

	/**
	 * Tests whether the given name is unique in scope of this service and has
	 * not been issued previously.
	 * 
	 * @return
	 */
	boolean isUnique(String name);

}
