package eu.linksmart.resource.message;

/**
 * Request to list instances of specified type.
 * 
 * @author pullmann
 *
 */
public class ListRequest extends ResourceRequest {

	private static final long serialVersionUID = -3865792166645511477L;

	static final String EVENT_TOPIC = ResourceRequest.EVENT_TOPIC + "/LIST";

	private String resourceType;

	int limit;

	int offset;

	public ListRequest(String resourceType) {
		this(resourceType, 0, 10);
	}

	public ListRequest(String resourceType, int offset, int limit) {
		// No concrete resource involved
		super(null);
		this.resourceType = resourceType;
		this.offset = offset;
		this.limit = limit;
	}

	public String getResourceType() {
		return resourceType;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

}
