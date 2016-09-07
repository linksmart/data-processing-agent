package eu.linksmart.api.event.types.impl;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class StatementInstance implements Statement {


    @JsonProperty("name")
    protected String name = UUID.randomUUID().toString();
    @JsonProperty("statement")
    protected String statement="";

    @JsonProperty("source")
    protected String source = "";
    @JsonProperty("input")
    protected String[] input ={""};
    @JsonProperty("output")
    protected String[] output=null;

    @JsonProperty("CEHandler")
    protected String CEHandler= "eu.linksmart.services.event.datafusion.handler.ComplexEventHandler";

    @JsonProperty("stateLifecycle")
    protected StatementLifecycle stateLifecycle=StatementLifecycle.RUN;

    @JsonProperty("scope")
    protected String[] scope={"default"};
    protected static final String uuid =UUID.randomUUID().toString();
    @JsonIgnore
    protected static final Object lock =new Object();
    @JsonProperty("SynchronousResponse")
    protected Object synchRespones ;
    @JsonProperty("TargetAgents")
    protected List<String> targetAgents= new  ArrayList<String>();

    @JsonProperty("ID")
    protected String id = "";

    private transient String hash;



    public StatementInstance() {
    }

    public StatementInstance(String name, String statement, String[] scope) {
        this.name = name;
        this.statement = statement;
        this.scope = scope;
    }
    public static String hashIt( String string){
        if(string == null)
            return "";
        MessageDigest SHA256 = null;
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return (new BigInteger(1,SHA256.digest((string).getBytes()))).toString();
    }

    public String getID() {
        if(id==null||id.equals(""))
            id = hashIt(name + statement);
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
                        id.equals(obj.getID())
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
    }

    public void setName(String name) {
        this.name = name;
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
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {

        synchronized (lock) {
            lock.notifyAll();
        }
    }


}