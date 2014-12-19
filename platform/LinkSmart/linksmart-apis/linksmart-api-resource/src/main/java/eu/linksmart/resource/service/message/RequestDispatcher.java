package eu.linksmart.resource.service.message;

import java.util.concurrent.Future;

import org.omg.PortableInterceptor.ACTIVE;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import eu.linksmart.resource.message.RequestStatus;
import eu.linksmart.resource.message.ResourceRequest;
import eu.linksmart.resource.message.ResourceResponse;

/**
 * Single point of entry for resource request processing. Asynchronously
 * dispatches a {@link ResourceRequest} to appropriate {@link RequestHandler}
 * services along a processing pipeline. The request remains in status
 * {@link INIT} till appropriate handlers could be resolved ( {@link ACTIVE}) or
 * a stale request was disposed finally ({@link FAILED}). Clients may either use
 * the service API or post request {@link Event}s via {@link EventAdmin}.
 * 
 */
public interface RequestDispatcher extends EventHandler {

	/**
	 * Asynchronously starts processing of the request.
	 * 
	 * @param request
	 *            ResourceRequest dispatched to the handling pipeline.
	 */
	Future<ResourceResponse> dispatch(ResourceRequest request);

	/**
	 * Retrieve processing information and result, when available.
	 * 
	 * @param requestId
	 *            Request ID used to retrieve the current processing status.
	 * @return
	 */
	RequestStatus getStatus(String requestId);

}
