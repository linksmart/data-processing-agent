package eu.linksmart.services.event.datafusion.feeder.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.types.impl.StatementInstance;
import eu.linksmart.api.event.types.Statement;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class PersistentBean {
    @JsonProperty("statements")
    protected List<StatementInstance> statements;
    @JsonProperty("observations")
    protected Map<String,Observation[]> observations;
    PersistentBean(){
        statements = null;
        observations = null;
    }

    public List<StatementInstance> getStatements() {

        return statements;

    }

    public void setStatements(List<StatementInstance> statements) {
        this.statements = statements;
    }

    public Map<String,Observation[]> getObservations() {
        return observations;
    }

    public void setObservations(Map<String,Observation[]> observations) {
        this.observations = observations;
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
    public Observation[] getObservations(String topic) {
        if(observations!=null)
            return observations.get(topic);
        else
            return new Observation[0];
    }

    public void setObservations(String key,Observation[] observation) {
        if(observations==null)
            observations = new Hashtable<>();

        observations.put(key, observation);

    }
}
