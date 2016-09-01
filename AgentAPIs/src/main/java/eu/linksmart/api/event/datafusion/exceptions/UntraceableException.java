package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class UntraceableException extends Exception {
    public UntraceableException() {
        super();
    }

    public UntraceableException(String message) {
        super(message);
    }

    public UntraceableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UntraceableException(Throwable cause) {
        super(cause);
    }

    public UntraceableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
