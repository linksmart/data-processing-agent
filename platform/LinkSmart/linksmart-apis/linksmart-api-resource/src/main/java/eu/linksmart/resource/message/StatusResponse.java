package eu.linksmart.resource.message;

import java.util.HashMap;

/**
 * Response indicating the result status of a {@link ResourceRequest}.
 * 
 * @author pullmann
 *
 */
public class StatusResponse extends ResourceResponse {

	private static final long serialVersionUID = -1397804379329007547L;

	public static final String EVENT_TOPIC = EVENT_TOPIC_RESPONSE + "/STATUS";

	public static final String PROPERTY_RESPONSE_STATUS = "resource.response_status";

	public static final String PROPERTY_RESPONSE_DESCRIPTION = "resource.response_description";

	public StatusResponse(int status) {
		this(status, null);
	}

	public StatusResponse(final int status, final String description) {
		super(new HashMap() {
			{
				put(PROPERTY_RESPONSE_STATUS, status);
				put(PROPERTY_RESPONSE_DESCRIPTION, description);
			}
		});
	}

	public int getStatus() {
		return (Integer) getProperty((PROPERTY_RESPONSE_STATUS));
	}

	public String getDescription() {
		return (String) getProperty((PROPERTY_RESPONSE_DESCRIPTION));
	}

	@Override
	public String getEventTopic() {
		return EVENT_TOPIC;
	}

}
