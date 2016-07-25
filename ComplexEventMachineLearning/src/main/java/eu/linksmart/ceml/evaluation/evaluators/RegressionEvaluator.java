package eu.linksmart.ceml.evaluation.evaluators;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.ceml.evaluation.metrics.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;

import java.util.*;

/**
 * Created by devasya on 7/20/2016.
 * For evaluating regression
 */
public class RegressionEvaluator extends GenericEvaluator< Collection<? extends  Number>>  {

    private static final int MAX_NUMBER_FOR_AVG = 10000;
    LinkedList<Map.Entry<Object,Object>> fixedSizeList = new LinkedList<>();
    List<Map.Entry<Object,Object>> latestEntries = new LinkedList<>();
    private double N = 0; //fading increment
    private int maxQueueSize = 200;

    public RegressionEvaluator( ArrayList<TargetRequest> targets) {
        super( targets);
    }

    @Override
    public RegressionEvaluator build() throws Exception {
         super.build();

        return this;
    }


    private void addTofixedsizeList(LinkedList<Map.Entry<Object, Object>> list, Map.Entry<Object, Object> entry){
        if(list.size()== maxQueueSize){
            list.remove();//removes the first most element
        }
        list.add(entry);
    }


    @Override
    public double evaluate(Collection predicted, Collection actual) {
        latestEntries.clear();

        Iterator iteratorPred = predicted.iterator();
        for (Object actualEntry : actual) {
            Object predEntry = iteratorPred.next();
            Map.Entry entry = new AbstractMap.SimpleEntry<>(predEntry, actualEntry);
            latestEntries.add(entry);
            addTofixedsizeList(fixedSizeList, entry);

        }

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
            double squaredRMSE = currentValue*currentValue;
            for(Map.Entry entry:latestEntries){
                Double predicted = (Double) entry.getKey();
                Double actual = (Double) entry.getValue();
                double diff =  actual-predicted;
                double squaredError = diff * diff;
                if(N != MAX_NUMBER_FOR_AVG){//ignore very old values
                    N++;
                }

                squaredRMSE = ((N-1)/N) * squaredRMSE  + squaredError/N;
            }
            currentValue = Math.sqrt(squaredRMSE);
            return  currentValue;
        }


    }

    public class MAEEvaluationMetric extends ModelEvaluationMetricBase{

        public MAEEvaluationMetric(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            for(Map.Entry entry:latestEntries){
                Double predicted = (Double) entry.getKey();
                Double actual = (Double) entry.getValue();
                double diff =  actual-predicted;
                double absError = Math.abs(diff);
                if(N != MAX_NUMBER_FOR_AVG){//ignore very old values
                    N++;
                }

                currentValue = ((N-1)/N) * currentValue  + absError/N;
            }
            return  currentValue;
        }


    }
}


