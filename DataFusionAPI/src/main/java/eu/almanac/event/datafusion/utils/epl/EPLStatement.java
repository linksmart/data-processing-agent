package eu.almanac.event.datafusion.utils.epl;


import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.datafusion.Statement;


import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EPLStatement implements Statement, Serializable {


    @JsonProperty("name")
    protected String name = UUID.randomUUID().toString();
    @JsonProperty("statement")
    protected String statement="";

    @JsonProperty("source")
    protected String source = "";
    @JsonProperty("input")
    protected String[] input ={""};
    @JsonProperty("output")
    protected String[] output={""};

    @JsonProperty("CEHandler")
    protected String CEHandler= "eu.almanac.event.datafusion.handler.ComplexEventMqttHandler";

    @JsonProperty("stateLifecycle")
    protected StatementLifecycle stateLifecycle=StatementLifecycle.RUN;

    @JsonProperty("scope")
    protected String[] scope={"default"};
    protected static final String uuid =UUID.randomUUID().toString();
    @JsonProperty("SynchronousResponse")
    protected Map synchRespones = new Hashtable<>();
    @JsonProperty("TargetAgents")
    protected ArrayList<String> targetAgents= new  ArrayList<String>();

    @JsonProperty("ID")
    protected String id = "";



    public EPLStatement() {
    }

    public EPLStatement(String name, String statement, String[] scope) {
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
    public ArrayList<String> getTargetAgents() {
        return targetAgents;
    }

    public StatementLifecycle getStateLifecycle() {
        return stateLifecycle;
    }

    @Override
    public Map getSynchronousResponse() {
        if(stateLifecycle == StatementLifecycle.SYNCHRONOUS)
            try {
                uuid.wait(60000);
            } catch (InterruptedException e) {

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
    public boolean equals(Statement obj) {

        return  (this == obj) ||
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

    public void setSynchronousResponse(Map response) {

        this.synchRespones = response;
        uuid.notifyAll();

    }
    public void setId(String id){
        this.id =id;
    }

    public void setTargetAgents(ArrayList<String> targetAgents) {
        this.targetAgents = targetAgents;
    }

}