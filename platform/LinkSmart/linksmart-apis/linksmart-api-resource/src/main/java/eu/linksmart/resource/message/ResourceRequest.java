package eu.linksmart.resource.message;

import java.util.Map;

/**
 * Message-oriented embodiment of a resource operation according to the <a
 * href="http://en.wikipedia.org/wiki/Command_pattern">Command pattern</a>.
 * Concrete subclasses capture the semantics of the requested operation,
 * involved parameter set and the response type. The class is more abstract,
 * protocol and representation-agnostic than alternatives like Restlet's <a href=
 * "http://restlet.com/technical-resources/restlet-framework/javadocs/2.2/osgi/api/org/restlet/Request.html">Request</a>
 *  object.
 * 
 * @author pullmann
 *
 */
public abstract class ResourceRequest extends ResourceMessage {

	private static final long serialVersionUID = 7492059230163412559L;

	protected static final String EVENT_TOPIC = "eu/linksmart/resource/request";

	protected String resourceName;

	protected Map<String, ?> properties;

	public ResourceRequest(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * Name of the resource in question, <code>null</code> if not appropriate.
	 * 
	 * @return
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Retrieves auxiliary properties of the request.
	 * 
	 * @return
	 */
	Map<String, ?> getProperties() {
		return properties;
	}

}
