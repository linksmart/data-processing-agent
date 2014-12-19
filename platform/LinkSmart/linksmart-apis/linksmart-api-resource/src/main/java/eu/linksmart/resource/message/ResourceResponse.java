package eu.linksmart.resource.message;

import java.util.Map;

import org.osgi.service.event.EventConstants;

/**
 * Response to a preceding {@link ResourceRequest} correlated by the shared
 * request ID ({@link #getRequestId()}).
 * 
 * @author pullmann
 *
 */
public abstract class ResourceResponse extends ResourceMessage {

	private static final long serialVersionUID = -5207721924897130013L;

	public static final String EVENT_TOPIC_RESPONSE = "eu/linksmart/resource/response";

	public ResourceResponse(Map properties) {
		super((String) properties.get(EventConstants.EVENT_TOPIC), properties);
	}

}
