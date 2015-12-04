package de.fraunhofer.fit.event.ceml.type.requests;

import de.fraunhofer.fit.event.ceml.CEMLFeeder;
import de.fraunhofer.fit.event.ceml.type.requests.builded.Model;
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
    protected DataStructureRequest data;
    private  boolean deployed =false;
    private  boolean hasBeenDeployed =false;

    protected Map<String,Statement> leaningStatements;
    protected Map<String,Statement> deployStatements;

    protected Model model;

    private Evaluator evaluation;
    private ArrayList<String> learningProcess;
    private ArrayList<String> deploy;


    public Map<String,Statement> getLeaningStatements() {
        return leaningStatements;
    }



    public DataStructureRequest getData() {
        return data;
    }

    public void setData(DataStructureRequest data) {
        this.data = data;
    }


    public ModelStructure getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }



    public void build() throws Exception {

        model.setName(name);
        data.setName(name);
        data.buildInstances();
        //model.build(this);
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
                deployStatements.put(statement.getHash(),statement);
                i = 0;
            }
        }
        if(evaluation == null)
            evaluation = new TumbleWindowEvaluator();


    }
    public void deploy(){
        if(!deployed &&!hasBeenDeployed) {
            deployed =hasBeenDeployed =CEMLFeeder.feedStatements(deployStatements.values()).contains("was successful");

        }else if (hasBeenDeployed && !deployed){
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

    public void insertLearningObjectInEngines(){
        model.insertInCEPEngines();
    }
}
