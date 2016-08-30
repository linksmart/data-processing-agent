package eu.linksmart.api.event.ceml.evaluation.metrics;



import eu.linksmart.api.event.datafusion.types.JsonSerializable;

/**
 * Created by angel on 4/12/15.
 */
public interface EvaluationMetric<T>  extends JsonSerializable {

    void setComparisonMethod(ComparisonMethod method);
    void reBuild(MetricDefinition<T> evaluationAlgorithm);
    boolean isReady();
    T getTarget();
    void setTarget(T target);
    T getResult();
    ModelEvaluationAlgorithmExtended getExtended();
    String report();
    double getNormalizedResult();
    void reset();
    boolean isControlMetric();
    enum ComparisonMethod{
        Equal, More, MoreEqual, Less, LessEqual
    }
}
