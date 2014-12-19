package eu.linksmart.resource.service;

import eu.linksmart.resource.ResourceValidationException;

/**
 * Performs a custom validation of supplied resource.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of validated resources.
 */
public interface ResourceValidator<T> {

	/**
	 * Validates the supplied resource.
	 * 
	 * @param resource
	 *            The resource to be validated.
	 * @throws ResourceValidationException
	 *             If the resource validation failed.
	 */
	void validate(T resource) throws ResourceValidationException;

}
