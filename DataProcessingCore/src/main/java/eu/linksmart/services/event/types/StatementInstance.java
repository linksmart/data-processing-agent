package eu.linksmart.services.event.types;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.ComplexEventHandler;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import eu.linksmart.services.utils.configuration.Configurator;
import io.swagger.annotations.ApiModelProperty;

import eu.linksmart.services.utils.function.Utils;
import java.util.*;
import java.util.stream.Collectors;
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
/**
 * Basic and reference implementation of a statement.
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.2.0
 * @see eu.linksmart.api.event.types.Statement
 * @see eu.linksmart.api.event.types.JsonSerializable
 *
 * */
public class StatementInstance extends PersistentRequestInstance implements Statement {
    /**
     * Define which handler will be instantiate in the CEP engine when no Handler was specifically defined.
     * */
    @JsonIgnore
    public transient static String DEFAULT_HANDLER = ComplexEventHandler.class.getCanonicalName();
    @JsonIgnore
    private static final transient Configurator conf = Configurator.getDefaultConfig();


    @JsonProperty("name")
    @ApiModelProperty(notes = "Name of the statement. For add a statement (POST) is mandatory")
    protected String name = "";
    @JsonProperty("statement")
    @ApiModelProperty(notes = "Statement or Query in the underlying implementation of the CEP engine. For most of the cases is mandatory")
    protected String statement="";

    @ApiModelProperty(notes = "The output URIs of the events")
    @JsonProperty("output")
    protected List<String> output=null;

    @ApiModelProperty(notes = "The handler that manage the streams. Don't overwrite the value if is not understand fully what its mean")
    @JsonProperty("cehandler")
    protected String CEHandler= null;

    @ApiModelProperty(notes = "Statement's Lifecycle.")
    @JsonProperty("StateLifecycle")
    protected StatementLifecycle stateLifecycle = null;

    @ApiModelProperty(notes = "Server where the events will be pushed")
    @JsonProperty("scope")
    protected List<String> scope =null;

    @JsonIgnore
    protected transient static final Object lock =new Object();

    @ApiModelProperty(notes = "In case of a synchronous request, the response will be sent here.")
    @JsonProperty("SynchronousResponse")
    protected Object synchRespones ;

    @ApiModelProperty(notes = "Indicates the agent ID which should process the statement. Not used for REST API")
    @JsonProperty("TargetAgents")
    protected List<String> targetAgents= null;

    @ApiModelProperty(notes = "Indicates the agent ID where this Statement was created")
    @JsonProperty("AgentID")
    protected String agentID= null;


    @ApiModelProperty(notes = "Indicates that the pushed events should be sent as REST POST and not as MQTT PUB")
    @JsonProperty("resultType")
    private String resultType = null;
    @JsonIgnore
    private transient Class<EventEnvelope> nativeResultType = null;
    @ApiModelProperty(notes = "Indicates which publisher will be used (MQTT_PUB default)")
    @JsonProperty("publisher")
    private Publisher publisher = Publisher.MQTT_PUB;
    @ApiModelProperty(notes = "if the statement should or should not be register outside agent (some catalog)")
    @JsonProperty("isRegister")
    private boolean register = true;

    public Object getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(Object lastOutput) {
        this.lastOutput = lastOutput;
    }

    //@ApiModelProperty(notes = "The last compound event result of this statement")
    @JsonIgnore
    private Object lastOutput ;

    private void initValues(){
        if(scope==null)
            scope= Collections.singletonList("outgoing");
        if(CEHandler ==null)
            CEHandler =DEFAULT_HANDLER;
        if(SharedSettings.getId() !=null && agentID == null)
            agentID= SharedSettings.getId();
        if(targetAgents==null)
            targetAgents=new ArrayList<>();
        if(resultType==null)
            resultType = ObservationImpl.class.getCanonicalName();
        if(nativeResultType==null && EventEnvelope.builders.containsKey(resultType))
            nativeResultType = EventEnvelope.builders.get(resultType).BuilderOf();

        if(stateLifecycle==null)
            stateLifecycle=StatementLifecycle.RUN;

        this.resultType = conf.getString(Const.FeederPayloadClass + "_" +conf.getString(Const.STATEMENT_DEFAULT_OUTPUT_TYPE));
        if(resultType==null)
            resultType = ObservationImpl.class.getCanonicalName();
    }

    public StatementInstance() {
        super();
       initValues();
        setGenerateID();

    }

    public StatementInstance(String name, String statement, List<String> scope) {
        super();
        initValues();
        this.name = name;
        this.statement = statement;
        this.scope = scope;
        setGenerateID();
    }

    private void setGenerateID(){
        if(( ( id==null || "".equals(id)) && name!=null && statement!= null && !"".equals(name) && !"".equals(statement) )) {
            setId( Utils.hashIt(name + statement));
        }
    }

    @Override
    public String getId() {
        setGenerateID();
        return id;
    }

    public String getCEHandler() {
        return CEHandler;
    }

    @Override
    public List<String> getTargetAgents() {
        return targetAgents;
    }

    public StatementLifecycle getStateLifecycle() {
        return stateLifecycle;
    }

    @Override
    public Object getSynchronousResponse() {
        if(stateLifecycle == StatementLifecycle.SYNCHRONOUS)
            synchronized (lock) {
                try {
                    lock.wait(60000);
                } catch (InterruptedException e) {

                }
            }

        return synchRespones;
    }


    @Override
    public String getName(){
        setGenerateID();
        return  name;
    }
    @Override
    public String getStatement(){
        return  statement;
    }

    @Override
    public List<String> getScope(){
        return  scope;
    }
    @Override
    public boolean haveOutput(){
        return output != null;
    }
    @Override
    public boolean haveScope(){
        return scope != null;
    }
    @Override
    public String getScope(int index){

        return scope.get(index);
    }

    @Override
    public List<String> getOutput() {
        if(output==null)
            output = Collections.singletonList(conf.getString(Const.EVENT_OUT_TOPIC_CONF_PATH));

        return output.stream().map(s->AgentUtils.topicReplace(s, id)).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object object) {

        Statement obj;
        return  (object instanceof Statement && (obj = (Statement) object) == object) &&
                (
                        ( obj.getName() == this.name || ( this.name != null && this.name.equals(obj.getName()) ) ) &&
                ( obj.getStatement() == this.statement || ( this.statement != null ) && this.statement.equals(obj.getStatement()) ) &&
                ( obj.getOutput() == this.getOutput() || ( this.output != null ) && this.output.equals(obj.getOutput()) ) &&
                ( obj.getCEHandler() == this.CEHandler || ( this.CEHandler != null ) && this.CEHandler.equals(obj.getCEHandler()) ) &&
                ( obj.getStateLifecycle() == this.stateLifecycle || ( this.stateLifecycle != null ) && this.stateLifecycle.equals(obj.getStateLifecycle()) ) &&
                ( obj.getScope() == this.getScope() || ( this.scope != null ) && this.scope.equals(obj.getScope()) ) &&
                ( obj.getTargetAgents() == this.targetAgents || ( this.targetAgents != null ) && this.targetAgents.equals(obj.getTargetAgents()) ) &&
                        id.equals(obj.getId())
                );

    }
   @Override
   public int hashCode(){
       return (this.name +
               this.statement +
               this.output +
               this.CEHandler +
               this.stateLifecycle +
               this.scope +
               this.targetAgents.stream().map(Object::toString).collect(Collectors.joining()) +
               id).hashCode();
   }
    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public void setStatement(String statement) {
        this.statement = statement;
        setGenerateID();
    }

    public void setName(String name) {
        this.name = name;
        setGenerateID();
    }
    public void setCEHandler(String CEHandler) {
        this.CEHandler =CEHandler;
    }

    public void setStateLifecycle(StatementLifecycle stateLifecycle) {
        this.stateLifecycle=stateLifecycle;
    }

    public void setSynchronousResponse(Object response) {

        this.synchRespones = response;
        synchronized (lock) {
            lock.notifyAll();
        }

    }
    public void setId(String id){
        this.id =id;
    }

    public void setTargetAgents(List<String> targetAgents) {
        this.targetAgents = targetAgents;
    }

    @Override
    public void toRegister(boolean registrable) {
        register =registrable;
    }

    @Override
    public boolean isRegistrable() {
        return register;
    }

    @Override
    public boolean isRESTOutput() {

        switch (publisher){
            case HTTP_GET:
            case REST_GET:
                return false;
            case HTTP:
            case REST:
            case HTTP_POST:
            case REST_POST:
                return true;
            case MQTT:
            case MQTT_PUB:
            default:
                return false;

        }
    }

    @Override
    @Deprecated
    public void isRESTOutput(boolean active) {
        publisher = Publisher.HTTP_POST;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        setGenerateID();
        try {
            nativeResultType = (Class<EventEnvelope>) Class.forName(resultType);

        } catch (Exception e) {
            throw new StatementException(id, this.getClass().getCanonicalName(),e.getMessage());
        }
        return this;
    }

    @Override
    public void destroy() throws Exception {

        synchronized (lock) {
            lock.notifyAll();
        }
    }

    @Override
    public String getResultType() {
        return resultType;
    }

    @Override
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }


    public Object getSynchRespones() {
        return synchRespones;
    }

    public void setSynchRespones(Object synchRespones) {
        this.synchRespones = synchRespones;
    }

    public String getAgentID() {
        return agentID;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }


}