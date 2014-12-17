package eu.linksmart.resource.message;

import java.io.Serializable;

/**
 * Message involved in a resource interaction comprising the exchange of a
 * {@link ResourceRequest} and {@link ResourceResponse} correlated by the shared
 * request ID ({@link #getRequestId()}).
 * 
 * @author pullmann
 *
 */
public abstract class ResourceMessage implements Serializable {

	private static final long serialVersionUID = -4132246170474617539L;

	private String requestId;

	/**
	 * Retrieves the request ID used to identify and correlate the
	 * request/response pair.
	 * 
	 * @return
	 */
	public String getRequestId() {
		return requestId;
	}

}
