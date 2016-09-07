package eu.linksmart.api.event.types.impl;

/**
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;

/**
 * Reference implementation of a response.
 * This response should be use in case an functionality of the API is simple and return only one result.
 * E.g. done, error, bad request, etc.
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.2.0
 * @see eu.linksmart.api.event.types.Statement
 * @see eu.linksmart.api.event.types.JsonSerializable
 *
 * */
public class GeneralRequestResponse implements JsonSerializable{
    /**
     * Title of the response
     * */
    private  String headline;
    /**
     * Id Agent that produced the response
     * */
    private  String agentID;
    /**
     * Id of the source that produced the response
     * */
    private  String producerID;
    /**
     * name/type of the source that produced the response
     * */
    private  String producerName;
    /**
     * message of the response
     * */
    private String message = "";
    /**
     * response code (based in HTTP rest codes)
     * */
    private int status =500;
    /**
     * message type (Default MessagesTypes.ERROR)
     *
     * @see eu.linksmart.api.event.types.impl.GeneralRequestResponse.MessagesTypes
     * */
    private MessagesTypes messageType =MessagesTypes.ERROR;
    /**
     * topic where the response should be publish or relative URL.
     * */
    private String topic="" ;

    public GeneralRequestResponse(String headline, String agentID, String producerID, String producerName, String message, int status, String topic) {

        setterStatementResponse( headline,  agentID,  producerID,  producerName,  message,  status);
        this.topic = topic;
    }
    public GeneralRequestResponse(String headline, String agentID, String producerID, String producerName, String message, int status) {

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
    public GeneralRequestResponse() {

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
        this.message = message.replace("\"","\\\"");
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

    @Override
    public GeneralRequestResponse build() throws TraceableException, UntraceableException {
        if(topic== null || topic.equals("")){
            topic="";

            if(agentID!=null){
                topic+=agentID+"/";
            }
            if(producerID!=null){
                topic+=producerID+"/";
            }
        }
        return this;
    }

    @Override
    public void destroy() throws Exception {
        //nothing
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