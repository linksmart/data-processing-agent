package eu.linksmart.ceml.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.almanac.event.datafusion.feeder.StatementFeeder;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.data.DataDefinition;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.ceml.models.AutoregressiveNeuralNetworkModel;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class CEMLManager implements CEMLRequest {
    @JsonProperty(value = "Name")
    protected String name;
    private Configurator conf = Configurator.getDefaultConfig();
    private Logger loggerService = Utils.initLoggingConf(CEMLManager.class);
    private int leadingModel =0;
    @JsonProperty(value = "Descriptors")
    @JsonDeserialize(as = DataDefinition.class)
    protected DataDescriptors descriptors;
    @JsonProperty(value = "Model")
    @JsonDeserialize(as = AutoregressiveNeuralNetworkModel.class)
    protected Model model;
    @JsonProperty(value = "AuxiliaryStreams")
    protected ArrayList<Statement> auxiliaryStatements;
    @JsonProperty(value = "LearningStreams")
    protected ArrayList<LearningStatement> learningStatements;
    @JsonProperty(value = "DeploymentStreams")
    protected ArrayList<Statement> deployStatements;

    @JsonProperty(value = "Settings")
    protected Map<String,Object> settings;

    private boolean deployed=false;
    Prediction lastPrediction;

    @Override
    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }



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
    public Prediction getLastPrediction() {
        return lastPrediction;
    }

    @Override
    public void setLastPrediction(Prediction prediction) {
        lastPrediction= prediction;
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
    public void report() {
        try {
            CEML.report(CEML.getMapper().writeValueAsString(this));

        }catch (JsonProcessingException e) {
            loggerService.error(e.getMessage(),e);
        }
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public JsonSerializable build() throws Exception {

        if(descriptors==null||model==null||learningStatements==null)
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
        model.setName(name);
        model.build();


        MultiResourceResponses<Statement> responses;
        if(auxiliaryStatements!=null&& !auxiliaryStatements.isEmpty()) {
            responses =StatementFeeder.feedStatements(auxiliaryStatements);
            if (!(responses.getOverallStatus()<300))
                throw new Exception(CEML.getMapper().writeValueAsString(responses));
        }
        ArrayList<Statement> arrayList = new ArrayList<>();
        arrayList.addAll(learningStatements);
        responses =StatementFeeder.feedStatements(arrayList);
        if (!responses.containsSuccess())
            throw new Exception(CEML.getMapper().writeValueAsString(responses));

        if(deployStatements!=null&& !deployStatements.isEmpty()) {
            responses =StatementFeeder.feedStatements(deployStatements);
            if (!(responses.getOverallStatus()<300))
                throw new Exception(CEML.getMapper().writeValueAsString(responses));

            if (!(settings.containsKey("AlwaysDeploy")&&(settings.get("AlwaysDeploy") instanceof Boolean &&(settings.get("AlwaysDeploy")).equals(true))))
                //deployStatements.forEach(s->StatementFeeder.pauseStatement(s.getID()));
                StatementFeeder.pauseStatements(deployStatements);


        }


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
