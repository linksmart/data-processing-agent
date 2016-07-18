package eu.linksmart.api.event.datafusion;


public class StatementResponse{
    private  String headline;
    private  String agentID;
    private  String producerID;
    private  String producerName;
    private String message = "";
    private int status =500;
    private MessagesTypes messageType =MessagesTypes.ERROR;
    private String topic="" ;

    public StatementResponse(String headline, String agentID, String producerID, String producerName, String message, int status, String topic) {

        setterStatementResponse( headline,  agentID,  producerID,  producerName,  message,  status);
        this.topic = topic;
    }
    public StatementResponse(String headline, String agentID, String producerID, String producerName, String message, int status) {

        setterStatementResponse( headline,  agentID,  producerID,  producerName,  message,  status);
    }
    private void setterStatementResponse(String headline, String agentID, String producerID, String producerName, String message, int status) {
        this.headline = headline;
        this.agentID = agentID;
        this.producerID = producerID;
        this.producerName = producerName;
        this.message = message;
        this.status = status;
        this.messageType = MessagesTypes.get(status);

    }
    public StatementResponse() {

    }
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    public String getProducerID() {
        return producerID;
    }

    public void setProducerID(String producerID) {
        this.producerID = producerID;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MessagesTypes getMessageType() {
        return messageType;
    }

    public void setMessageType(MessagesTypes messageType) {
        this.messageType = messageType;
    }

    public String getTopic() {
        if(topic== null || topic.equals("")){
            topic="";

            if(agentID!=null){
                topic+=agentID+"/";
            }
            if(producerID!=null){
                topic+=producerID+"/";
            }
        }
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public enum MessagesTypes{
        SUCCESS, INFORMATIVE, REDIRECTION, CLIENT_ERROR, ERROR;
        public static MessagesTypes get(int value){
            if(value<200)
                return INFORMATIVE;
            if(value<300)
                return SUCCESS;
            if (value<400)
                return REDIRECTION;
            if (value<500)
                return CLIENT_ERROR;

            return ERROR;

        }
    }
}