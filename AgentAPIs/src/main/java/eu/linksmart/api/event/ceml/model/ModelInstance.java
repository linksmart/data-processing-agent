package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.SchemaNode;


import java.util.*;

/**
 * Created by José �?ngel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 * Class implementing Autoregressive neural networks.
 */

// TODO TBD

public abstract class ModelInstance<Input,Output,LearningObject> implements Model<Input,Output,LearningObject>{

    @JsonProperty("descriptors")
    protected DataDescriptors descriptors;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("nativeType")
    protected Class<LearningObject> nativeType;
    @JsonPropertyDescription("Algorithm use to build the model")
    @JsonProperty(value = "type")
    protected String type;
    //@JsonProperty(value = "Evaluator")
    @JsonProperty(value = "evaluator")
    protected  Evaluator<Output> evaluator ;

    @JsonProperty(value = "targets")
    protected  List<TargetRequest> targets;

    @JsonProperty(value = "parameters")
    protected  Map<String,Object> parameters;
    @JsonProperty(value = "prediction")
    protected Prediction<Output> lastPrediction;

    @JsonProperty(value = "learner")
    protected LearningObject learner;
    @JsonProperty("dataSchema")
    protected SchemaNode schema;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public LearningObject getLearner() {
        return learner;
    }


    public void setLearner(LearningObject learner) {
        this.learner = learner;
    }

   // @JsonIgnore
   // protected Class<? extends Model>type;

    //final protected String modelName;

    public ModelInstance(List<TargetRequest> targets,Map<String,Object> parameters, Evaluator evaluator, Object learner){
        this.name = this.getClass().getSimpleName();
        this.targets = targets;
        this.parameters =parameters;
        this.evaluator = evaluator;
        this.evaluator.setParameters(parameters);
        this.lastPrediction = new PredictionInstance<>();
        this.learner = (LearningObject) learner;
    }


    @Override
    public Evaluator<Output> getEvaluator() {
        return evaluator;
    }

    @Override
    public void setDescriptors(DataDescriptors descriptors) {
        this.descriptors = descriptors;

    }

    @Override
    public DataDescriptors getDescriptors() {
        return descriptors;
    }

    @Override
    public SchemaNode getDataSchema() {
        return schema;
    }

    @Override
    public void setDataSchema(SchemaNode schema) {
        this.schema=schema;

    }

    @Override
    public Prediction<Output> getLastPrediction() {
        return lastPrediction;
    }

    @Override
    public void setLastPrediction(Prediction<Output> value) {
        lastPrediction = value;
    }


    @Override
    public Model<Input, Output, LearningObject> build() throws TraceableException, UntraceableException {
        if(descriptors== null || !descriptors.isEmpty() ||evaluator== null  || learner == null)
            throw new StatementException(this.getClass().getName(),this.getClass().getCanonicalName(),"For the model the descriptors, evaluator and learner are mandatory fields!");

        nativeType = (Class<LearningObject>) learner.getClass();

        evaluator.build();

        return this;
    }
    public void learn(Input input, Input targetLabel) throws TraceableException, UntraceableException {
        if (input instanceof List) {
            List complete = new ArrayList((List)input);
            if (targetLabel instanceof List)
                complete.addAll((List) targetLabel);
            else
                complete.add(targetLabel);

            learn((Input)complete);

        }else if (input instanceof Map) {
            Map complete = new Hashtable((Map)input);
            complete.put("target",targetLabel);
            learn((Input) complete);
        }else
            throw new InternalException(getName(),"Internal Server Error","Unsupported type of data by the default learn function. Plase use list or map");

    }
    public void batchLearn(List<Input> input, List<Input> targetLabel) throws TraceableException, UntraceableException {
        if(input != null && !input.isEmpty() && targetLabel != null && !targetLabel.isEmpty() && input.size() == targetLabel.size()) {
            for(Input inputInstance : input)
                for(Input targetInputInstance : input) {

                    learn(inputInstance, targetInputInstance);
                }
        }
    }

/*
    @Override
    public void rebuild(Model<Input, Output, LearningObject> me) throws Exception {
        if(me.getTargets()!=null)
            this.targets = me.getTargets();
        if(me.getParameters()!=null)
            this.parameters =me.getParameters();

        if(me.getEvaluator()!=null || me.getParameters()!=null)
            this.evaluator.rebuild(me.getEvaluator());
        if(me.getLastPrediction()!=null)
            this.lastPrediction = me.getLastPrediction();
    }
*/

    @Override
    public void destroy() throws Exception {
        // nothing
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public Class getNativeType() {
        return nativeType;
    }
    @Override
    public void setNativeType(Class nativeType) {
        this.nativeType = nativeType;
    }
    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }
    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public List<TargetRequest> getTargets() {
        return targets;
    }
    @Override
    public void setTargets(List<TargetRequest> targets) {
        this.targets = targets;
    }


}