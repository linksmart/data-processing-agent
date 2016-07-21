package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.EvaluatorBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.EvaluationMetricBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;
import eu.linksmart.api.event.datafusion.JsonSerializable;

import java.util.*;

/**
 * Created by devasya on 7/20/2016.
 * For evaluating regression
 */
public class RegressionEvaluator extends GenericEvaluator<Double> implements Evaluator<Double> {

    private static final int MAX_NUMBER_FOR_AVG = 10000;
    LinkedList<Map.Entry<Double,Double>> fixedSizeList = new LinkedList<>();
    Map.Entry<Double,Double> latestEntry ;
    private double N = 0; //fading increment
    private int maxQueueSize = 200;

    public RegressionEvaluator(Collection<String> namesClasses, ArrayList<TargetRequest> targets) {
        super(namesClasses, targets);
    }


    private void addTofixedsizeList(LinkedList<Map.Entry<Double, Double>> list, Map.Entry<Double, Double> entry){
        if(list.size()== maxQueueSize){
            list.remove();//removes the first most element
        }
        list.add(entry);
    }


    @Override
    public double evaluate(Double predicted, Double actual) {
        latestEntry = new AbstractMap.SimpleEntry<>(predicted, actual);
        addTofixedsizeList(fixedSizeList,latestEntry );
        double accumulateMetric =0;
        int i=0;
        for(EvaluationMetric algorithm: evaluationAlgorithms.values()) {
            if(algorithm instanceof ModelEvaluationMetric){
                ((ModelEvaluationMetric) algorithm).calculate();
            }else{
                loggerService.error("Evaluation algorithm " + algorithm.getClass().getName() + " is not a ModelEvaluationMetric subclass");
            }
            if(!algorithm.isControlMetric()) {
                accumulateMetric += algorithm.getNormalizedResult();
                i++;
            }
        }
        return accumulateMetric/(evaluationAlgorithms.size()-i);
    }


    public class RMSEEvaluationMetric extends ModelEvaluationMetricBase{

        public RMSEEvaluationMetric(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            double predicted = latestEntry.getKey();
            double actual = latestEntry.getValue();
            double diff = actual-predicted;
            double squaredError = diff * diff;
            if(N != MAX_NUMBER_FOR_AVG){//ignore very old values
                N++;
            }
            double squaredRMSE = currentValue*currentValue;
            return (currentValue = Math.sqrt(((N-1)/N) * squaredRMSE  + squaredError/N));
        }


    }
}


