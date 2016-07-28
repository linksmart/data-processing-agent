package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 25.01.2016 a researcher of Fraunhofer FIT.
 */
public class PredictionInstance {
    private String predictedBy="none";
    protected Double certaintyDegree;
    protected boolean acceptedPrediction;
    protected ArrayList<EvaluationMetric> evaluations = null;


    public PredictionInstance(String predictedBy, ArrayList<EvaluationMetric> evaluations, double certaintyDegree) {
        this.predictedBy = predictedBy;
        this.certaintyDegree = certaintyDegree;
        this.evaluations = evaluations;

        acceptedPrediction= certaintyDegree >.99;
    }
    public PredictionInstance(String predictedBy, ArrayList<EvaluationMetric> evaluations) {
        this.predictedBy = predictedBy;


        this.evaluations = evaluations;


    }
    public PredictionInstance() {
        this.acceptedPrediction = false;
        this.certaintyDegree =Double.NEGATIVE_INFINITY;
        this.evaluations = null;
    }


    private void process(){

        for (EvaluationMetric evaluationMetric : evaluations)
            certaintyDegree += evaluationMetric.getNormalizedResult();
        certaintyDegree = certaintyDegree /evaluations.size();
        acceptedPrediction= certaintyDegree >.9999;
    }

    public double getCertaintyDegree() {
        return certaintyDegree;
    }

    public void setCertaintyDegree(double certaintyDegree) {
        this.certaintyDegree = certaintyDegree;
    }

    public boolean isAcceptedPrediction() {
        return acceptedPrediction;
    }

    public void setAcceptedPrediction(boolean acceptedPrediction) {
        this.acceptedPrediction = acceptedPrediction;
    }


}
