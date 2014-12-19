package eu.linksmart.resource.message;

import java.io.Serializable;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

/**
 * Message involved in a resource interaction. This comprises the exchange of a
 * {@link ResourceRequest} and {@link ResourceResponse} messages correlated by
 * the shared request ID ({@link #getRequestId()}). Extends {@link Event} to
 * explicitly support built-in message-oriented communication.
 * 
 * @author pullmann
 *
 */
public abstract class ResourceMessage extends Event implements Serializable {

	private static final long serialVersionUID = -4132246170474617539L;

	public static final String PROPERTY_REQUEST_ID = "resource.request_id";

	public ResourceMessage(Map properties) {
		this((String) properties.get(EventConstants.EVENT_TOPIC), properties);
	}

	public ResourceMessage(String eventTopic, Map properties) {
		super(eventTopic, properties);
	}

	/**
	 * Retrieves the request ID used to identify and correlate the
	 * request/response pair.
	 * 
	 * @return
	 */
	public String getRequestId() {
		return (String) getProperty(PROPERTY_REQUEST_ID);
	}

	/**
	 * Requires every message to specify a topic it is published to.
	 * 
	 * @return
	 */
	public abstract String getEventTopic();

}
