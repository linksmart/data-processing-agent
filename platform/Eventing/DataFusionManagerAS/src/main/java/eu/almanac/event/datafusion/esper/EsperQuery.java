package eu.almanac.event.datafusion.esper;

import eu.almanac.event.datafusion.intern.ConfigurationManagement;
import eu.almanac.event.datafusion.intern.LoggerService;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.Statement;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EsperQuery implements Statement {

    protected String name;
    protected String statement;

    protected String source;
    protected String[] input =null;
    protected String[] output=null;
    protected String[] scope={"local"};



    public EsperQuery(Event event) throws Exception {

        int n;
        if(event.getEbyName("Name")!= null)
            this.name = event.getEbyName("Name").getStringValue();
        else{
            LoggerService.publish("query/" + "anonymous", "IoTEntity Event Error: The query must have a name!", null, true);
            throw new Exception("IoTEntity Event Error: The query must have a name!");
        }

        if(event.getEbyName("Statement")!= null) {
           // this.satement = event.getProperties("Statement").getIoTStateObservation()[0].getValue().replace("{", "").replace("}", "");

            this.statement = getInputAndCleanStatement(event.getEbyName("Statement").getStringValue());
        }else{
            LoggerService.publish("query/" + name, "IoTEntity Event Error: The query must have a name!", null, true);
            throw new Exception("IoTEntity Event Error: The query must have a statement!");
        }
        if(event.getEbyName("Source")!= null) {
            // this.satement = event.getProperties("Statement").getIoTStateObservation()[0].getValue().replace("{", "").replace("}", "");

            this.source = getInputAndCleanStatement(event.getEbyName("Source").getStringValue());
        }else {

            this.source = name;
        }
        if(event.getEbyName("Output")!= null) {
            String [] aux = event.getEbyName("Output").getStringValue().split(";");
            this.output = aux;
           /* n = 0;
            for (IoTValue i : event.getEbyName("Output").getIoTStateObservation()) {
                this.output[n] = i.getValue();
                n++;
            }*/
        }

        if(event.getEbyName("Input")!= null) {

            String [] aux = event.getEbyName("Input").getStringValue().split(";");
            this.input = aux;
        }

        if(event.getEbyName("Scope")!= null) {
            String [] aux = event.getEbyName("Scope").getStringValue().split(";");
            this.scope = aux;
        }
    }

 /*   private String getInputAndCleanStatement(String statement) {

        String ret = statement ,lower=statement.toLowerCase();

        if(lower.contains("topics")) {
            String fromStatement = statement.substring(lower.indexOf("topics"));

            if (fromStatement.contains("{") && fromStatement.contains("}")) {
                ret = statement.substring(0, lower.indexOf("topics"));
                String aux[] = statement.substring( lower.indexOf("topics")).split("\\}");
                String topics[] =fromStatement.substring(fromStatement.indexOf("{"),fromStatement.indexOf("}")).split(",");

                ArrayList<String> inputs = new ArrayList<>();
                int n = 0;
                for (String i : topics) {
                    try {

                        inputs.add( i.substring(i.indexOf("[")+1, i.indexOf("]")));

                        ret += inputs.get(inputs.size()-1)+i.substring(i.indexOf("]")+1);
                    }catch (Exception e){
                        ret += i;
                    }



                    if(n<topics.length-1)
                        ret += ", ";

                    n++;
                }

                if (aux.length>1)
                    ret += aux[1];


            }else{
                LoggerHandler.publish("query/"+name, "missing '{' or '}' after the from in query:" + getName(), null,true);
            }
        }


        return ret;
    }*/
    private String getInputAndCleanStatement(String statement) {

        String ret = statement ,lower=statement.toLowerCase();

        if(lower.contains("topics")) {
            String fromStatement = statement.substring(lower.indexOf("topics"));


            if (fromStatement.contains("{") && fromStatement.contains("}")) {
                ret = statement.substring(0, lower.indexOf("topics"));
                //String aux[] = statement.substring( lower.indexOf("topics")).split("\\}");
                String topics[] =fromStatement.split("\\{([^}]+)\\}");

                Pattern pattern = Pattern.compile("\\{[^}]+\\}",Pattern.DOTALL);

                // Now create matcher object.
                Matcher matcher = pattern.matcher(statement);
                int k=matcher.groupCount();
                ArrayList<String> inputs = new ArrayList<>();
                int n = 0, m=0;
                for (int i=0; matcher.find();i++) {
                    try {
                        String aux =matcher.group(0);
                        int start =1;
                        if(aux.charAt(1) == '/')
                           start =2;
                        aux = aux.substring(start,aux.length()-1).replace("#","hash").replace("/",".");
                       // if(m==1)
                            inputs.add( aux );
                       // else if(n!=0)
                            ret +=  aux+topics[n+1];
                    }catch (Exception e){
                        ret += i;
                    }


                    n++;
                    m=(m+1)%3;
                }

                //if (topics.length>1)
                //    ret += topics[topics.length-1];


            }else{
                LoggerService.publish("query/" + name, "missing '{' or '}' after the from in query:" + getName(), null, true);
            }
        }


        return ret;
    }
    public String getName(){
        return  name;
    }
    public String getStatement(){
        return  statement;
    }
    public String[] getInput(){
        return  input;
    }
    public String[] getScope(){
        return  scope;
    }
    public boolean haveInput(){
        return input != null;
    }
    public boolean haveOutput(){
        return output != null;
    }
    public boolean haveScope(){
        return scope != null;
    }

    @Override
    public String getHash() {
        return null;
    }

    public String getInput(int index){
        return  input[index];
    }
    public String getScope(int index){
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

}