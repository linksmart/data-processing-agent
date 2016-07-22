package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class EvaluatorBase<T>  implements Evaluator<T> {

    public EvaluatorBase(){
        targets =null;
    }

    public ArrayList<TargetRequest> getTargets() {
        return targets;
    }

    @JsonPropertyDescription("Evaluation metrics needed so the model is ready to be deployed")
    @JsonProperty(value = "Targets")
    protected ArrayList<TargetRequest> targets;
    public void setTargets(ArrayList<TargetRequest> targets) {
        this.targets = targets;
    }

}
