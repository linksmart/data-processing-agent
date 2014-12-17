package eu.linksmart.resource.service.message;

import eu.linksmart.resource.message.ReadRequest;
import eu.linksmart.resource.message.ReadResponse;

public interface ReadRequestHandler<T> extends
		RequestHandler<ReadRequest, ReadResponse<T>> {
}
