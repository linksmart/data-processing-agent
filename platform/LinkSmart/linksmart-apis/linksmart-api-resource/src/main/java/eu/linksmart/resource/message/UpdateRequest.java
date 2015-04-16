package eu.linksmart.resource.message;

import java.util.HashMap;

import eu.linksmart.resource.ResourcePath;

/**
 * Request to apply given update on the indicated resource and path.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public class UpdateRequest extends ResourceRequest {

	private static final long serialVersionUID = 7835201652191307332L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_REQUEST + "/UPDATE";

	public static final String PROPERTY_RESOURCE_UPDATE = "resource.update";

	public static final String PROPERTY_RESOURCE_UPDATE_PATH = "resource.update_path";

	public UpdateRequest(final Object update, final String resourceName,
			final ResourcePath path) {
		super(new HashMap() {
			{
				put(PROPERTY_RESOURCE_UPDATE, update);
				put(PROPERTY_RESOURCE_NAME, resourceName);
				put(PROPERTY_RESOURCE_UPDATE_PATH, path);
			}
		});
	}

	public Object getUpdate() {
		return getProperty(PROPERTY_RESOURCE_UPDATE);
	}

	public Object getUpdatePath() {
		return getProperty(PROPERTY_RESOURCE_UPDATE_PATH);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
