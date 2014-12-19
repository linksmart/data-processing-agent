package eu.linksmart.resource.message;

import java.util.HashMap;

/**
 * Response to a previous {@link ReadRequest} containing the requested resource.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of the enclosed resource.
 */
public class ReadResponse<T> extends ResourceResponse {

	private static final long serialVersionUID = 8011979205068236624L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_RESPONSE + "/READ";

	public static final String PROPERTY_RESOURCE = "resource";

	public ReadResponse(final T resource) {
		super(new HashMap() {
			{
				put(PROPERTY_RESOURCE, resource);
			}
		});
	}

	public T getResource() {
		return (T) getProperty(PROPERTY_RESOURCE);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
