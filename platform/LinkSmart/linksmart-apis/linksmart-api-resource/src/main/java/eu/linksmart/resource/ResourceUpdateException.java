package eu.linksmart.resource;

/**
 * Exception thrown in course of processing a partial update on a resource.
 * 
 * @author pullmann
 *
 */
public class ResourceUpdateException extends Exception {

	private static final long serialVersionUID = 816278984007223487L;

	public ResourceUpdateException(String message) {
		super(message);
	}

	public ResourceUpdateException(String message, Throwable cause) {
		super(message, cause);
	}

}
