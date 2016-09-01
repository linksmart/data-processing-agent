package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by José Ángel Carvajal on 30.08.2016 a researcher of Fraunhofer FIT.
 */
public class UnknownUntraceableException extends  UntraceableException {
    public UnknownUntraceableException() {
        super();
    }

    public UnknownUntraceableException(String message) {
        super(message);
    }

    public UnknownUntraceableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownUntraceableException(Throwable cause) {
        super(cause);
    }

    public UnknownUntraceableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
