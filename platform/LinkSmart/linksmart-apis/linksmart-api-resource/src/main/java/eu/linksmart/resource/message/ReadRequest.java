package eu.linksmart.resource.message;

/**
 * Request to read and retrieve the specified resource.
 * 
 * @author pullmann
 *
 */
public class ReadRequest extends ResourceRequest {

	private static final long serialVersionUID = -7761999979708441076L;

	static final String EVENT_TOPIC = ResourceRequest.EVENT_TOPIC + "/READ";

	public ReadRequest(String resourceName) {
		super(resourceName);
	}

}
