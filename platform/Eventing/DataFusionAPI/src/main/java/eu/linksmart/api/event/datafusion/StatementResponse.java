package eu.linksmart.api.event.datafusion;

import java.util.ArrayList;
import org.springframework.http.HttpStatus;

public class StatementResponse{
    String message = "";
    HttpStatus status ;
    boolean success =false;
    String topic="" ;

    public StatementResponse(String message, HttpStatus status, String topic, boolean success) {
        this.message = message;
        this.status = status;
        this.topic = topic;
        this.success = success;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }



    public StatementResponse() {}

    public StatementResponse(String message, HttpStatus status, boolean success) {
        this.message = message;
        this.status = status;
        this.success = success;
    }

    public String getMessage() {
            return message;
        }

    public void setMessage(String message) {
            this.message = message;
        }



    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}