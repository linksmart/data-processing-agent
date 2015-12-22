package de.fraunhofer.fit.event.ceml.type.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import de.fraunhofer.fit.event.ceml.CEMLFeeder;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class LearningRequest  {
    protected String name;
    private Configurator conf = Configurator.getDefaultConfig();

    @JsonPropertyDescription("Data definition")
    @JsonProperty(value = "Data")
    protected DataStructure data;
    @JsonPropertyDescription("Current Model and model definition")
    @JsonProperty(value = "Model")
    protected Model model;
    @JsonPropertyDescription("Evaluator definition and current evaluation status")
    @JsonProperty(value = "Evaluation")
    private Evaluator evaluation;
    @JsonPropertyDescription("The raw statements that defines the learning process")
    @JsonProperty(value = "LearningProcess")
    private ArrayList<String> learningProcess;
    @JsonPropertyDescription("The raw statements which will be deployed during the time the learnt object reached the evaluation criteria")
    @JsonProperty(value = "Deployment")
    private ArrayList<String> deploy;

    @JsonIgnore
    private  boolean deployed =false;
    @JsonIgnore
    protected Map<String,Statement> deployStatements;
    @JsonIgnore
    protected Map<String,Statement> leaningStatements;


    public Map<String, Statement> getDeployStatements() {
        return deployStatements;
    }
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
            evaluation = new DoubleTumbleWindowEvaluator();
        evaluation.build(data.getAttributes().keySet());


    }
    public void reBuild(LearningRequest request){
        if(request.model != null){
            model.reBuild(request.model);
        }
        rebuildLearningStatements(request.learningProcess);
        rebuildDeploymentStatements(request.deploy);

        if(request.evaluation != null){
            evaluation.reBuild(request.evaluation);

        }
    }

    public void rebuildLearningStatements(ArrayList<String> statements){


        if(statements!= null){

            if(leaningStatements!=null && !leaningStatements.isEmpty()){
                CEMLFeeder.removeStatement(leaningStatements.values());
            }

            learningProcess = statements;
            loadLearningStatements();
        }

    }

    public void rebuildDeploymentStatements(ArrayList<String> statements){
        if(statements != null){
            if(deployStatements!=null && !deployStatements.isEmpty()){
                CEMLFeeder.removeStatement(deployStatements.values());
            }

            deploy = statements;

            loadDeploymentStatements();
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
