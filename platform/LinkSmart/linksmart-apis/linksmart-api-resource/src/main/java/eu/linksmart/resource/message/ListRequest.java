package eu.linksmart.resource.message;

import java.util.HashMap;

/**
 * Request to list instances of specified type.
 * 
 * @author pullmann
 *
 */
public class ListRequest extends ResourceRequest {

	private static final long serialVersionUID = -3865792166645511477L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_REQUEST + "/LIST";

	public static final String PROPERTY_RESOURCE_TYPE = "resource.type";

	public static final String PROPERTY_RESOURCE_LIST_OFFSET = "resource.list_offset";

	public static final String PROPERTY_RESOURCE_LIST_LIMIT = "resource.list_limit";

	public ListRequest(String resourceType) {
		this(resourceType, 0, 10);
	}

	public ListRequest(final String resourceType, final int offset,
			final int limit) {
		super(new HashMap() {
			{
				put(PROPERTY_RESOURCE_TYPE, resourceType);
				put(PROPERTY_RESOURCE_LIST_OFFSET, offset);
				put(PROPERTY_RESOURCE_LIST_LIMIT, limit);
			}
		});
	}

	public String getResourceType() {
		return (String) getProperty(PROPERTY_RESOURCE_TYPE);
	}

	public int getOffset() {
		return (Integer) getProperty(PROPERTY_RESOURCE_LIST_OFFSET);
	}

	public int getLimit() {
		return (Integer) getProperty(PROPERTY_RESOURCE_LIST_LIMIT);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
