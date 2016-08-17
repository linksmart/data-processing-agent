package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 28.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Prediction<T> {

     Double getCertaintyDegree();

     void setCertaintyDegree(double certaintyDegree) ;

     boolean isAcceptedPrediction();

     void setAcceptedPrediction(boolean acceptedPrediction);

    Collection<EvaluationMetric> getEvaluationMetrics();

    String getPredictedBy();

    void setPredictedBy(String predictedBy);

    T getPrediction();

    default Double calculateDegreeCertainty(){
        Double certaintyDegree =0.0;
        for (EvaluationMetric evaluationMetric : getEvaluationMetrics())
            certaintyDegree += evaluationMetric.getNormalizedResult()/getEvaluationMetrics().size();

        return certaintyDegree;
    }

    Object getOriginalInput();

    void setOriginalInput(Object originalInput);

    Date getMadeAt();

    void setMadeAt(Date originalInput);
}
