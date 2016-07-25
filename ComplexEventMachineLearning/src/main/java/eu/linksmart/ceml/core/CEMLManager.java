package eu.linksmart.ceml.core;

import eu.almanac.event.datafusion.feeder.StatementFeeder;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.data.DataDefinition;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.ceml.models.AutoregressiveNeuralNetworkModel;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class CEMLManager implements CEMLRequest {

    protected String name;
    private Configurator conf = Configurator.getDefaultConfig();
    private LoggerService loggerService = Utils.initDefaultLoggerService(CEMLManager.class);
    private int leadingModel =0;
    @JsonDeserialize(as = DataDefinition.class)
    protected DataDescriptors descriptors;
    @JsonDeserialize(as = AutoregressiveNeuralNetworkModel.class)
    protected Model model;

    protected ArrayList<Statement> auxiliaryStatements;
    protected ArrayList<LearningStatement> learningStatements;
    protected ArrayList<Statement> deployStatements;
    private boolean deployed=false;

    @Override
    public DataDescriptors getDescriptors() {
        return descriptors;
    }

    @Override
    public Model getModel() {
        return model;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<LearningStatement> getLearningStreamStatements() {
        return learningStatements;
    }

    @Override
    public Collection< Statement> getDeploymentStreamStatements() {
        return deployStatements;
    }

    @Override
    public Collection< Statement> getAuxiliaryStreamStatements() {
        return auxiliaryStatements;
    }

    @Override
    public Statement getStreamStatement(String StatementId) {
        return null;
    }

    @Override
    public void deploy() throws Exception {
        if (!deployed){
            loggerService.info("Request "+name+" is being deployed");
            MultiResourceResponses<Statement> responses =StatementFeeder.startStatements(deployStatements);
            if((deployed=(responses.containsSuccess())))
                loggerService.info("Request "+name+" has been deployed");

        }
    }

    @Override
    public void undeploy() throws Exception {
        if(deployed) {
            StatementFeeder.pauseStatements(deployStatements);
            deployed =false;
            loggerService.info("Request "+name+" had been removed from active deployment");
        }
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public JsonSerializable build() throws Exception {

        if(descriptors==null||model==null||learningStatements!=null)
            throw new Exception("The descriptors, model and evaluator are mandatory fields!");
        descriptors.build();

        int i=0;
        if(auxiliaryStatements!=null)
            for (Statement statement: auxiliaryStatements) {
                statement.setCEHandler("");
                statement.setName("AuxiliaryStream:" + name + "[" + String.valueOf(i) + "]");
                statement.build();
                i++;
            }
        for (LearningStatement statement: learningStatements) {
            statement.setRequest(this);
            statement.setName("LearningStream:" + name + "[" + String.valueOf(i) + "]");
            statement.build();
        }
        if(deployStatements!=null)
        for (Statement statement: deployStatements){
            statement.setName("DeploymentStream:" + name + "[" + String.valueOf(i) + "]");
            statement.build();

        }

        model.setDescriptors(descriptors);

        model.build();



        StatementFeeder.feedStatements(auxiliaryStatements);
        ArrayList<Statement> arrayList = new ArrayList<>();
        arrayList.addAll(learningStatements);
        StatementFeeder.feedStatements(arrayList);
        StatementFeeder.feedStatements(deployStatements);

        return this;
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
}
