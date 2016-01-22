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
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class LearningRequest  {
    protected String name;
    private Configurator conf = Configurator.getDefaultConfig();
    private LoggerService loggerService = Utils.initDefaultLoggerService(LearningRequest.class);

    @JsonPropertyDescription("Data definition")
    @JsonProperty(value = "Data")
    protected DataStructure data;
    @JsonPropertyDescription("Current Model and model definition")
    @JsonProperty(value = "Model")
    protected Model model;
    @JsonPropertyDescription("Evaluator definition and current evaluation status")
    @JsonProperty(value = "Evaluation")
    private Evaluator evaluation;

    @JsonPropertyDescription("The raw statements that defines the initial pre-processing steps ")
    @JsonProperty(value = "LearningStreams")
    private ArrayList<String> support;
    @JsonPropertyDescription("The raw statements that defines the learning process")
    @JsonProperty(value = "LearningRules")
    private ArrayList<String> learningProcess;
    @JsonPropertyDescription("The raw statements which will be deployed during the time the learnt object reached the evaluation criteria")
    @JsonProperty(value = "DeploymentRules")
    private ArrayList<String> deploy;

    @JsonIgnore
    private  boolean deployed =false;

    public Map<String, Statement> getSupportStatements() {
        return supportStatements;
    }

    @JsonIgnore
    protected Map<String,Statement> deployStatements;
    @JsonIgnore
    protected Map<String,Statement> leaningStatements;
    @JsonIgnore
    protected Map<String,Statement> supportStatements;


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
    private void loadStatements(boolean isDeployment){
        Integer i =0;
        ArrayList<String>  map;
        if(isDeployment)
            map = deploy;
        else
            map =support;

        for (String strStatement : map) {
            Statement statement = new EPLStatement();
            if(isDeployment) {
                ((EPLStatement) statement).setName("DeployStatement:" + name + i.toString());
                ((EPLStatement) statement).setStateLifecycle(Statement.StatementLifecycle.PAUSE);
            }else {
                ((EPLStatement) statement).setName("SupportStatement:" + name + i.toString());
                ((EPLStatement) statement).setCEHandler(null);

            }
            ((EPLStatement) statement).setStatement(strStatement);



            if( isDeployment)
                deployStatements.put(statement.getHash(),statement);
            else
                supportStatements.put(statement.getHash(),statement);

            i ++;
        }
    }
    public void build() throws Exception {

        model.setName(name);
        data.setName(name);
        data.buildInstances();
        model.build(this);
        supportStatements = new Hashtable<>();
        if(support != null) {

            loadStatements(false);
        }

        leaningStatements = new Hashtable<>();
        if(learningProcess==null)
            throw new Exception("The Learning Process was not defined");

        loadLearningStatements();

        deployStatements = new Hashtable<>();
        if(deploy != null) {

            loadStatements(true);
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


            loadStatements(true);
        }
    }
    public void deploy(){
      if (!deployed){
          loggerService.info("Request "+name+" is being deployed");
           if( deployed =CEMLFeeder.startStatements(deployStatements.values()).contains("was successful"))
               loggerService.info("Request "+name+" has been deployed");

        }


    }
    public void undeploy(){
        if(deployed) {
            CEMLFeeder.pauseStatements(deployStatements.values());
            deployed =false;
            loggerService.info("Request "+name+" had been removed from active deployment");
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
