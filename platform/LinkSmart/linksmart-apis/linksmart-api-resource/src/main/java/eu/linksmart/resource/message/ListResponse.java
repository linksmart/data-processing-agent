package eu.linksmart.resource.message;

import java.util.HashMap;
import java.util.List;

/**
 * Response to a previous {@link ListRequest}.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of resources listed.
 */
public class ListResponse<T> extends ResourceResponse {

	private static final long serialVersionUID = -6318039912441374072L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_RESPONSE + "/LIST";

	public static final String PROPERTY_RESOURCE_LIST = "resource.list";

	public ListResponse(final List<T> resources) {
		super(new HashMap() {
			{
				put(PROPERTY_RESOURCE_LIST, resources);
			}
		});
	}

	public List<T> getResourceList() {
		return (List<T>) getProperty(PROPERTY_RESOURCE_LIST);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
