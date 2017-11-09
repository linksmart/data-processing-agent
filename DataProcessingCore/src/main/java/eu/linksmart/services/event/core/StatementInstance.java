package eu.linksmart.services.event.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.intern.SharedSettings;
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
    public static String DEFAULT_HANDLER = "eu.linksmart.services.event.handler.ComplexEventHandler";


    @JsonProperty("name")
    @ApiModelProperty(notes = "Name of the statement. For add a statement (POST) is mandatory")
    protected String name = "";
    @JsonProperty("statement")
    @ApiModelProperty(notes = "Statement or Query in the underlying implementation of the CEP engine. For most of the cases is mandatory")
    protected String statement="";

    @JsonProperty("source")
    @ApiModelProperty(notes = "Server source for the event")
    @Deprecated
    protected String source = "";

    @ApiModelProperty(notes = "The input URIs of the events")
    @Deprecated
    @JsonProperty("input")
    protected String[] input ={""};

    @ApiModelProperty(notes = "The output URIs of the events")
    @Deprecated
    @JsonProperty("output")
    protected String[] output=null;

    @ApiModelProperty(notes = "The handler that manage the streams. Don't overwrite the value if is not understand fully what its mean")
    @Deprecated
    @JsonProperty("CEHandler")
    protected String CEHandler= DEFAULT_HANDLER;

    @ApiModelProperty(notes = "Statement's Lifecycle.")
    @Deprecated
    @JsonProperty("StateLifecycle")
    protected StatementLifecycle stateLifecycle=StatementLifecycle.RUN;

    @ApiModelProperty(notes = "Server where the events will be pushed")
    @Deprecated
    @JsonProperty("scope")
    protected String[] scope={"outgoing"};
    protected static final String uuid =UUID.randomUUID().toString();
    @JsonIgnore
    protected transient static final Object lock =new Object();

    @ApiModelProperty(notes = "In case of a synchronous request, the response will be sent here.")
    @Deprecated
    @JsonProperty("SynchronousResponse")
    protected Object synchRespones ;

    @ApiModelProperty(notes = "Indicates the agent ID which should process the statement. Not used for REST API")
    @Deprecated
    @JsonProperty("TargetAgents")
    protected List<String> targetAgents= new  ArrayList<String>();



    @ApiModelProperty(notes = "Indicates that the pushed events should be sent as REST POST and not as MQTT PUB")
    @JsonProperty("isRestOutput")
    private boolean restOutput;
    @JsonIgnore
    private transient boolean toRegister = true;

    public EventEnvelope getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(EventEnvelope lastOutput) {
        this.lastOutput = lastOutput;
    }

    //@ApiModelProperty(notes = "The last compound event result of this statement")
    @JsonIgnore
    private EventEnvelope lastOutput ;

    public StatementInstance() {
        super();
        setGenerateID();
        setGenerateOutput();
    }

    public StatementInstance(String name, String statement, String[] scope) {
        super();
        this.name = name;
        this.statement = statement;
        this.scope = scope;
        setGenerateID();
        setGenerateOutput();
    }

    private void setGenerateID(){
        if(( ( id==null || "".equals(id)) && name!=null && statement!= null && !"".equals(name) && !"".equals(statement) )) {
            setId( Utils.hashIt(name + statement));
        }
    }
    private void setGenerateOutput() {
        if ((output == null || output.length < 1) && id != null && !"".equals(id))
            setOutput(new String[]{DefaultMQTTPublisher.defaultOutput(SharedSettings.getId()) + id + "/"});

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
    public String[] getInput(){
        return  input;
    }
    @Override
    public String[] getScope(){
        return  scope;
    }
    @Override
    public boolean haveInput(){
        return input != null;
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
    public String getInput(int index){
        return  input[index];
    }
    @Override
    public String getScope(int index){
        if (scope==null)
            initScope();

        return  scope[index];
    }

    @Override
    public String[] getOutput() {
        setGenerateOutput();
        return output;
    }

    @Override
    public String getSource() {
        return source;
    }

    private void initScope(){
        scope =new String[1];
        scope[0] ="local";
    }
    @Override
    public boolean equals(Object object) {

        Statement obj;
        return  (object instanceof Statement && (obj = (Statement) object) == object) &&
                (
                        this.name.equals(obj.getName()) &&
                        this.statement.equals(obj.getStatement()) &&
                        this.source.equals(obj.getSource()) &&
                        Arrays.deepEquals(this.input, obj.getInput()) &&
                        Arrays.deepEquals(this.output, obj.getOutput()) &&
                        this.CEHandler.equals(obj.getCEHandler()) &&
                        this.stateLifecycle.equals(obj.getStateLifecycle()) &&
                        Arrays.deepEquals(this.scope, obj.getScope()) &&
                        this.targetAgents.equals(obj.getTargetAgents()) &&
                        id.equals(obj.getId())
                );

    }
   @Override
   public int hashCode(){
       return (this.name +
               this.statement +
               this.source +
               Arrays.toString(this.input) +
               Arrays.toString(this.output) +
               this.CEHandler +
               this.stateLifecycle +
               Arrays.toString(this.scope) +
               this.targetAgents.stream().map(Object::toString).collect(Collectors.joining()) +
               id).hashCode();
   }
    public void setScope(String[] scope) {
        this.scope = scope;
    }

    public void setOutput(String[] output) {
        this.output = output;
    }

    public void setInput(String[] input) {
        this.input = input;
    }

    public void setSource(String source) {
        this.source = source;
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
        setGenerateOutput();
    }

    public void setTargetAgents(List<String> targetAgents) {
        this.targetAgents = targetAgents;
    }

    @Override
    public void toRegister(boolean registrable) {
        toRegister=registrable;
    }

    @Override
    public boolean isRegistrable() {
        return toRegister;
    }

    @Override
    public boolean isRESTOutput() {
        return restOutput;
    }

    @Override
    public void isRESTOutput(boolean active) {
        restOutput = active;
    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        setGenerateID();
        setGenerateOutput();
        return this;
    }

    @Override
    public void destroy() throws Exception {

        synchronized (lock) {
            lock.notifyAll();
        }
    }


}