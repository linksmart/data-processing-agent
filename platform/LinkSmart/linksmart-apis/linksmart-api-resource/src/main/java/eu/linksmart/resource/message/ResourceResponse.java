package eu.linksmart.resource.message;

/**
 * Response to a preceding {@link ResourceRequest} correlated by the shared
 * request ID ({@link #getRequestId()}).
 * 
 * @author pullmann
 *
 */
public class ResourceResponse extends ResourceMessage {

	private static final long serialVersionUID = -5207721924897130013L;

	static final String EVENT_TOPIC = "eu/linksmart/resource/response";

}
