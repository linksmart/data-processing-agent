package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class TraceableException extends Exception {

    private static final long serialVersionUID = 5614280930770087934L;
    protected final String errorProducerId;

    public String getErrorProducerId() {
        return errorProducerId;
    }

    public TraceableException(String errorProducerId, String message) {
        super(message);
        this.errorProducerId =errorProducerId;
    }
    public TraceableException(String message, String errorProducerId, Throwable cause) {
        super(message, cause);
        this.errorProducerId =errorProducerId;
    }
    public TraceableException(Throwable cause, String errorProducerId) {
        super(cause);
        this.errorProducerId =errorProducerId;
    }
    public TraceableException(String message, String errorProducerId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorProducerId =errorProducerId;
    }
    
}
