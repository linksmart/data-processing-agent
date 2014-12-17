package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.ListRequest;
import eu.linksmart.resource.message.ListResponse;

public interface ListRequestHandler<T> extends
		RequestHandler<ListRequest, ListResponse<T>> {

}
