package eu.almanac.event.datafusion.utils.epl;


import eu.linksmart.api.event.datafusion.Statement;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class EPLStatement implements Statement {

    protected String name;
    protected String statement;

    protected String source = null;
    protected String[] input =null;
    protected String[] output=null;


    protected String[] scope={"local"};
    protected String uuid =UUID.randomUUID().toString();

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