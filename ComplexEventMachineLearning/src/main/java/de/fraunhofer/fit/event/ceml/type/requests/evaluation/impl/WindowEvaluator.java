package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.EvaluatorBase;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.ClassificationEvaluationValue;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.ClassEvaluationMetricBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.*;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by angel on 1/12/15.
 */
public class WindowEvaluator extends GenericEvaluator<Integer> implements Evaluator<Integer>{

    private long totalFalsePositives = 0;
    private long totalFalseNegatives = 0;
    private long totalTruePositives = 0;
    private long totalTrueNegatives = 0;
   // private long samples = 0;


    private double[][] confusionMatrix ;
    private ArrayList<String> classes;

    private  long[][] sequentialConfusionMatrix;


    public WindowEvaluator(Collection<String> namesClasses, ArrayList<TargetRequest> targets){
        super( targets);
        classes =new ArrayList<>(namesClasses);
    }

    @Override
    public double evaluate(Integer predicted,Integer actual){
        confusionMatrix[actual][predicted]++;

            for (int i = 0; i < classes.size(); i++) {
                if (i == actual && actual.equals( predicted)) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]++;
                    totalTruePositives++;
                } else if (i != actual && i == predicted) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]++;
                    totalFalsePositives++;
                } else if (i == actual && i != predicted) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]++;
                    totalFalseNegatives++;
                } else if (i != actual && i != predicted) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]++;
                    totalTrueNegatives++;
                }



        }

        return calculateEvaluationMetrics(actual);



    }


    protected double calculateEvaluationMetrics(int evaluatedClass){
        double accumulateMetric =0;
        int i=0;
        for(EvaluationMetric algorithm: evaluationAlgorithms.values()) {

            if (algorithm instanceof ModelEvaluationMetric)
                ((ModelEvaluationMetric) algorithm).calculate();
            else if (algorithm instanceof ClassEvaluationMetric)
                ((ClassEvaluationMetric) algorithm).calculate(evaluatedClass);
            else
                loggerService.error("Evaluation algorithm " + algorithm.getClass().getName() + " is an instance of an unknown algorithm class");

            if(!algorithm.isControlMetric()) {
                accumulateMetric += algorithm.getNormalizedResult();
                i++;
            }

        }
        return accumulateMetric/(evaluationAlgorithms.size()-i);

    }






    public boolean readyToSlide(){
        return  evaluationAlgorithms.get(SlideAfter.class.getSimpleName()).isReady();
    }


    protected void reset(){
        for(int i=0; i<classes.size();i++) {

            sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
        }
       totalFalsePositives = 0;
       totalFalseNegatives = 0;
       totalTruePositives = 0;
       totalTrueNegatives = 0;


        evaluationAlgorithms.values().forEach(eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric::reset);

        //evaluationAlgorithms.get(SlideAfter.class.getSimpleName()).reset();

    }



   // @SuppressWarnings("unchecked")
    @Override
    public WindowEvaluator build() throws Exception {
        //classes = new ArrayList<>(namesClasses);

        if(classes==null|| classes.isEmpty())
            throw new Exception("Classes is a mandatory field for WindowEvaluator");

        confusionMatrix= new double[classes.size()][classes.size()];
        sequentialConfusionMatrix = new long[classes.size()][4];
        for(int i=0; i<classes.size();i++) {

            sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
        }

        super.build();
        


        return this;
    }

    public abstract class ModelEvaluationMetricSubBase extends ModelEvaluationMetricBase implements ModelEvaluationAlgorithmExtended {

        public ModelEvaluationMetricSubBase(EvaluationMetric.ComparisonMethod method, double target){
            super(method,target);

        }

        @Override
        public long getTotalFalsePositives() {
            return WindowEvaluator.this.totalFalsePositives;
        }

        @Override
        public void setTotalFalsePositives(long totalFalsePositives) {
            WindowEvaluator.this.totalFalsePositives = totalFalsePositives;
        }

        @Override
        public long getTotalFalseNegatives() {
            return  WindowEvaluator.this.totalFalseNegatives;
        }

        @Override
        public void setTotalFalseNegatives(long totalFalseNegatives) {
            WindowEvaluator.this.totalFalseNegatives = totalFalseNegatives;
        }

        @Override
        public long getTotalTruePositives() {
            return  WindowEvaluator.this.totalTruePositives;
        }

        @Override
        public void setTotalTruePositives(long totalTruePositives) {
            WindowEvaluator.this.totalTruePositives = totalTruePositives;
        }

        @Override
        public long getTotalTrueNegatives() {
            return  WindowEvaluator.this.totalTrueNegatives;
        }

        @Override
        public void setTotalTrueNegatives(long totalTrueNegatives) {
            WindowEvaluator.this.totalTrueNegatives = totalTrueNegatives;
        }

        @Override
        public double[][] getConfusionMatrix() {
            return  WindowEvaluator.this.confusionMatrix;
        }

        @Override
        public void setConfusionMatrix(double[][] sequentialConfusionMatrix) {
            WindowEvaluator.this.confusionMatrix = sequentialConfusionMatrix;
        }

    }
    public abstract class ClassEvaluationMetricSubBase extends ClassEvaluationMetricBase<Double> implements ModelEvaluationAlgorithmExtended, ClassEvaluationAlgorithmExtended {

        public ClassEvaluationMetricSubBase(EvaluationMetric.ComparisonMethod method, Double[] targets){
            super(method,targets);

        }

        @Override
        public long getTotalFalsePositives() {
            return WindowEvaluator.this.totalFalsePositives;
        }

        @Override
        public void setTotalFalsePositives(long totalFalsePositives) {
            WindowEvaluator.this.totalFalsePositives = totalFalsePositives;
        }

        @Override
        public long getTotalFalseNegatives() {
            return  WindowEvaluator.this.totalFalseNegatives;
        }

        @Override
        public void setTotalFalseNegatives(long totalFalseNegatives) {
            WindowEvaluator.this.totalFalseNegatives = totalFalseNegatives;
        }

        @Override
        public long getTotalTruePositives() {
            return  WindowEvaluator.this.totalTruePositives;
        }

        @Override
        public void setTotalTruePositives(long totalTruePositives) {
            WindowEvaluator.this.totalTruePositives = totalTruePositives;
        }

        @Override
        public long getTotalTrueNegatives() {
            return  WindowEvaluator.this.totalTrueNegatives;
        }

        @Override
        public void setTotalTrueNegatives(long totalTrueNegatives) {
            WindowEvaluator.this.totalTrueNegatives = totalTrueNegatives;
        }

        @Override
        public double[][] getConfusionMatrix() {
            return  WindowEvaluator.this.confusionMatrix;
        }

        @Override
        public void setConfusionMatrix(double[][] sequentialConfusionMatrix) {
            WindowEvaluator.this.confusionMatrix = sequentialConfusionMatrix;
        }

        @Override
        public long getFalsePositives(int classIndex) {
            return (long) sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.falsePositives.ordinal()];
        }

        @Override
        public void setFalsePositives(int classIndex, long FalsePositives) {
             sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.falsePositives.ordinal()]= FalsePositives;
        }

        @Override
        public long getFalseNegatives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.falseNegatives.ordinal()];
        }

        @Override
        public void setFalseNegatives(int classIndex, long FalseNegatives) {
           sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.falseNegatives.ordinal()] = FalseNegatives;
        }

        @Override
        public long getTruePositives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.truePositives.ordinal()];
        }

        @Override
        public void setTruePositives(int classIndex, long TruePositives) {
            sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.truePositives.ordinal()] = TruePositives;
        }

        @Override
        public long getTrueNegatives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.trueNegatives.ordinal()];
        }

        @Override
        public void setTrueNegatives(int classIndex, long TrueNegatives) {
            sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.trueNegatives.ordinal()]= TrueNegatives;
        }
    }

    public class Accuracy extends ModelEvaluationMetricSubBase {
        public Accuracy(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            double denominator = (totalTrueNegatives + totalTruePositives + totalFalseNegatives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = new Double((double) (totalTrueNegatives + totalTruePositives) / denominator));
            return 0.0;

        }
    }

     public class ClassPrecision extends ClassEvaluationMetricSubBase {

        public ClassPrecision(ComparisonMethod method, Double[] target) {
            super(method, target);
        }

        @Override
        public Double calculate(int classIndex) {
            long denominator = (long) (sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.falsePositives.ordinal()]);
            if (denominator > 0)
                return (currentValue[classIndex]= ((double) sequentialConfusionMatrix[classIndex][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
    }
    public class Precision extends ModelEvaluationMetricSubBase {

        public Precision(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            long denominator = (totalTruePositives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = ((double) totalTruePositives) / denominator);
            return 0.0;

        }
    }
    public class Sensitivity extends ModelEvaluationMetricSubBase {

        public Sensitivity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            long denominator = (totalTruePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue = new Double(((double) totalTruePositives) / denominator));
            return 0.0;

        }
    }

    public class ClassSensitivity extends ClassEvaluationMetricSubBase {

        public ClassSensitivity(ComparisonMethod method, Double[] target) {
            super(method, target);
        }

        @Override
        public Double calculate(int indexClass) {
            long denominator = (((long)sequentialConfusionMatrix[indexClass][ClassificationEvaluationValue.truePositives.ordinal()]) + (long)sequentialConfusionMatrix[indexClass][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue[indexClass] = ((double)sequentialConfusionMatrix[indexClass][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
    }
    public class Recall extends Sensitivity {

        public Recall(ComparisonMethod method, double target) {
            super(method, target);
        }

    }
    public class ClassRecall extends ClassSensitivity {

        public ClassRecall(ComparisonMethod method, Double[] target) {
            super(method, target);
        }

    }
    public class Specificity extends ModelEvaluationMetricSubBase {

        public Specificity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {

            long denominator = (totalFalsePositives + totalTrueNegatives);
            if (denominator > 0)
                return (currentValue = ((double) totalTrueNegatives) / denominator);
            return 0.0;

        }
    }

    public class NegativePredictiveValue extends ModelEvaluationMetricSubBase {

        public NegativePredictiveValue(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {


            long denominator = (totalTrueNegatives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue= ((double) totalTrueNegatives));
            return 0.0;

        }
    }
    public class FallOut extends ModelEvaluationMetricSubBase {

        public FallOut(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            Double denominator = (double)(totalFalsePositives + totalTrueNegatives);
            if (denominator > 0)
                return (currentValue = totalFalsePositives/denominator);
            return 0.0;
        }
    }
    public class FalseDiscoveryRate extends ModelEvaluationMetricSubBase {

        public FalseDiscoveryRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            Double denominator = (double)(totalFalsePositives + totalTruePositives);
            if (denominator > 0)
                return ( currentValue= 1.0 - totalFalsePositives);
            return 0.0;
        }
    }
    public class MissRate extends ModelEvaluationMetricSubBase {

        public MissRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            long denominator = (totalFalseNegatives + totalTruePositives);
            if (denominator > 0)
                return ( currentValue= ((double) totalFalseNegatives) / denominator);
            return 0.0;
        }
    }
    public class F1Score extends ModelEvaluationMetricSubBase {

        public F1Score(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            double denominator = (( totalTruePositives *2.0) + totalFalsePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue= ( totalTruePositives *2.0) / denominator);
            return 0.0;
        }
    }

    public class ClassWeightedF1Score extends ClassEvaluationMetricSubBase {

        protected ClassPrecision precision;
        protected ClassRecall recall;
        protected Integer[] weight;
        protected int samples=0;
        public ClassWeightedF1Score(ComparisonMethod method, Double[] target) {
            super(method, target);
            precision = new ClassPrecision(method,target);
            recall = new ClassRecall(method,target);
            weight = new Integer[target.length];
        }


        @Override
        public Double calculate(int classIndex) {
            double pre = precision.calculate(classIndex), re=recall.calculate(classIndex);

            weight[classIndex]++;
            samples++;

            return currentValue[classIndex]=(weight[classIndex]/samples)*2.0*((pre*re)/(pre+re));

        }


    }
    public class MatthewsCorrelationCoefficient extends ModelEvaluationMetricSubBase {

        public MatthewsCorrelationCoefficient(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            double denominator = Math.sqrt((totalTruePositives + totalFalsePositives)*(totalTruePositives + totalFalseNegatives)*(totalTrueNegatives + totalFalsePositives)*(totalTrueNegatives + totalFalseNegatives));
            if (denominator > 0)
                return ( currentValue= ((double) (totalTrueNegatives * totalTruePositives)-(totalFalsePositives * totalFalseNegatives)) / denominator);

            return 0.0;
        }
    }
    public class Informedness extends ModelEvaluationMetricSubBase {

        public Informedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            return (currentValue = sensitivity() + specificity() - 1);

        }
        private double sensitivity() {
            long denominator = (totalTruePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue = ((double) totalTruePositives) / denominator);
            return 0;

        }
        private double specificity() {

            long denominator = (totalFalsePositives + totalTrueNegatives);
            if (denominator > 0)
                return (currentValue = ((double) totalTrueNegatives) / denominator);
            return 0;

        }
    }
    public class Markedness extends ModelEvaluationMetricSubBase {

        public Markedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            return (currentValue = precision() + negativePredictiveValue() - 1);
        }
        private double precision() {
            long denominator = (totalTruePositives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = ((double) totalTruePositives) / denominator);
            return 0;

        }
        private double negativePredictiveValue() {


            long denominator = (totalTrueNegatives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue= ((double) totalTrueNegatives));
            return 0;

        }
    }




/* TODO: transform all basic metrics into algorithms
    public class TotalTruePositives   extends EvaluationAlgorithmBase<Long> {

        public TotalTruePositives(ComparisonMethod method, long target){
            super(method, target);
            currentValue = totalTruePositives;

        }

        @Override
        public boolean isReady() {
            currentValue = totalTruePositives;
            switch (method){

                case Equal:
                    return currentValue.compareTo(target) == 0;
                case More:
                    return currentValue.compareTo(target) > 0;
                case MoreEqual:
                    return currentValue.compareTo(target) >= 0;
                case Less:
                    return currentValue.compareTo(target) < 0;
                case LessEqual:
                    return currentValue.compareTo(target) <= 0;
            }
            return false;
        }

        @Override
        public Long getResult() {
            currentValue = totalTruePositives;

            return currentValue;
        }
    }
    public class ClassTruePositives   extends ClassEvaluationAlgorithmBase<Long> {

        public ClassTruePositives(ComparisonMethod method, Long[] target){
            super(method, target);

        }

        @Override
        public Long calculate(int classIndex) {
            return sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()];
        }

        @Override
        public boolean isClassReady(int i) {
            switch (method){

                case Equal:
                    return sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]==target[i];
                case More:
                    return sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]>target[i];
                case MoreEqual:
                    return sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]>=target[i];
                case Less:
                    return sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]<target[i];
                case LessEqual:
                    return sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]<=target[i];
            }
            return false;
        }


        @Override
        public boolean isReady() {
            boolean ready = true;
            for(int i=0; i<target.length &&ready;i++) {

                Long objectCmp = sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()];
                switch (method) {

                    case Equal:
                        ready =objectCmp.compareTo(target[i]) == 0;
                        break;
                    case More:
                        ready = objectCmp.compareTo(target[i]) < 0;
                        break;
                    case MoreEqual:
                        ready = objectCmp.compareTo(target[i]) <= 0;
                        break;
                    case Less:
                        ready = objectCmp.compareTo(target[i]) > 0;
                        break;
                    case LessEqual:
                        ready = objectCmp.compareTo(target[i]) >= 0;
                }

            }

            return ready;
        }
        @Override
        public Long getClassResult(int classIndex) {
            return sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()];
        }

        protected Long[][] transposeMatrix(long [][] m){
            Long[][] temp = new Long[m[0].length][m.length];
            for (int i = 0; i < m.length; i++)
                for (int j = 0; j < m[0].length; j++)
                    temp[j][i] = m[i][j];
            return temp;
        }
        @Override
        public Long[] getResult() {

            return transposeMatrix(sequentialConfusionMatrix)[EvaluationMetrics.truePositives.ordinal()];
        }

    }

*/

}