package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;
import eu.linksmart.gc.utils.gson.Gerializable;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by angel on 2/12/15.
 */
@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface Evaluator {
     boolean evaluate(int predicted, int actual);

     boolean isDeployable();

    void build(Collection<String> classesNames) throws Exception;

    void reBuild(Evaluator evaluator);

    String report();
    enum EvaluationMetrics{
        truePositives,trueNegatives,falsePositives,falseNegatives
    }

}
