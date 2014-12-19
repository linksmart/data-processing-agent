package eu.linksmart.resource.message;

/**
 * Request to read and retrieve the specified resource.
 * 
 * @author pullmann
 *
 */
public class ReadRequest extends ResourceRequest {

	private static final long serialVersionUID = -7761999979708441076L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_REQUEST + "/READ";

	public ReadRequest(final String resourceName) {
		super(resourceName);
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
