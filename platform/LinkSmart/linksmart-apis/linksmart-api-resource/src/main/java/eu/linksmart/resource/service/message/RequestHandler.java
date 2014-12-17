package eu.linksmart.resource.service.message;

import java.util.Set;

import eu.linksmart.resource.message.ResourceRequest;
import eu.linksmart.resource.message.ResourceResponse;

/**
 * (Asynchronously invoked) handler of a {@link ResourceRequest}.
 * 
 * @author pullmann
 *
 */
public interface RequestHandler<IN extends ResourceRequest, OUT extends ResourceResponse> {

	/**
	 * Type of the request handled (simple class name of the
	 * {@link ResourceRequest} subclass.
	 */
	static final String PROPERTY_REQUEST_TYPE = "request.type";

	/**
	 * Comma separated name list of resources handled by this component.
	 */
	static final String PROPERTY_RESOURCE_NAME = "resource.names";

	/**
	 * Comma separated type list of resources handled by this component.
	 */
	static final String PROPERTY_RESOURCE_TYPES = "resource.types";

	OUT handle(IN request);

	Class<IN> getRequestType();

	Set<String> getResourceNames();

	Set<String> getResourceTypes();

}
