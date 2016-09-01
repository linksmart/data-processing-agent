package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class StatementException extends TraceableException {


    public StatementException(String errorProducerId, String errorProducerType, String message) {
        super(errorProducerId, errorProducerType, message);
    }

    public StatementException(String errorProducerId, String errorProducerType, String message, Throwable cause) {
        super(errorProducerId, errorProducerType, message, cause);
    }

    public StatementException(String errorProducerId, String errorProducerType, Throwable cause) {
        super(errorProducerId, errorProducerType, cause);
    }

    public StatementException(String errorProducerId, String errorProducerType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorProducerId, errorProducerType, message, cause, enableSuppression, writableStackTrace);
    }
}
