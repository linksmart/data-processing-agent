package eu.linksmart.resource.message;



/**
 * Request to store a resource gained from parsing a supplied representation.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public class StoreRequest<T> extends ResourceRequest {

	static final String EVENT_TOPIC = ResourceRequest.EVENT_TOPIC + "/STORE";

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

}
