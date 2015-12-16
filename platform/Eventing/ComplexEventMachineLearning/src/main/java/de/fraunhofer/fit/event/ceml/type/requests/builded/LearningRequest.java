package de.fraunhofer.fit.event.ceml.type.requests.builded;

import de.fraunhofer.fit.event.ceml.CEMLFeeder;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
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


    private void loadLearningStatements(){
        Integer i=0;
        for (String strStatement : learningProcess) {
            Statement statement = new LearningStatement("LearningStatement:"+name+i.toString(),this, strStatement);
            leaningStatements.put(statement.getHash(),statement);
            i++;
        }
    }
    private void loadDeploymentStatements(){
        Integer i =0;
        for (String strStatement : deploy) {
            Statement statement = new EPLStatement();
            ((EPLStatement) statement).setName("DeployStatement:" + name + i.toString());
            ((EPLStatement) statement).setStatement(strStatement);
            ((EPLStatement) statement).setStateLifecycle(Statement.StatementLifecycle.PAUSE);

            deployStatements.put(statement.getHash(),statement);
            i ++;
        }
    }
    public void build() throws Exception {

        model.setName(name);
        data.setName(name);
        data.buildInstances();
        model.build(this);

        leaningStatements = new Hashtable<>();
        if(learningProcess==null)
            throw new Exception("The Learning Process was not defined");

        loadLearningStatements();

        deployStatements = new Hashtable<>();
        if(deploy != null) {

            loadDeploymentStatements();
        }
        if(evaluation == null)
            evaluation = new TumbleWindowEvaluator();
        evaluation.build(data.getAttributes().keySet());


    }
    public void reBuild(LearningRequest request){
        if(request.model != null){
            model.reBuild(request.model);
        }
        if(request.learningProcess != null){
            if(leaningStatements!=null && !leaningStatements.isEmpty()){
                CEMLFeeder.removeStatement(leaningStatements.values());
            }

            learningProcess = request.learningProcess;
            loadLearningStatements();
        }
        if(request.deploy != null){
            if(deployStatements!=null && !deployStatements.isEmpty()){
                CEMLFeeder.removeStatement(deployStatements.values());
            }

            deploy = request.deploy;

            loadDeploymentStatements();
        }
        if(request.evaluation != null){
            evaluation.reBuild(request.evaluation);

        }
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
