package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.DeleteRequest;
import eu.linksmart.resource.message.StatusResponse;

/**
 * Handles resource deletion.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of resource to be deleted.
 */
public interface DeleteRequestHandler<T> extends
		RequestHandler<DeleteRequest, StatusResponse> {
}
