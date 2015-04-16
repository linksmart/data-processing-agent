package eu.linksmart.resource.message;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.EventConstants;

/**
 * Message-oriented embodiment of a resource operation according to the <a
 * href="http://en.wikipedia.org/wiki/Command_pattern">Command pattern</a>.
 * Concrete subclasses capture the semantics of the requested operation,
 * involved parameter set and the response type. The class is more abstract,
 * protocol and representation-agnostic than alternatives like Restlet's <a
 * href=
 * "http://restlet.com/technical-resources/restlet-framework/javadocs/2.2/osgi/api/org/restlet/Request.html"
 * >Request</a> object.
 * 
 * @author pullmann
 *
 */
public abstract class ResourceRequest extends ResourceMessage {

	private static final long serialVersionUID = 7492059230163412559L;

	public static final String EVENT_TOPIC_REQUEST = "eu/linksmart/resource/request";

	public static final String PROPERTY_RESOURCE_NAME = "resource.name";

	public ResourceRequest(final String resourceName) {
		super(new HashMap() {
			{
				put(PROPERTY_RESOURCE_NAME, resourceName);
			}
		});
	}

	public ResourceRequest(Map properties) {
		super(properties);
	}

	/**
	 * Name of the resource in question, <code>null</code> if not appropriate.
	 * 
	 * @return
	 */
	public String getResourceName() {
		return (String) getProperty(PROPERTY_RESOURCE_NAME);
	}

}
