package eu.linksmart.api.event.ceml.evaluation;


import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

import java.util.Collection;
import java.util.Map;

/**
 * Created by angel on 2/12/15.
 */
//@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface Evaluator {
     double evaluate(int predicted, int actual);

     boolean isDeployable();

    void build(Collection<String> classesNames) throws Exception;

    void reBuild(Evaluator evaluator);

    Map<String,EvaluationMetric> getEvaluationAlgorithms();

    String report();
    enum EvaluationClassificationValues {
        truePositives,trueNegatives,falsePositives,falseNegatives
    }

}
