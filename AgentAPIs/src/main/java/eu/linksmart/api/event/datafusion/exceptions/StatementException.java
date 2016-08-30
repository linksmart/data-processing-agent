package eu.linksmart.api.event.datafusion.exceptions;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class StatementException extends Exception {

    private static final long serialVersionUID = 5614280930770087934L;

    public String getErrorTopic() {
        return errorTopic;
    }

    protected final String errorTopic;
    @Deprecated
    public StatementException() {
        super();
        errorTopic= "unknown/error";
    }
    @Deprecated
    public StatementException(String message) {
        super(message);
        errorTopic= "unknown/error";
    }
    @Deprecated
    public StatementException(String message, Throwable cause) {
        super(message, cause);
        errorTopic= "unknown/error";
    }
    @Deprecated
    public StatementException(Throwable cause) {
        super(cause);
        errorTopic= "unknown/error";
    }
    @Deprecated
    public StatementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        errorTopic= "unknown/error";
    }
    public StatementException(String errorTopic, String message) {
        super(message);
        this.errorTopic =errorTopic;
    }
    public StatementException(String message, String errorTopic, Throwable cause) {
        super(message, cause);
        this.errorTopic =errorTopic;
    }
    public StatementException(Throwable cause, String errorTopic) {
        super(cause);
        this.errorTopic =errorTopic;
    }
    public StatementException(String message, String errorTopic, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorTopic =errorTopic;
    }
}
