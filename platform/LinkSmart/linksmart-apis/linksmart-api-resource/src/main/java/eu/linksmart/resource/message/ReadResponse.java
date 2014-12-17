package eu.linksmart.resource.message;



public class ReadResponse<T> extends ResourceResponse {

	private static final long serialVersionUID = 8011979205068236624L;

	private T resource;

	public ReadResponse(T resource) {
		this.resource = resource;
	}

	public T getResource() {
		return resource;
	}
}
