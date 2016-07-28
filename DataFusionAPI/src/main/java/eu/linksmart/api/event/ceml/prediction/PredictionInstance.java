package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 25.01.2016 a researcher of Fraunhofer FIT.
 */
public class PredictionInstance<T> implements Prediction<T> {
    private String predictedBy="none";
    protected Double certaintyDegree;
    protected boolean acceptedPrediction;
    private T prediction;
    protected Collection<EvaluationMetric> evaluations = null;


    public PredictionInstance(T prediction,String predictedBy, ArrayList<EvaluationMetric> evaluations, double certaintyDegree) {
        this.predictedBy = predictedBy;
        this.certaintyDegree = certaintyDegree;
        this.evaluations = evaluations;
        this.prediction =prediction;

        acceptedPrediction= certaintyDegree >.99;
    }
    public PredictionInstance(T prediction,String predictedBy, Collection<EvaluationMetric> evaluations) {
        this.predictedBy = predictedBy;
        this.evaluations = evaluations;
        this.prediction =prediction;
        this.certaintyDegree = calculateDegreeCertainty();


    }
    public PredictionInstance() {
        this.acceptedPrediction = false;
        this.certaintyDegree =Double.NEGATIVE_INFINITY;
        this.evaluations = null;
        this.prediction =null;
    }


    @Override
    public Double getCertaintyDegree() {
        return certaintyDegree;
    }

    public  void setCertaintyDegree(double certaintyDegree) {
        this.certaintyDegree = certaintyDegree;
    }

    public boolean isAcceptedPrediction() {
        return acceptedPrediction;
    }

    public void setAcceptedPrediction(boolean acceptedPrediction) {
        this.acceptedPrediction = acceptedPrediction;
    }

    @Override
    public Collection<EvaluationMetric> getEvaluationMetrics() {
        return evaluations;
    }


    public String getPredictedBy() {
        return predictedBy;
    }

    public void setPredictedBy(String predictedBy) {
        this.predictedBy = predictedBy;
    }

    @Override
    public T getPrediction() {
        return prediction;
    }
}
