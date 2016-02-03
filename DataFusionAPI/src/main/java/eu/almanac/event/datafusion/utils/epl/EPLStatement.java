package eu.almanac.event.datafusion.utils.epl;


import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.datafusion.Statement;


import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

public class EPLStatement implements Statement, Serializable {


    @JsonProperty("name")
    protected String name = UUID.randomUUID().toString();
    @JsonProperty("statement")
    protected String statement;

    @JsonProperty("source")
    protected String source = null;
    @JsonProperty("input")
    protected String[] input =null;
    @JsonProperty("output")
    protected String[] output=null;

    @JsonProperty("CEHandler")
    protected String CEHandler= "eu.almanac.event.datafusion.handler.ComplexEventMqttHandler";

    @JsonProperty("stateLifecycle")
    protected StatementLifecycle stateLifecycle=StatementLifecycle.RUN;

    @JsonProperty("scope")
    protected String[] scope={"default"};
    protected static final String uuid =UUID.randomUUID().toString();
    protected Map synchRespones = null;

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

    public String getHash() {
        return hashIt(name + statement);
    }

    public String getCEHandler() {
        return CEHandler;
    }

    public StatementLifecycle getStateLifecycle() {
        return stateLifecycle;
    }

    @Override
    public Map getSynchronousResponse() {
        if(stateLifecycle ==stateLifecycle.SYNCHRONOUS)
            try {
                uuid.wait(60000);
            } catch (InterruptedException e) {

            }

        return synchRespones;
    }


    protected String hash;




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
   /* public void setName(String name) {
        this.name = name;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setInput(String[] input) {
        this.input = input;
    }

    public void setOutput(String[] output) {
        this.output = output;
    }
    public void setScope(String[] scope) {
        this.scope = scope;
    }*/
}