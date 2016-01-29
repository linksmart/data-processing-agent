package de.fraunhofer.fit.event.ceml.type.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.sun.org.apache.xpath.internal.operations.Mod;
import de.fraunhofer.fit.event.ceml.CEML;
import de.fraunhofer.fit.event.ceml.CEMLFeeder;
import de.fraunhofer.fit.event.ceml.LearningHandler;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.prediction.Prediction;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.CEPEngineAdvanced;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.classifiers.Evaluation;
import weka.core.Instance;

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
    private int leadingModel =0;

    @JsonPropertyDescription("Data definition")
    @JsonProperty(value = "Data")
    protected DataStructure data;
    @JsonPropertyDescription("Current Model and model definition")
    @JsonProperty(value = "Model")
    protected ArrayList<Model> model;


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


    public ArrayList<Model> getModel() {
        return model;
    }

    public void setModel(ArrayList<Model> model) {
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

        for(Model m: model)
            m.setName(name);
        data.setName(name);
        data.buildInstances();
        for(Model m: model)
            m.build(this);

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
        insertInCEPEngines();


    }
    public void reBuild(LearningRequest request){
        if(request.model != null){
            if(model.size()!=request.model.size()){
                loggerService.error("Models do not match in size while rebuilding");
                return;
            }

            for(int i=0; i<model.size();i++)
                model.get(i).reBuild(request.model.get(i));
        }
        rebuildLearningStatements(request.learningProcess);
        rebuildDeploymentStatements(request.deploy);



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

    public Prediction evaluate(Instance instance){
        Prediction max = new Prediction();

        for (int i=0; i<model.size();i++) {
            Prediction aux =model.get(i).evaluate(instance);

            if (aux.getEvaluationMetricResult()> max.getEvaluationMetricResult()) {
                max = aux;
                leadingModel =i;
            }
            loggerService.info(aux.toString());
        }
        return max;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int insertInCEPEngines(){
        int n=0;
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()      ) {
            CEPEngineAdvanced extended = dfw.getAdvancedFeatures();
            if(extended!=null) {
                extended.insertObject(name, this);
                n++;
            }

        }
        return n;
    }

    public Prediction classify(Map args){
        Instance instance = CEML.populateInstance(args,this);

        return model.get(leadingModel).prediction(instance);


    }



}
