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

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.ArrayList;

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
    private List<String> topics = new ArrayList<String>(); // =""

    public GeneralRequestResponse(String headline, String agentID, String producerID, String producerName, String message, int status, List<String> topics) {

        this();
        setterStatementResponse( headline,  agentID,  producerID,  producerName,  message,  status);
        this.topics.clear();
        this.topics.addAll(topics);
    }
    public GeneralRequestResponse(String headline, String agentID, String producerID, String producerName, String message, int status) {

        this();
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
        topics.add("");

    }
    @ApiModelProperty(notes = "Provide the content title of the response", required = true)
    @JsonProperty
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    @ApiModelProperty(notes = "Provide the id of the agent where the response was generated", required = true)
    @JsonProperty
    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    @ApiModelProperty(notes = "Provide the id of the request that provide a response", required = true)
    @JsonProperty
    public String getProducerID() {
        return producerID;
    }

    public void setProducerID(String producerID) {
        this.producerID = producerID;
    }

    @ApiModelProperty(notes = "Provide the name of the request that provide a response", required = true)
    @JsonProperty
    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    @ApiModelProperty(notes = "Message of the response", required = true)
    @JsonProperty
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message.replace("\"","\\\"");
    }

    @ApiModelProperty(notes = "Code of the response", required = true)
    @JsonProperty
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @ApiModelProperty(notes = "Verbose code of the response", required = true)
    @JsonProperty
    public MessagesTypes getMessageType() {
        return messageType;
    }

    public void setMessageType(MessagesTypes messageType) {
        this.messageType = messageType;
    }

    @ApiModelProperty(notes = "Topics of the response. Used as URI", required = true)
    @JsonProperty
    public List<String> getTopics() {
        String topic = "";
        if(topics.get(0).equals(topic)){
            topics.clear();
            if(agentID!=null){
                topic+=agentID+"/";
            }
            if(producerID!=null){
                topic+=producerID+"/";
            }
            topics.add(topic);
        }
        return topics;
    }

//    public void setTopic(String topic) {
//        this.topic = topic;
//    }

    @Override
    public GeneralRequestResponse build() throws TraceableException, UntraceableException {
        String topic = "";
        if(this.topics.get(0).equals(topic)){
            topics.clear();
            if(agentID!=null){
                topic+=agentID+"/";
            }
            if(producerID!=null){
                topic+=producerID+"/";
            }
            this.topics.add(topic);
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