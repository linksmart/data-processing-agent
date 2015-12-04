package de.fraunhofer.fit.event.ceml.type.requests.builded;

import de.fraunhofer.fit.event.ceml.CEMLFeeder;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.TumbleEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TumbleWindowEvaluator;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class LearningRequest {
    protected String name;
    private Configurator conf = Configurator.getDefaultConfig();
    protected DataStructure data;
    private  boolean deployed =false;

    protected Map<String,Statement> leaningStatements;

    public Map<String, Statement> getDeployStatements() {
        return deployStatements;
    }

    protected Map<String,Statement> deployStatements;

    protected Model model;

    private TumbleWindowEvaluator evaluation;
    private ArrayList<String> learningProcess;
    private ArrayList<String> deploy;


    public Map<String,Statement> getLeaningStatements() {
        return leaningStatements;
    }



    public DataStructure getData() {
        return data;
    }

    public void setData(DataStructure data) {
        this.data = data;
    }


    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }



    public void build() throws Exception {

        model.setName(name);
        data.setName(name);
        data.buildInstances();
        model.build(this);

        Integer i =0;
        leaningStatements = new Hashtable<>();
        if(learningProcess==null)
            throw new Exception("The Learning Process was not defined");
        for (String strStatement : learningProcess) {
            Statement statement = new LearningStatement("LearningStatement:"+name+i.toString(),this, strStatement);
            leaningStatements.put(statement.getHash(),statement);
            i++;
        }

        deployStatements = new Hashtable<>();
        if(deploy != null) {

            i=0;
            for (String strStatement : deploy) {
                Statement statement = new EPLStatement();
                ((EPLStatement) statement).setName("DeployStatement:" + name + i.toString());
                ((EPLStatement) statement).setStatement(strStatement);
                ((EPLStatement) statement).setStateLifecycle(Statement.StatementLifecycle.PAUSE);

                deployStatements.put(statement.getHash(),statement);
                i = 0;
            }
        }
        if(evaluation == null)
            evaluation = new TumbleWindowEvaluator();
        evaluation.build(data.getAttributes().keySet());


    }
    public void deploy(){
      if (!deployed){
            deployed =CEMLFeeder.startStatements(deployStatements.values()).contains("was successful");
        }


    }
    public void undeploy(){
        if(deployed) {
            CEMLFeeder.pauseStatements(deployStatements.values());
            deployed =false;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Evaluator getEvaluation() {
        return evaluation;
    }


}
