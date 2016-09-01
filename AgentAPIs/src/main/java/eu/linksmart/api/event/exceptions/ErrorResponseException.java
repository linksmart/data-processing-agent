package eu.linksmart.api.event.exceptions;

import eu.linksmart.api.event.types.impl.GeneralRequestResponse;

/**
 * Created by José Ángel Carvajal on 31.08.2016 a researcher of Fraunhofer FIT.
 */
public class ErrorResponseException extends TraceableException {
    final GeneralRequestResponse requestResponse;

    public ErrorResponseException(GeneralRequestResponse requestResponse) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage());
        this.requestResponse = requestResponse;
    }

    public ErrorResponseException(GeneralRequestResponse requestResponse, Throwable cause) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage(), cause);
        this.requestResponse = requestResponse;
    }

    public ErrorResponseException(GeneralRequestResponse requestResponse, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage(), cause, enableSuppression, writableStackTrace);
        this.requestResponse = requestResponse;
    }
    public GeneralRequestResponse getRequestResponse() {
        return requestResponse;
    }

}
