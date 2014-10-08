package eu.alamanac.event.datafusion.esper;

import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTValue;
import eu.linksmart.api.event.datafusion.Query;

public class EsperQuery implements Query {

    protected String name;
    protected String query;
    protected String[] input;

    protected String[] scope;

    public EsperQuery(IoTEntityEvent event){

        this.name = event.getProperties("Name").getIoTStateObservation()[0].getValue();
        this.query = event.getProperties("Query").getIoTStateObservation()[0].getValue();

        this.input = new String[event.getProperties("Input").getIoTStateObservation().length];
        int n=0;
        for (IoTValue i :event.getProperties("Input").getIoTStateObservation() ) {
            this.input[n] = i.getValue();
            n++;
        }

        this.scope = new String[event.getProperties("Scope").getIoTStateObservation().length];
        n=0;
        for (IoTValue i :event.getProperties("Scope").getIoTStateObservation() ) {
            this.scope[n] = i.getValue();
            n++;
        }
    }

    public String getName(){
        return  name;
    }
    public String getQuery(){
        return  query;
    }
    public String[] getInput(){
        return  input;
    }
    public String[] getScope(){
        return  scope;
    }

    public String getInput(int index){
        return  input[index];
    }
    public String getScope(int index){
        return  scope[index];
    }

}