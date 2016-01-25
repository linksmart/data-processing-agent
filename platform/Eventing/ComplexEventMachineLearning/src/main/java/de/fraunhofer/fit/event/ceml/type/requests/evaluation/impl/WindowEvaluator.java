package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.EvaluatorBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.*;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.ClassEvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.ModelEvaluationAlgorithmBase;
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
public class WindowEvaluator extends EvaluatorBase implements Evaluator{
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(WindowEvaluator.class);
    private long totalFalsePositives = 0;
    private long totalFalseNegatives = 0;
    private long totalTruePositives = 0;
    private long totalTrueNegatives = 0;
   // private long samples = 0;

    EvaluationAlgorithm samples;
    private double[][] confusionMatrix ;
    private ArrayList<String> classes;

    private  long[][] sequentialConfusionMatrix;

    protected Map<String,EvaluationAlgorithm> evaluationAlgorithms = new HashMap<>();


    protected ArrayList<TargetRequest> targets;


    public WindowEvaluator(Collection<String> namesClasses, ArrayList<TargetRequest> targets){
        this.targets=targets;
        build(namesClasses);


    }

    @Override
    public boolean evaluate(int predicted, int actual){
        confusionMatrix[actual][predicted]++;

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


            calculateEvaluationMetrics(actual);

            return isDeployable();
        }
        return false;



    }
    protected void calculateEvaluationMetrics(int classIndex){
        for(EvaluationAlgorithm algorithm: evaluationAlgorithms.values())
            if(algorithm instanceof ModelEvaluationAlgorithm)
                ((ModelEvaluationAlgorithm)algorithm).calculate();
            else if(algorithm instanceof ClassEvaluationAlgorithm)
                ((ClassEvaluationAlgorithm)algorithm).calculate(classIndex);
            else
                loggerService.error("Evaluation algorithm "+algorithm.getClass().getName()+" is an instance of an unknown algorithm class");


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

            sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.trueNegatives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falsePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falseNegatives.ordinal()] = 0;
        }
       totalFalsePositives = 0;
       totalFalseNegatives = 0;
       totalTruePositives = 0;
       totalTrueNegatives = 0;


    }



    @SuppressWarnings("unchecked")
    @Override
    public void build(Collection<String> namesClasses){
        classes = new ArrayList<>(namesClasses);

        confusionMatrix= new double[classes.size()][classes.size()];
        sequentialConfusionMatrix = new long[classes.size()][4];
        for(int i=0; i<classes.size();i++) {

            sequentialConfusionMatrix[i][EvaluationMetrics.truePositives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.trueNegatives.ordinal()]  = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falsePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][EvaluationMetrics.falseNegatives.ordinal()] = 0;
        }
        for(TargetRequest target:targets){
            String algorithm = WindowEvaluator.class.getCanonicalName()+"$"+target.getName();

            evaluationAlgorithms.put(
                    target.getName(),
                    instanceEvaluationAlgorithm(algorithm,target.getMethod(),target.getThreshold())
            );


        }
        samples = evaluationAlgorithms.get(Samples.class.getSimpleName());
    }

    @Override
    public void reBuild(Evaluator evaluator) {
        if(evaluator instanceof DoubleTumbleWindowEvaluator){
            DoubleTumbleWindowEvaluator aux = (DoubleTumbleWindowEvaluator)evaluator;
            for(TargetRequest algorithm: aux.getTargets()) {
                evaluationAlgorithms.get(algorithm.getName()).reBuild(algorithm);
            }

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

    @Override
    public String report(){
        String report = "";
        for(EvaluationAlgorithm algorithm: evaluationAlgorithms.values()){
            report += algorithm.report()+" || ";
        }
        return report;
    }
    public abstract class ModelEvaluationAlgorithmSubBase extends ModelEvaluationAlgorithmBase implements ModelEvaluationAlgorithmExtended {

        public ModelEvaluationAlgorithmSubBase(EvaluationAlgorithm.ComparisonMethod method, double target){
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
    public abstract class ClassEvaluationAlgorithmSubBase extends ClassEvaluationAlgorithmBase<Double>  implements ModelEvaluationAlgorithmExtended, ClassEvaluationAlgorithmExtended {

        public ClassEvaluationAlgorithmSubBase(EvaluationAlgorithm.ComparisonMethod method, Double[] targets){
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
            return (long) sequentialConfusionMatrix[classIndex][EvaluationMetrics.falsePositives.ordinal()];
        }

        @Override
        public void setFalsePositives(int classIndex, long FalsePositives) {
             sequentialConfusionMatrix[classIndex][EvaluationMetrics.falsePositives.ordinal()]= FalsePositives;
        }

        @Override
        public long getFalseNegatives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][EvaluationMetrics.falseNegatives.ordinal()];
        }

        @Override
        public void setFalseNegatives(int classIndex, long FalseNegatives) {
           sequentialConfusionMatrix[classIndex][EvaluationMetrics.falseNegatives.ordinal()] = FalseNegatives;
        }

        @Override
        public long getTruePositives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()];
        }

        @Override
        public void setTruePositives(int classIndex, long TruePositives) {
            sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()] = TruePositives;
        }

        @Override
        public long getTrueNegatives(int classIndex) {
            return  (long) sequentialConfusionMatrix[classIndex][EvaluationMetrics.trueNegatives.ordinal()];
        }

        @Override
        public void setTrueNegatives(int classIndex, long TrueNegatives) {
            sequentialConfusionMatrix[classIndex][EvaluationMetrics.trueNegatives.ordinal()]= TrueNegatives;
        }
    }

    public class Accuracy extends ModelEvaluationAlgorithmSubBase {
        public Accuracy(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            double denominator = (totalTrueNegatives + totalTruePositives + totalFalseNegatives + totalFalsePositives);
            if (denominator > 0)
                return (currentValue = ((double) (totalTrueNegatives + totalTruePositives)) / denominator);
            return 0.0;

        }
    }
    public class ClassPrecision extends ClassEvaluationAlgorithmSubBase{

        public ClassPrecision(ComparisonMethod method, Double[] target) {
            super(method, target);
        }

        @Override
        public Double calculate(int classIndex) {
            long denominator = (long) (sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()] + sequentialConfusionMatrix[classIndex][EvaluationMetrics.falsePositives.ordinal()]);
            if (denominator > 0)
                return (currentValue[classIndex]= ((double) sequentialConfusionMatrix[classIndex][EvaluationMetrics.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
    }
    public class Precision extends ModelEvaluationAlgorithmSubBase {

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
    public class Sensitivity extends ModelEvaluationAlgorithmSubBase {

        public Sensitivity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            long denominator = (totalTruePositives + totalFalseNegatives);
            if (denominator > 0)
                return ( currentValue = ((double) totalTruePositives) / denominator);
            return 0.0;

        }
    }

    public class ClassSensitivity extends ClassEvaluationAlgorithmSubBase {

        public ClassSensitivity(ComparisonMethod method, Double[] target) {
            super(method, target);
        }

        @Override
        public Double calculate(int indexClass) {
            long denominator = (((long)sequentialConfusionMatrix[indexClass][EvaluationMetrics.truePositives.ordinal()]) + (long)sequentialConfusionMatrix[indexClass][EvaluationMetrics.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue[indexClass] = ((double)sequentialConfusionMatrix[indexClass][EvaluationMetrics.truePositives.ordinal()]) / denominator);
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
    public class Specificity extends ModelEvaluationAlgorithmSubBase {

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

    public class NegativePredictiveValue extends ModelEvaluationAlgorithmSubBase {

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
    public class FallOut extends ModelEvaluationAlgorithmSubBase {

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
    public class FalseDiscoveryRate extends ModelEvaluationAlgorithmSubBase {

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
    public class MissRate extends ModelEvaluationAlgorithmSubBase {

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
    public class F1Score extends ModelEvaluationAlgorithmSubBase {

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

    public class ClassWeightedF1Score extends ClassEvaluationAlgorithmSubBase  {

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
    public class MatthewsCorrelationCoefficient extends ModelEvaluationAlgorithmSubBase {

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
    public class Informedness extends ModelEvaluationAlgorithmSubBase {

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
    public class Markedness extends ModelEvaluationAlgorithmSubBase {

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


    public class Samples extends de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.Samples {

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
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