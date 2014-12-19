package eu.linksmart.resource.message;

/**
 * Request to delete the specified resource.
 * 
 * @author pullmann
 *
 */
public class DeleteRequest extends ResourceRequest {

	private static final long serialVersionUID = -4627616515293461865L;

	static final String EVENT_TOPIC = EVENT_TOPIC_REQUEST + "/DELETE";

	public DeleteRequest(String resourceName) {
		super(resourceName);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
