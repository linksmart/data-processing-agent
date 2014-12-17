package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.DeleteRequest;
import eu.linksmart.resource.message.StatusResponse;

public interface DeleteRequestHandler<T> extends
		RequestHandler<DeleteRequest, StatusResponse> {
}
	