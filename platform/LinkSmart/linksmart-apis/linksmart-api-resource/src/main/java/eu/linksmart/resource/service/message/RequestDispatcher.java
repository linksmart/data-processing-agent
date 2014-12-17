package eu.linksmart.resource.service.message;

import java.util.concurrent.Future;

import eu.linksmart.resource.message.RequestStatus;
import eu.linksmart.resource.message.ResourceRequest;
import eu.linksmart.resource.message.ResourceResponse;

/**
 * Single point of entry for request processing. Asynchronously dispatches a
 * {@link ResourceRequest} to appropriate {@link RequestHandler} services along
 * a processing pipeline. The request remains in status {@link INIT} till
 * appropriate handlers could be resolved ( {@link ACTIVE}) or a stale request
 * was disposed finally ({@link FAILED}).
 * 
 */
public interface RequestDispatcher {

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
