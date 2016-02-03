package eu.linksmart.api.event.datafusion;

/**
 * Created by Jose Angel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class StatementException extends Exception {

    private static final long serialVersionUID = 5614280930770087934L;

    public String getErrorTopic() {
        return errorTopic;
    }

    protected String errorTopic;
    @Deprecated
    public StatementException() {
        super();
    }
    @Deprecated
    public StatementException(String message) {
        super(message);
    }
    @Deprecated
    public StatementException(String message, Throwable cause) {
        super(message, cause);
    }
    @Deprecated
    public StatementException(Throwable cause) {
        super(cause);
    }
    @Deprecated
    public StatementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
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
