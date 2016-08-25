package eu.almanac.event.datafusion.utils.epl.intern;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jose Angel Carvajal on 31.08.2015 a researcher of Fraunhofer FIT.
 */
public class StatementInstance extends eu.almanac.event.datafusion.utils.epl.StatementInstance {


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
        lock.notifyAll();

    }
    public void setId(String id){
        this.id =id;
    }

    public void setTargetAgents(ArrayList<String> targetAgents) {
        this.targetAgents = targetAgents;
    }
}
