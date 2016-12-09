package eu.linksmart.services.event.ceml.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.feeder.StatementFeeder;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.data.DataDefinition;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.ceml.models.AutoregressiveNeuralNetworkModel;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class CEMLManager implements CEMLRequest {
    @JsonProperty(value = "Name")
    protected String name;
    @JsonProperty(value = "Descriptors")
    @JsonDeserialize(as = DataDefinition.class)
    protected DataDescriptors descriptors;
    @JsonProperty(value = "Model")
    @JsonDeserialize(as = AutoregressiveNeuralNetworkModel.class)
    protected Model model;
    @JsonProperty(value = "AuxiliaryStreams")
    protected List<Statement> auxiliaryStatements;
    @JsonProperty(value = "LearningStreams")
    protected List<LearningStatement> learningStatements;
    @JsonProperty(value = "DeploymentStreams")
    protected List<Statement> deployStatements;
    @JsonProperty(value = "Settings")
    protected Map<String,Object> settings;
    @JsonProperty(value = "isDeployed")
    protected boolean deployed=false;
    @JsonProperty(value = "LastPrediction")
    protected Prediction lastPrediction;

    @JsonIgnore
    private transient Configurator conf = Configurator.getDefaultConfig();
    @JsonIgnore
    private transient Logger loggerService = Utils.initLoggingConf(CEMLManager.class);
    @JsonIgnore
    private transient boolean built =false;

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
    public void deploy()  {
        if (!deployed){
            loggerService.info("Request "+name+" is being deployed");
            MultiResourceResponses<Statement> responses =StatementFeeder.startStatements(deployStatements);
            if((deployed=(responses.containsSuccess())))
                loggerService.info("Request "+name+" has been deployed");

        }
    }

    @Override
    public void undeploy() {
        if(deployed) {
            StatementFeeder.pauseStatements(deployStatements);
            deployed =false;
            loggerService.info("Request "+name+" had been removed from active deployment");
        }
    }

    @Override
    public void report() {
        try {
            CEML.report(name,CEML.getMapper().writeValueAsString(this));

        }catch (JsonProcessingException e) {
            loggerService.error(e.getMessage(),e);
        }
    }

    @Override
    public void setName(String name) {
        if(!built)
            this.name=name;
        else
            loggerService.error("An illegal intent of changing the name of the request "+this.name+" after the building process");
    }

    @Override
    public JsonSerializable build() throws TraceableException, UnknownUntraceableException {
        boolean[] phasesDone = {false, false, false, false, false, false, false, false, false, false};
        String[] phasesNames ={
                "pre-requisites", "descriptors building", "auxiliary statement building", "learning statement building",
                "deployment statement building", "model building", "request insertion", "auxiliary statement creation", "learning statement creation",
                "deployment statement creation"};
        int[] statementsCounter ={0,0,0,0,0,0};
        Exception exception =null;
        List<MultiResourceResponses<Statement>> responses = new LinkedList<>();

        // preRequisites, descriptorBuilt, statementAuxiliaryBuilt, statementLearningBuilt, statementDeploymentBuilt, modelBuilt, requestInserted, learningStatementDeploy, auxiliaryStatementDeploy, deploymentStatementDeploy;

        try {

            if (descriptors == null || model == null || learningStatements == null)
                throw new Exception("The descriptors, model and evaluator are mandatory fields!");
            phasesDone[0] =true;

            descriptors.build();
            phasesDone[1] = true;

            if(phasesDone[1]) {
                if (auxiliaryStatements != null)
                    for (Statement statement : auxiliaryStatements) {
                        statement.setCEHandler("");
                        statement.setName("AuxiliaryStream[" + String.valueOf(statementsCounter[0]) + "]:" + name );
                        statement.setId("AS[" + String.valueOf(statementsCounter[0]) + "]:" + name);
                        statement.setStatement(statement.getStatement().replace("<id>",name));
                        statement.build();
                        statementsCounter[0]++;
                    }
                phasesDone[2] =  (auxiliaryStatements == null || (auxiliaryStatements.size() == statementsCounter[0]));

                if (learningStatements != null)
                    for (LearningStatement statement : learningStatements) {
                        statement.setRequest(this);
                        statement.setName("LearningStream[" + String.valueOf(statementsCounter[0]) + "]:" + name );
                        statement.setId("LS[" + String.valueOf(statementsCounter[0]) + "]:" + name );
                        statement.setStatement(statement.getStatement().replace("<id>",name));
                        statement.build();
                        statementsCounter[1]++;
                    }
                phasesDone[3]=(learningStatements != null && learningStatements.size() == statementsCounter[1]);

                if (deployStatements != null)
                    for (Statement statement : deployStatements) {
                        statement.setName("DeploymentStream[" + String.valueOf(statementsCounter[0]) + "]:" + name );
                        statement.setId("DS[" + String.valueOf(statementsCounter[0]) + "]:" + name );
                        statement.setStatement(statement.getStatement().replace("<id>",name));
                        statement.build();
                        statementsCounter[2]++;

                    }
                phasesDone[4] = (deployStatements == null || (deployStatements.size() == statementsCounter[2]));
            }

            if(phasesDone[4]) {
                model.setDescriptors(descriptors);
                model.setName(name);
                model.build();
                phasesDone[5] = true;
            }

            if(phasesDone[5]) {
                insertInCEPEngines();
                phasesDone[6] = true;
            }

            if (phasesDone[6] && auxiliaryStatements != null && !auxiliaryStatements.isEmpty()) {
                MultiResourceResponses<Statement> response;
                for (Statement statement : auxiliaryStatements) {
                    response = StatementFeeder.feedStatement(statement);
                    responses.add(response);
                    if (!(phasesDone[7] = response.getOverallStatus() < 300))
                        break;
                    statementsCounter[3]++;
                    //throw new Exception(CEML.getMapper().writeValueAsString(responses));
                }
            } else
                phasesDone[7] = phasesDone[6];

            if (phasesDone[7] && learningStatements != null && !learningStatements.isEmpty()) {
                MultiResourceResponses<Statement> response;
                for (Statement statement : learningStatements) {
                    response = StatementFeeder.feedStatement(statement);
                    responses.add(response);
                    if (!(phasesDone[8] = response.getOverallStatus() < 300))
                        break;
                    statementsCounter[4]++;
                    //throw new Exception(CEML.getMapper().writeValueAsString(responses));
                }
            }
            if (phasesDone[8] && deployStatements != null && !deployStatements.isEmpty()) {
                MultiResourceResponses<Statement> response;
                for (Statement statement : deployStatements) {
                    response = StatementFeeder.feedStatement(statement);
                    responses.add(response);
                    if (!(phasesDone[9] = response.getOverallStatus() < 300))
                        break;

                    StatementFeeder.pauseStatement(statement);
                    statementsCounter[5]++;
                }

            } else
                phasesDone[9] = phasesDone[8];


        }catch (Exception e){
            exception =e;
        }
        errorHandling(phasesDone,phasesNames,statementsCounter,exception, !responses.isEmpty()? responses.get(responses.size()-1): null);

        built=true;
        return this;

    }
/*
    @Override
    public void rebuild(CEMLRequest me) throws Exception {

    }
*/
    @Override
    public void destroy() throws Exception{

        rollbackStatements(deployStatements, deployStatements.size());

        rollbackStatements(learningStatements,learningStatements.size());

        rollbackStatements(auxiliaryStatements,auxiliaryStatements.size());

        dropInCEPEngines();

        model.destroy();

        descriptors.destroy();


    }
    private void errorHandling(boolean[] phasesDone,String[] phasesNames,int[] buildStatements, Exception exception,  MultiResourceResponses<Statement> response)throws TraceableException, UnknownUntraceableException {
        int i =0;
        for(; i<phasesDone.length && phasesDone[i];) i++;

        if(i<10) {
            String base = "Error in the " + phasesNames[i] + " phase (" + String.valueOf(i) + ") of CEML creation phases ";
            String message = null;
            if (exception != null)
                message = base + ": " + exception.getMessage();

            if ((i < 3 || (i > 4 && i < 6)) && (exception == null))
                message = base + ": Basic requisites for building the request have not being met";
            else if ((i > 2 && i < 5) || (i > 6 && i < 10)) {
                base += " on statement [" + String.valueOf(buildStatements[i - (i<5? 3: 4)]) + "]: ";
                if (exception != null)
                    message = base + exception.getMessage();
                else if (i > 2 && i < 5) {
                    message = base + "Unknown error while building the statement";
                } else if (i > 6 && i < 10) {
                    if (response != null)
                        message = base + " " + response.getResponsesTail().getMessage();
                    else
                        message = base + " Unknown error while inserting the learning statement into the engine";
                }
                if (i > 6 && i < 10) {
                    try {
                        rollback(i, buildStatements);
                    } catch (Exception e) {
                        message = "The request failed due to: " + message + ". The agent tried to rollback to a satisfactory state and failed due to: " + e.getMessage();
                    }
                }
            } else if (i == 6 && exception == null) {
                message = base + ": Unknown error while inserting the request object into the engine";
            }

            if (message != null) {

                if(exception!=null) {
                    if (exception instanceof StatementException || exception instanceof InternalException || exception instanceof UnknownException)
                        if(response!=null) {
                            response.getResponsesTail().setMessage(message);
                            throw new ErrorResponseException(response.getResponsesTail());
                        }else if (exception instanceof StatementException )
                            throw new StatementException(((TraceableException) exception).getErrorProducerId(), ((TraceableException) exception).getErrorProducerType(), message, exception);
                        else   if ( exception instanceof InternalException )
                            throw new InternalException(((TraceableException) exception).getErrorProducerId(), ((TraceableException) exception).getErrorProducerType(), message, exception);
                        else
                            throw new UnknownException(((TraceableException) exception).getErrorProducerId(), ((TraceableException) exception).getErrorProducerType(), message, exception);
                    else if (exception instanceof UnknownUntraceableException)
                        throw new UnknownUntraceableException(message, exception);
                }else {
                    if (response != null){
                        response.getResponsesTail().setMessage(message);
                        throw new ErrorResponseException(response.getResponsesTail());
                    }else
                        throw new UnknownUntraceableException(message);
                }
            }
        }

    }

    private void rollback(int fromPhase, int[] buildStatements) throws Exception{
        if(fromPhase>8){
            if(deployStatements!=null && !deployStatements.isEmpty())
                rollbackStatements(deployStatements,buildStatements[5]);
        }
        if(fromPhase>7){
            if(learningStatements!=null && !learningStatements.isEmpty()) {
                rollbackStatements(learningStatements, buildStatements[4]);
            }
        }
        if(fromPhase>6){
            if(auxiliaryStatements!=null && !auxiliaryStatements.isEmpty())
                rollbackStatements(auxiliaryStatements,buildStatements[3]);
        }

        if(fromPhase>5){
            dropInCEPEngines();
        }

    }
    private void rollbackStatements(List statements, int till) throws Exception{

        Statement aux = null;
        try {
            for (int i=0; i<till;i++) {
                if(statements.get(i) instanceof Statement) {
                    aux = (Statement) statements.get(i);
                    StatementFeeder.removeStatement(aux);
                }
            }
        }catch (Exception e){
            if(aux!=null)
                throw new Exception("Error while rolling back statement named "+aux.getName()+ " with ID "+aux.getID()+"; the agent may had being left in an unstable state");
            else
                throw new Exception("Error while rolling back statements; the agent may had being left in an unstable state");
        }
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
    public int dropInCEPEngines(){
        int n=0;
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()      ) {
            CEPEngineAdvanced extended = dfw.getAdvancedFeatures();
            if(extended!=null) {
                extended.dropObject(name);
                n++;
            }

        }
        return n;
    }
    public EventEnvelope predict(Object input){
        try {
            Object aux = input;
            List<EventEnvelope> orgInput= null;
            if(input instanceof ArrayList) {
                List aux1= (ArrayList)input;
                if(!aux1.isEmpty()&& aux1.get(1) instanceof EventEnvelope) {
                    orgInput = (ArrayList<EventEnvelope>) aux1;
                    aux = orgInput.stream().map(i -> (Object) i.getValue()).collect(Collectors.toList());
                }else
                    aux = input;
            }if (input instanceof EventEnvelope[]){
                orgInput = new ArrayList<>(Arrays.asList((EventEnvelope[]) input));
                aux = orgInput.stream().map(i -> (Object) i.getValue()).collect(Collectors.toList());

            }else if(input instanceof Object[])
                aux=Arrays.asList((Object[])input);

            Prediction prediction = model.predict(aux);
            prediction.setOriginalInput(input);

           setLastPrediction(prediction);
            if(orgInput==null)
                return Observation.factory(prediction,"Prediction",name, DynamicConst.getId());
            else {
                return Observation.factory(prediction,"Prediction",name,DynamicConst.getId(), orgInput.get(orgInput.size()-1).getDate().getTime());
            }
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return Observation.factory(e.getMessage(),"Error",name,DynamicConst.getId());
        }
    }
}
