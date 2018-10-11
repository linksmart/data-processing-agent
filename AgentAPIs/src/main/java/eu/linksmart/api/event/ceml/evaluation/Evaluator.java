package eu.linksmart.api.event.ceml.evaluation;


import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;


import java.util.List;
import java.util.Map;

/**
 * Created by angel on 2/12/15.
 */
//@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface Evaluator<T> extends JsonSerializable {
     double evaluate(List<T> predicted, List<T> actual);
     boolean isDeployable();

    //void build(DataDescriptors classesNames) throws Exception;

    void reBuild(Evaluator<T> evaluator);

    Map<String,EvaluationMetric< Number>> getEvaluationAlgorithms();
    void setParameters(Map<String, Object> parameters);

    String report();


}
