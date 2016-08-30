package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class StatementException extends TraceableException {


    public StatementException(String errorProducerId, String message) {
        super(errorProducerId, message);
    }

    public StatementException(String message, String errorProducerId, Throwable cause) {
        super(message, errorProducerId, cause);
    }

    public StatementException(Throwable cause, String errorProducerId) {
        super(cause, errorProducerId);
    }

    public StatementException(String message, String errorProducerId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, errorProducerId, cause, enableSuppression, writableStackTrace);
    }
}
