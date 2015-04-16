package eu.linksmart.resource.message;

/**
 * Request to store a resource gained from parsing a supplied representation.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of resource to be stored.
 */
public class StoreRequest<T> extends ResourceRequest {

	private static final long serialVersionUID = -5621756085824800717L;

	static final String EVENT_TOPIC = ResourceRequest.EVENT_TOPIC_REQUEST
			+ "/STORE";

	private String resourceType;

	private T resource;

	public StoreRequest(T resource, String resourceName) {
		this(resource, resourceName, null);
	}

	public StoreRequest(T resource, String resourceName, String resourceType) {
		super(resourceName);
		this.resource = resource;
		this.resourceType = resourceType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public T getResource() {
		return resource;
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
