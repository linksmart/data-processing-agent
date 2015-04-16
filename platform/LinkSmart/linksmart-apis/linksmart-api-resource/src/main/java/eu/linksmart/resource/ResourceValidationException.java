package eu.linksmart.resource;

/**
 * Exception thrown when the validation of a resource failed.
 * 
 * @author pullmann
 *
 */
public class ResourceValidationException extends Exception {

	private static final long serialVersionUID = 816278984007223487L;

	public ResourceValidationException(String message) {
		super(message);
	}

	public ResourceValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
