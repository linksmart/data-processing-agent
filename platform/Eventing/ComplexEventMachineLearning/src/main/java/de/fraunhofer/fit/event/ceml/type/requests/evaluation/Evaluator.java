package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

import java.util.Collection;

/**
 * Created by angel on 2/12/15.
 */
public interface Evaluator {
     boolean evaluate(int predicted, int actual);

     boolean isDeployable();

    void build(Collection<String> classesNames);

    enum EvaluationMetrics{
        truePositives,trueNegatives,falsePositives,falseNegatives
    }
}
