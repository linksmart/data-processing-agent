package eu.linksmart.services.event.ceml.evaluation.evaluators;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.base.GenericEvaluator;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;

import java.util.*;

/**
 * Created by devasya on 7/20/2016.
 * For evaluating regression
 */
public class RegressionEvaluator extends GenericEvaluator<Collection<Number>> {


    LinkedList<Map.Entry<Number,Number>> fixedSizeList = new LinkedList<>();
    List<Map.Entry<Number,Number>> latestEntries = new LinkedList<>();

    private int maxQueueSize = 200;

    public RegressionEvaluator( List<TargetRequest> targets) {
        super( targets);
    }

    @Override
    public RegressionEvaluator build() throws UntraceableException, TraceableException {
         super.build();

        return this;
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }


    private void addToFixedSizeList(LinkedList<Map.Entry<Number, Number>> list, Map.Entry<Number, Number> entry){
        if(list.size()== maxQueueSize){
            list.remove();//removes the first most element
        }
        list.add(entry);
    }


    @Override
    public double evaluate(Collection<Number> predicted, Collection<Number>  actual) { // O(n*m) -> O(n^2)
        latestEntries.clear();

        Iterator<Number> iteratorPredicted = predicted.iterator();
        for (int i=0; i<actual.size();i++) { // O(n)
            List<Number> list = new ArrayList<>(actual);
            Number actualEntry = list.get(i);
            Number predictedEntry = iteratorPredicted.next();
            if(actualEntry.equals(Double.NaN) || predictedEntry.equals(Double.NaN))
                continue;

            Map.Entry<Number,Number> entry = new AbstractMap.SimpleEntry<>(predictedEntry, actualEntry);
            latestEntries.add(entry);
            addToFixedSizeList(fixedSizeList, entry);

        }

        double accumulateMetric =0;
        int i=0;
        for(EvaluationMetric algorithm: evaluationAlgorithms.values()) { // O(n*m) -> O(n^2)
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
        return accumulateMetric/(i);
    }


    public class RMSE extends ModelEvaluationMetricBase{
        private static final int MAX_NUMBER_FOR_AVG = 10000;
        private long N = 0; //fading increment
        public RMSE(ComparisonMethod method, Double target) {
            super(ComparisonMethod.Less, target);
            currentValue = 100.0;
        }

        @Override
        public Double calculate() {
            if(!(latestEntries.size() >0))
                return currentValue;

            double squaredRMSE = currentValue*currentValue;
            double squaredSum = 0.0;
            for(Map.Entry entry:latestEntries){
//                Map.Entry entry = latestEntries.get(latestEntries.size()-1);
                Double predicted = ((Number) entry.getKey()).doubleValue();
                Double actual = ((Number) entry.getValue()).doubleValue();
                double diff =  actual-predicted;
                squaredSum += diff * diff;
            }
            double squaredError = squaredSum/latestEntries.size();

            if(N != MAX_NUMBER_FOR_AVG){//ignore very old values
                N++;
            }

            squaredRMSE =((N-1) * squaredRMSE  + squaredError)/N;
            currentValue = Math.sqrt(squaredRMSE);
            return  currentValue;
        }


    }

    public class MAE extends ModelEvaluationMetricBase{
        private static final int MAX_NUMBER_FOR_AVG = 10000;
        private long N = 0; //fading increment
        public MAE(ComparisonMethod method, Double target) {
            super(ComparisonMethod.Less, target);
            currentValue = 100.0;
        }

        @Override
        public Double calculate() {
            if(!(latestEntries.size() >0))
                return currentValue;

            double absErrorSum = 0.0;
            for(Map.Entry entry:latestEntries){
                Double predicted = ((Number) entry.getKey()).doubleValue();
                Double actual = ((Number) entry.getValue()).doubleValue();
                double diff =  actual-predicted;
                absErrorSum += Math.abs(diff);

            }
            double absError = absErrorSum/latestEntries.size();
            if(N != MAX_NUMBER_FOR_AVG){//ignore very old values
                N++;
            }

            currentValue = ((N-1) * currentValue  + absError)/N;
            return  currentValue;
        }


    }

    /*
    Akaike information criterion: Statistical model for finding goodness of a fit.
    Overal Picture: https://en.wikipedia.org/wiki/Akaike_information_criterion
    More can be found here :http://www.ijcaonline.org/journal/number5/pxc387242.pdf
     */
    public class AICc extends ModelEvaluationMetricBase{
        private static final int DAYS_A_WEEK = 7 ;
        private static final int HOURS_A_DAY =24;
        private long N = 0; //fading increment
        double avgResidualSquare =0;

        int prev = (Integer) parameters.get("prev");
        int prevSeasonal=  (Integer) parameters.get("prevSeasonal");
        int numHidden= (Integer) parameters.get("numHiddenNodes");

        int freeParamCount =((prev+prevSeasonal*24)*numHidden+ numHidden* HOURS_A_DAY)*DAYS_A_WEEK;

        public AICc(ComparisonMethod method, Double target) {
            super(ComparisonMethod.Less, target);
            avgResidualSquare = 0;
        }

        @Override
        public Double calculate() {
            if(!(latestEntries.size() >0))
                return currentValue;

            for(Map.Entry entry:latestEntries){
                Double predicted = (Double) entry.getKey();
                Double actual = (Double) entry.getValue();
                double diff =  actual-predicted;
                double  squaredError= diff*diff;
                N++;

                avgResidualSquare = ((N-1) * avgResidualSquare + squaredError)/N;

            }
            double AIC = N * Math.log(avgResidualSquare)+ 2 * freeParamCount ;
            currentValue = AIC + (2 * freeParamCount*(freeParamCount+1))/(N-freeParamCount-1);//assign  AICc to currentValue
            return  currentValue;
        }


    }
}


