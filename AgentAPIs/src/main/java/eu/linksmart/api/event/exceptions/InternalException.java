package eu.linksmart.api.event.exceptions;

/**
 * Created by José Ángel Carvajal on 30.08.2016 a researcher of Fraunhofer FIT.
 */
public class InternalException extends TraceableException {

    public InternalException(String errorProducerId, String errorProducerType, String message) {
        super(errorProducerId, errorProducerType, message);
    }

    public InternalException(String errorProducerId, String errorProducerType, String message, Throwable cause) {
        super(errorProducerId, errorProducerType, message, cause);
    }

    public InternalException(String errorProducerId, String errorProducerType, Throwable cause) {
        super(errorProducerId, errorProducerType, cause);
    }

    public InternalException(String errorProducerId, String errorProducerType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorProducerId, errorProducerType, message, cause, enableSuppression, writableStackTrace);
    }
}
