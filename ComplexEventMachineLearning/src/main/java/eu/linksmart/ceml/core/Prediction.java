package eu.linksmart.ceml.core;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 25.01.2016 a researcher of Fraunhofer FIT.
 */
public class Prediction {
    protected int predictedClass;
    protected String predictedClassName;
    protected double evaluationMetricResult;
    protected boolean acceptedPrediction;
    protected ArrayList<EvaluationMetric> evaluations = null;


    public Prediction(int predictedClass, String predictedClassName, String predictedBy, ArrayList<EvaluationMetric> evaluations,double evaluationMetricResult ) {
        this.predictedClass = predictedClass;
        this.predictedClassName = predictedClassName;

        this.evaluationMetricResult=evaluationMetricResult;
        this.evaluations = evaluations;

        acceptedPrediction= evaluationMetricResult>.99;
    }
    public Prediction(int predictedClass, String predictedClassName, String predictedBy, ArrayList<EvaluationMetric> evaluations ) {
        this.predictedClass = predictedClass;
        this.predictedClassName = predictedClassName;


        this.evaluations = evaluations;


    }
    private void process(){

        for (EvaluationMetric evaluationMetric : evaluations)
            evaluationMetricResult+= evaluationMetric.getNormalizedResult();
        evaluationMetricResult= evaluationMetricResult/evaluations.size();
        acceptedPrediction=evaluationMetricResult>.99;
    }
    public Prediction( ) {
        this.predictedClass = -1;
        this.predictedClassName = "";
        this.acceptedPrediction = false;
        this.evaluationMetricResult=Double.NEGATIVE_INFINITY;
        this.evaluations = null;
    }

    public int getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(int predictedClass) {
        this.predictedClass = predictedClass;
    }

    public String getPredictedClassName() {
        return predictedClassName;
    }

    public void setPredictedClassName(String predictedClassName) {
        this.predictedClassName = predictedClassName;
    }


    public double getEvaluationMetricResult() {
        return evaluationMetricResult;
    }

    public void setEvaluationMetricResult(double evaluationMetricResult) {
        this.evaluationMetricResult = evaluationMetricResult;
    }

    public boolean isAcceptedPrediction() {
        return acceptedPrediction;
    }

    public void setAcceptedPrediction(boolean acceptedPrediction) {
        this.acceptedPrediction = acceptedPrediction;
    }

    @Override
    public String toString(){
        return "\n(R) Prediction> prediction (index, name):("+predictedClass+", "+predictedClassName+") accepted: "+acceptedPrediction+" evaluationRate: "+evaluationMetricResult;
    }
}
