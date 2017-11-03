package eu.linksmart.services.event.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class BootstrappingBean {
    @JsonProperty("statements")
    protected List<Statement> statements;
    @JsonProperty("events")
    protected Map<String,EventEnvelope[]> events;



    public BootstrappingBean(){
        statements = null;
        events = null;
    }

    public List<Statement> getStatements() {

        return statements;

    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public Map<String,EventEnvelope[]> getEvents() {
        return events;
    }

    public void setEvents(Map<String, EventEnvelope[]> events) {
        this.events = events;
    }

    public Statement getStatement(int index) {

        if(statements!=null)
            return statements.get(index);
        else
            return null;

    }

    public void setStatements(int index, Statement statement) {
        if(statements==null)
            statements = new ArrayList<>();

        statements.set(index, (StatementInstance) statement);

    }
    public EventEnvelope[] getEvent(String topic) {
        if(events !=null)
            return events.get(topic);
        else
            return null;
    }

    public void setEvent(String key, EventEnvelope[] observation) {
        if(events ==null)
            events = new Hashtable<>();

        events.put(key, observation);

    }
  /*  @JsonProperty("learningRequests")
    protected List<CEMLRequest> learningRequests;
    public List<CEMLRequest> getLearningRequests() {
        return learningRequests;
    }

    public void setLearningRequests(List<CEMLRequest> learningRequests) {
        this.learningRequests = learningRequests;
    }*/

}

