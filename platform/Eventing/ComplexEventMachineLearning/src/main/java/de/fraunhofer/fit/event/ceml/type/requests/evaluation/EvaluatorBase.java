package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.*;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;
import eu.linksmart.gc.utils.gson.GsonSerializable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class EvaluatorBase  implements Evaluator{

    public EvaluatorBase(){
        targets =null;
    }

    public ArrayList<TargetRequest> getTargets() {
        return targets;
    }

    @JsonPropertyDescription("Evaluation metrics needed so the model is ready to be deployed")
    @JsonProperty(value = "Targets")
    protected ArrayList<TargetRequest> targets;

}
