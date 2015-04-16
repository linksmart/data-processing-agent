package eu.linksmart.resource;

/**
 * Exception thrown in course of processing a life-cycle management operation on
 * a resource.
 * 
 * @author pullmann
 *
 */
public class ResourceManagementException extends Exception {

	private static final long serialVersionUID = -333367522901387610L;

	public ResourceManagementException(String message) {
		super(message);
	}

	public ResourceManagementException(String message, Throwable cause) {
		super(message, cause);
	}

}
