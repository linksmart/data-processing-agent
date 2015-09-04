package eu.almanac.event.datafusion.feeder.type;

import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.datafusion.Statement;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class PersistentBean {
    protected ArrayList<EPLStatement> statements;
    protected Map<String,Observation> observations;
    PersistentBean(){
        statements = null;
        observations = null;
    }

    public ArrayList<EPLStatement> getStatements() {

        return statements;

    }

    public void setStatements(ArrayList<EPLStatement> statements) {
        this.statements = statements;
    }

    public Map<String,Observation> getObservations() {
        return observations;
    }

    public void setObservations(Map<String,Observation> observations) {
        this.observations = observations;
    }
    @Nullable
    public Statement getStatement(int index) {

        if(statements!=null)
            return statements.get(index);
        else
            return null;

    }

    public void setStatements(int index, Statement statement) {
        if(statements==null)
            statements = new ArrayList<>();

        statements.set(index, (EPLStatement) statement);

    }
    @Nullable
    public Observation getObservations(String topic) {
        if(observations!=null)
            return observations.get(topic);
        else
            return null;
    }

    public void setObservations(String key,Observation observation) {
        if(observations==null)
            observations = new Hashtable<>();

        observations.put(key, observation);

    }
}
