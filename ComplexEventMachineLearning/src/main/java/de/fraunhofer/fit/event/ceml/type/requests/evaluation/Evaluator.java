package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;

import java.util.Collection;
import java.util.Map;

/**
 * Created by angel on 2/12/15.
 */
@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface Evaluator {
     double evaluate(int predicted, int actual);

     boolean isDeployable();

    void build(Collection<String> classesNames) throws Exception;

    void reBuild(Evaluator evaluator);

    Map<String,EvaluationAlgorithm> getEvaluationAlgorithms();

    String report();
    enum EvaluationMetrics{
        truePositives,trueNegatives,falsePositives,falseNegatives
    }

}
