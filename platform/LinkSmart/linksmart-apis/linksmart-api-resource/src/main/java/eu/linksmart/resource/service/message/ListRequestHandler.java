package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.ListRequest;
import eu.linksmart.resource.message.ListResponse;

/**
 * Handles resource listing.
 * 
 * @author pullmann
 *
 * @param <T>
 *            Type of resources to be listed.
 */
public interface ListRequestHandler<T> extends
		RequestHandler<ListRequest, ListResponse<T>> {

}
