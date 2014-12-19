package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.ReadRequest;
import eu.linksmart.resource.message.ReadResponse;

/**
 * Handles resource retrieval.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public interface ReadRequestHandler<T> extends
		RequestHandler<ReadRequest, ReadResponse<T>> {
}
