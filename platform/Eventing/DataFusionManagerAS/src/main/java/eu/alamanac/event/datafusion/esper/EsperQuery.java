package eu.alamanac.event.datafusion.esper;

import eu.alamanac.event.datafusion.core.DataFusionManager;
import eu.alamanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTValue;
import eu.linksmart.api.event.datafusion.Statement;

public class EsperQuery implements Statement {

    protected String name;
    protected String satement;
    protected String[] input =null;
    protected String[] output=null;
    protected String[] scope=null;



    public EsperQuery(IoTEntityEvent event) throws Exception {

        int n;
        if(event.getProperties("Name")!= null)
            this.name = event.getProperties("Name").getIoTStateObservation()[0].getValue();
        else{
            LoggerHandler.publish("syntax_error", "IoTEntity Event Error: The query must have a name!", null);
            throw new Exception("IoTEntity Event Error: The query must have a name!");
        }

        if(event.getProperties("Statement")!= null)
            this.satement = event.getProperties("Statement").getIoTStateObservation()[0].getValue();
        else{
            LoggerHandler.publish("syntax_error","IoTEntity Event Error: The query must have a name!",null);
            throw new Exception("IoTEntity Event Error: The query must have a statement!");
        }

        if(event.getProperties("Output")!= null) {
            this.output = new String[event.getProperties("Output").getIoTStateObservation().length];
            n = 0;
            for (IoTValue i : event.getProperties("Output").getIoTStateObservation()) {
                this.output[n] = i.getValue();
                n++;
            }
        }

        if(event.getProperties("Input")!= null) {
            this.input = new String[event.getProperties("Input").getIoTStateObservation().length];
            n = 0;
            for (IoTValue i : event.getProperties("Input").getIoTStateObservation()) {
                this.input[n] = i.getValue();
                n++;
            }
        }

        if(event.getProperties("Scope")!= null) {
            this.scope = new String[event.getProperties("Scope").getIoTStateObservation().length];
            n = 0;
            for (IoTValue i : event.getProperties("Scope").getIoTStateObservation()) {
                this.scope[n] = i.getValue();
                n++;
            }
        }
    }

    public String getName(){
        return  name;
    }
    public String getStatement(){
        return  satement;
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

}