package eu.linksmart.api.event.ceml.evaluation;


import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;


import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by angel on 2/12/15.
 */
//@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface Evaluator<T> extends JsonSerializable {
     double evaluate(T predicted, T actual);

     boolean isDeployable();

    //void build(DataDescriptors classesNames) throws Exception;

    void reBuild(Evaluator<T> evaluator);

    Map<String,EvaluationMetric< Number>> getEvaluationAlgorithms();
    public void setParameters(Map<String, Object> parameters);

    String report();


}
