package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;


import java.util.*;

/**
 * Created by José �?ngel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 * Class implementing Autoregressive neural networks.
 */

// TODO TBD
public abstract class ModelInstance<Input,Output,LearningObject> implements Model<Input,Output,LearningObject>{

    @JsonIgnore
    protected DataDescriptors descriptors;

    protected String name;
    protected Class<LearningObject> nativeType;
    @JsonPropertyDescription("Algorithm use to build the model")
    @JsonProperty(value = "Type")
    protected String type;
    @JsonProperty(value = "Evaluator")
    protected  Evaluator<Output> evaluator ;
    @JsonProperty(value = "Targets")
    protected  ArrayList<TargetRequest> targets;
    @JsonProperty(value = "Parameters")
    protected  Map<String,Object> parameters;
    @JsonProperty(value = "Prediction")
    protected Prediction<Output> lastPrediction;
    @JsonIgnore
    protected LearningObject lerner;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @JsonIgnore
    public LearningObject getLerner() {
        return lerner;
    }

    @JsonIgnore
    public void setLerner(LearningObject lerner) {
        this.lerner = lerner;
    }

   // @JsonIgnore
   // protected Class<? extends Model>type;

    //final protected String modelName;

    public ModelInstance(ArrayList<TargetRequest> targets,Map<String,Object> parameters, Evaluator evaluator){
        this.targets = targets;
        this.parameters =parameters;
        this.evaluator = evaluator;
        this.evaluator.setParameters(parameters);
        this.lastPrediction = new PredictionInstance<>();

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
    public JsonSerializable build() throws Exception {
        if(descriptors== null || !descriptors.isEmpty() ||evaluator== null  || lerner == null)
            throw new Exception("For the model the descriptors, evaluator and learner are mandatory fields!");

        nativeType = (Class<LearningObject>) lerner.getClass();

        evaluator.build();

        return this;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getNativeType() {
        return nativeType;
    }

    public void setNativeType(Class nativeType) {
        this.nativeType = nativeType;
    }

}