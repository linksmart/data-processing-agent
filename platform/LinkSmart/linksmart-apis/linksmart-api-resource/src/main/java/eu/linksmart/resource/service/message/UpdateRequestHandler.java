package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.StatusResponse;
import eu.linksmart.resource.message.UpdateRequest;

/**
 * Handles partial resource updates returning a status.
 * 
 * @author pullmann
 *
 * @param <T>
 */

public interface UpdateRequestHandler<T> extends
		RequestHandler<UpdateRequest, StatusResponse> {
}
