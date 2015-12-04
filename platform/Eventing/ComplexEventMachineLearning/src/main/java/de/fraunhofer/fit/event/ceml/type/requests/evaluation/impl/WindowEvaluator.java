package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.builded.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.builded.Samples;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithmExtended;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by angel on 1/12/15.
 */
public class WindowEvaluator implements Evaluator {
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(WindowEvaluator.class);
    private long totalFalsePositives = 0;
    private long totalFalseNegatives = 0;
    private long totalTruePositives = 0;
    private long totalTrueNegatives = 0;
    private long samples = 0;

    private double[][] confusionMatrix ;
    private ArrayList<String> classes;

    private  double[][] sequentialConfusionMatrix;

    protected Map<String,EvaluationAlgorithm> evaluationAlgorithms = new HashMap<>();


    protected ArrayList<TargetRequest> targets;


    public WindowEvaluator(Collection<String> namesClasses, ArrayList<TargetRequest> targets){
        this.targets=targets;
        build(namesClasses);


    }

    @Override
    public boolean evaluate(int predicted, int actual){
        confusionMatrix[actual][predicted]++;
        sample();
            for (int i = 0; i < classes.size(); i++) {
                if (i == actual && actual == predicted) {
                    sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]++;
                    totalTruePositives++;
                } else if (i != actual && i == predicted) {
                    sequentialConfusionMatrix[i][EvaluationMetrics.falsePositives.ordinal()]++;
                    totalFalsePositives++;
                } else if (i == actual && i != predicted) {
                    sequentialConfusionMatrix[i][EvaluationMetrics.falseNegatives.ordinal()]++;
                    totalFalseNegatives++;
                } else if (i != actual && i != predicted) {
                    sequentialConfusionMatrix[i][EvaluationMetrics.trueNegatives.ordinal()]++;
                    totalTrueNegatives++;
                }


            calculateEvaluationMetrics();

            return isDeployable();
        }
        return false;



    }
    protected void calculateEvaluationMetrics(){
        for(EvaluationAlgorithm algorithm: evaluationAlgorithms.values())
            algorithm.calculate();


    }

    public boolean isDeployable(){

        for(EvaluationAlgorithm algorithm: evaluationAlgorithms.values())
            if(!algorithm.isReady())
                return false;
        return true;


    }
    public boolean readyToSlide(){
        return  evaluationAlgorithms.get(Samples.class.getSimpleName()).isReady();
    }


    protected void reset(){
        for(int i=0; i<classes.size();i++) {

            sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]  = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.trueNegatives.ordinal()]  = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falsePositives.ordinal()] = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falseNegatives.ordinal()] = 0.0;
        }
       totalFalsePositives = 0;
       totalFalseNegatives = 0;
       totalTruePositives = 0;
       totalTrueNegatives = 0;
       samples =0;

    }


    public long getSamples() {
        return samples;
    }

    public long sample() {
        return samples += 1;
    }
    @SuppressWarnings("unchecked")
    @Override
    public void build(Collection<String> namesClasses){
        classes = new ArrayList<>(namesClasses);

        confusionMatrix= new double[classes.size()][classes.size()];
        sequentialConfusionMatrix = new double[classes.size()][4];
        for(int i=0; i<classes.size();i++) {

            sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]  = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.trueNegatives.ordinal()]  = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falsePositives.ordinal()] = 0.0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falseNegatives.ordinal()] = 0.0;
        }
        for(TargetRequest target:targets){
            String algorithm = WindowEvaluator.class.getCanonicalName()+"$"+target.getName();

            evaluationAlgorithms.put(
                    target.getName(),
                    instanceEvaluationAlgorithm(algorithm,target.getMethod(),target.getThreshold())
            );


        }
    }
     public EvaluationAlgorithm instanceEvaluationAlgorithm(String canonicalName, String method, double target)  {

        try {
            Class clazz = Class.forName(canonicalName);

            Constructor constructor = null;


            EvaluationAlgorithm.ComparisonMethod methodParameter = EvaluationAlgorithm.ComparisonMethod.More;
            if(method.trim().toLowerCase().equals("equal")){
                methodParameter = EvaluationAlgorithm.ComparisonMethod.Equal;

            } else if(method.trim().toLowerCase().contains("smaller")|| method.trim().toLowerCase().contains("less")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter = EvaluationAlgorithm.ComparisonMethod.LessEqual;
                }else
                    methodParameter = EvaluationAlgorithm.ComparisonMethod.Less;
            } else if(method.trim().toLowerCase().contains("bigger")|| method.trim().toLowerCase().contains("more")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter = EvaluationAlgorithm.ComparisonMethod.MoreEqual;
                }
            }

            constructor = clazz.getConstructor(WindowEvaluator.class,EvaluationAlgorithm.ComparisonMethod.class, double.class);


            return  (EvaluationAlgorithm) constructor.newInstance(this,methodParameter,target);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }
    public void setTargets(ArrayList<TargetRequest> targets) {
        this.targets = targets;
    }

    public abstract class EvaluationAlgorithmSubBase  extends EvaluationAlgorithmBase  implements EvaluationAlgorithmExtended{

        public EvaluationAlgorithmSubBase(EvaluationAlgorithm.ComparisonMethod method, double target){
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
            return  WindowEvaluator.this.sequentialConfusionMatrix;
        }

        @Override
        public void setConfusionMatrix(double[][] sequentialConfusionMatrix) {
            WindowEvaluator.this.sequentialConfusionMatrix = sequentialConfusionMatrix;
        }


    }

    public class Accuracy extends EvaluationAlgorithmSubBase {
        public Accuracy(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            double denominator = (totalTrueNegatives + totalTruePositives + totalFalseNegatives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = ((double) (totalTrueNegatives + totalTruePositives)) / denominator);
            return 0;

        }
    }
    public class Precision extends EvaluationAlgorithmSubBase {

        public Precision(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            long denominator = (totalTruePositives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = ((double) totalTruePositives) / denominator);
            return 0;

        }
    }
    public class Sensitivity extends EvaluationAlgorithmSubBase {

        public Sensitivity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            long denominator = (totalTruePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue = ((double) totalTruePositives) / denominator);
            return 0;

        }
    }
    public class Specificity extends EvaluationAlgorithmSubBase {

        public Specificity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {

            long denominator = (totalFalsePositives + totalTrueNegatives);
            if (denominator > 0)
                return (currentValue = ((double) totalTrueNegatives) / denominator);
            return 0;

        }
    }

    public class NegativePredictiveValue extends EvaluationAlgorithmSubBase {

        public NegativePredictiveValue(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {


            long denominator = (totalTrueNegatives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue= ((double) totalTrueNegatives));
            return 0;

        }
    }
    public class FallOut extends EvaluationAlgorithmSubBase {

        public FallOut(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            long denominator = (totalFalsePositives + totalTrueNegatives);
            if (denominator > 0)
                return (currentValue = totalFalsePositives/denominator);
            return 0;
        }
    }
    public class FalseDiscoveryRate extends EvaluationAlgorithmSubBase {

        public FalseDiscoveryRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            long denominator = (totalFalsePositives + totalTruePositives);
            if (denominator > 0)
                return ( currentValue= 1- totalFalsePositives);
            return 0;
        }
    }
    public class MissRate extends EvaluationAlgorithmSubBase {

        public MissRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            long denominator = (totalFalseNegatives + totalTruePositives);
            if (denominator > 0)
                return ( currentValue= ((double) totalFalseNegatives) / denominator);
            return 0;
        }
    }
    public class F1Score extends EvaluationAlgorithmSubBase {

        public F1Score(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            double denominator = (( totalTruePositives *2.0) + totalFalsePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue= ( totalTruePositives *2.0) / denominator);
            return 0;
        }
    }
    public class MatthewsCorrelationCoefficient extends EvaluationAlgorithmSubBase {

        public MatthewsCorrelationCoefficient(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            double denominator = Math.sqrt((totalTruePositives + totalFalsePositives)*(totalTruePositives + totalFalseNegatives)*(totalTrueNegatives + totalFalsePositives)*(totalTrueNegatives + totalFalseNegatives));
            if (denominator > 0)
                return ( currentValue= ((double) (totalTrueNegatives * totalTruePositives)-(totalFalsePositives * totalFalseNegatives)) / denominator);

            return 0;
        }
    }
    public class Informedness extends EvaluationAlgorithmSubBase {

        public Informedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
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
    public class Markedness extends EvaluationAlgorithmSubBase {

        public Markedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
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


    public class Samples extends de.fraunhofer.fit.event.ceml.type.requests.builded.Samples{

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
        }
    }

}