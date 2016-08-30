package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 28.07.2016 a researcher of Fraunhofer FIT.
 */
public class Classified<T> extends PredictionInstance<T> {

    protected String predictedClassName;

    public Classified(T prediction, Object originInput, String predictedClassName, String predictedBy, List<EvaluationMetric> evaluations) {
        super(prediction,originInput,predictedBy,evaluations);

        this.predictedClassName = predictedClassName;
    }
    public Classified() {
        super();
        this.predictedClassName = "";

    }


    public String getPredictedClassName() {
        return predictedClassName;
    }

    public void setPredictedClassName(String predictedClassName) {
        this.predictedClassName = predictedClassName;
    }
    @Override
    public String toString(){
        return "\n(R) Prediction> prediction (index, name):("+getPrediction().toString()+", "+predictedClassName+") accepted: "+acceptedPrediction+" evaluationRate: "+ certaintyDegree;
    }

}
