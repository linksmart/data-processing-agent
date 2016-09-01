package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public abstract class TraceableException extends Exception {

    private static final long serialVersionUID = 5614280930770087934L;
    protected final String errorProducerId, errorProducerType;

    public String getErrorProducerId() {
        return errorProducerId;
    }

    public TraceableException(String errorProducerId,String errorProducerType,  String message) {
        super(message);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    public TraceableException(String errorProducerId,String errorProducerType,String message,  Throwable cause) {
        super(message, cause);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    public TraceableException( String errorProducerId, String errorProducerType,Throwable cause) {
        super(cause);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    public TraceableException( String errorProducerId, String errorProducerType,String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }

    public String getErrorProducerType() {
        return errorProducerType;
    }
}
