package eu.linksmart.resource.message;

/**
 * Request to delete the specified resource.
 * 
 * @author pullmann
 *
 */
public class DeleteRequest extends ResourceRequest {

	private static final long serialVersionUID = -4627616515293461865L;

	static final String EVENT_TOPIC = ResourceRequest.EVENT_TOPIC + "/DELETE";

	public DeleteRequest(String resourceName) {
		super(resourceName);
	}

}
