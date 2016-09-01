package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by José Ángel Carvajal on 30.08.2016 a researcher of Fraunhofer FIT.
 */
public class UnknownException extends TraceableException{
    public UnknownException(String errorProducerId, String errorProducerType, String message) {
        super(errorProducerId, errorProducerType, message);
    }

    public UnknownException(String errorProducerId, String errorProducerType, String message, Throwable cause) {
        super(errorProducerId, errorProducerType, message, cause);
    }

    public UnknownException(String errorProducerId, String errorProducerType, Throwable cause) {
        super(errorProducerId, errorProducerType, cause);
    }

    public UnknownException(String errorProducerId, String errorProducerType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorProducerId, errorProducerType, message, cause, enableSuppression, writableStackTrace);
    }
}
