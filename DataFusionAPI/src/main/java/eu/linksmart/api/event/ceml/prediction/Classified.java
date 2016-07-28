package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 28.07.2016 a researcher of Fraunhofer FIT.
 */
public class Classified extends PredictionInstance {

    protected int predictedClass;
    protected String predictedClassName;

    public Classified(int predictedClass, String predictedClassName, String predictedBy, ArrayList<EvaluationMetric> evaluations, double evaluationMetricResult) {
        super(predictedBy,evaluations,evaluationMetricResult);
        this.predictedClass = predictedClass;
        this.predictedClassName = predictedClassName;


    }
    public Classified(int predictedClass, String predictedClassName, String predictedBy, ArrayList<EvaluationMetric> evaluations) {
        super(predictedBy,evaluations);
        this.predictedClass = predictedClass;
        this.predictedClassName = predictedClassName;





    }
    public Classified() {
        super();
        this.predictedClass = -1;
        this.predictedClassName = "";

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
    @Override
    public String toString(){
        return "\n(R) Prediction> prediction (index, name):("+predictedClass+", "+predictedClassName+") accepted: "+acceptedPrediction+" evaluationRate: "+ certaintyDegree;
    }

}
