package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by José Ángel Carvajal on 30.08.2016 a researcher of Fraunhofer FIT.
 */
public class InternalException extends TraceableException {
    public InternalException(String errorProducerId, String message) {
        super(errorProducerId, message);
    }

    public InternalException(String message, String errorProducerId, Throwable cause) {
        super(message, errorProducerId, cause);
    }

    public InternalException(Throwable cause, String errorProducerId) {
        super(cause, errorProducerId);
    }

    public InternalException(String message, String errorProducerId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, errorProducerId, cause, enableSuppression, writableStackTrace);
    }
}
