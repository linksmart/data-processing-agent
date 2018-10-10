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
     double evaluate(T predicted, T actual);
    default double evaluate(List<T> predicted, List<T> actual){
        if(predicted.size() == actual.size() && actual.size() == 1 )
            return evaluate(predicted.get(0),actual.get(0));

        throw new UnsupportedOperationException();
    }
    default double evaluate(T predicted, List<T> actual){
        if( actual.size() == 1 )
            return evaluate(predicted,actual.get(0));

        throw new UnsupportedOperationException();
    }
     boolean isDeployable();

    //void build(DataDescriptors classesNames) throws Exception;

    void reBuild(Evaluator<T> evaluator);

    Map<String,EvaluationMetric< Number>> getEvaluationAlgorithms();
    void setParameters(Map<String, Object> parameters);

    String report();


}
