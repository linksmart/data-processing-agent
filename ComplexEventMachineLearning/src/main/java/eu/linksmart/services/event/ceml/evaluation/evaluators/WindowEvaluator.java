package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import eu.linksmart.api.event.ceml.evaluation.ClassificationEvaluationValue;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.base.GenericEvaluator;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.ClassEvaluationMetricBase;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.*;

import javax.management.modelmbean.ModelMBeanConstructorInfo;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by angel on 1/12/15.
 */
public class WindowEvaluator extends GenericEvaluator<Integer> implements Evaluator<Integer>{
   // private long samples = 0;

    @JsonProperty
    private  double[][] confusionMatrix ;
    @JsonProperty
    private  List<String> classes;
    @JsonProperty
    private  long[][] sequentialConfusionMatrix;


    public WindowEvaluator(Collection<String> namesClasses, List<TargetRequest> targets){
        super( targets);
        classes =new ArrayList<>(namesClasses);
    }

    @Override
    public double evaluate(Integer predicted,Integer actual){
        confusionMatrix[actual][predicted]++;

            for (int i = 0; i < classes.size(); i++) {
                if (actual.equals(i) && actual.equals( predicted)) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]++;
                } else if (!actual.equals(i) && predicted.equals(i)) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]++;
                } else if (actual.equals(i) && !predicted.equals(i)) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]++;
                } else if (!actual.equals(i) && !predicted.equals(i)) {
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]++;
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
        for(int i=0; i<classes.size();i++){
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
            }



        evaluationAlgorithms.values().forEach(eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric::reset);

        //evaluationAlgorithms.get(SlideAfter.class.getSimpleName()).reset();

    }



   // @SuppressWarnings("unchecked")
    @Override
    public WindowEvaluator build() throws TraceableException, UntraceableException {
        //classes = new ArrayList<>(namesClasses);
        try {

            if(classes==null|| classes.isEmpty())
                throw new StatementException(this.getClass().getName(),this.getClass().getCanonicalName(),"Classes is a mandatory field for WindowEvaluator");

            confusionMatrix= new double[classes.size()][classes.size()];
            sequentialConfusionMatrix = new long[classes.size()][4];
            for(int i=0; i<classes.size();i++){
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
            }
        }catch (Exception e){
            throw new UnknownUntraceableException(e.getMessage(),e);
        }


        super.build();
        


        return this;
    }
    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
    public abstract class ModelEvaluationMetricSubBase extends ModelEvaluationMetricBase  {

        public ModelEvaluationMetricSubBase(EvaluationMetric.ComparisonMethod method, double target){
            super(ComparisonMethod.More,target);

        }

        @Override
        public Double calculate() {
            double acc = 0;
            for(int i= 0; i<classes.size(); i++ )
                acc = calculate(i);

            return acc / (double)classes.size();

        }
    }


    /**
     *
     * Precision is a description of random errors, a measure of statistical variability.
     * see https://en.wikipedia.org/wiki/Accuracy_and_precision
     *
     */
    public class Accuracy extends ModelEvaluationMetricSubBase {
        public Accuracy(ComparisonMethod method, double target) {
            super(method, target);
        }



        @Override
        public Double calculate(int i) {

            double denominator = (
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] +
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] +
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]+
                    sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]
            );
            if (denominator > 0)
                return (currentValue = (double)
                        (sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()])
                        / denominator);
            return 0.0;
        }
    }
    /**
     * Alias of Accuracy
     * */
    public class ACC extends Accuracy {public ACC(ComparisonMethod method, double target) {super(method, target);}}
    /*
     *
 */

    /**
     *Accuracy has two definitions:
     *  1. more commonly, it is a description of systematic errors, a measure of statistical bias;
     *  2. alternatively, ISO defines accuracy as describing both types of observational error above (preferring the term trueness for the common definition of accuracy).
     *  https://en.wikipedia.org/wiki/Accuracy_and_precision
     *
     */
    public class Precision extends ModelEvaluationMetricSubBase {

        public Precision(ComparisonMethod method, double target) {
            super(method, target);
        }


        @Override
        public Double calculate(int i) {
            long denominator = ( sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]);
            if (denominator > 0)
                return (currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
    }
    /**
     * Alias of Precision
     * */
    public class PPV extends Precision{public PPV(ComparisonMethod method, double target) {super(method, target);}}
    /**
     * Sensitivity (also called the true positive rate, the recall, or probability of detection[1] in some fields) measures the proportion of positives that are correctly identified as such (e.g., the percentage of sick people who are correctly identified as having the condition).
     * see https://en.wikipedia.org/wiki/Sensitivity_and_specificity
     */
    public class Sensitivity extends ModelEvaluationMetricSubBase {

        public Sensitivity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }

    }
    /**
     * Alias of Sensitivity
     * */
    public class TPR extends Sensitivity {public TPR(ComparisonMethod method, double target) {super(method, target);}}
    /*
     *
     *
*/

    /**
     * Recall in information retrieval is the fraction of the documents that are relevant to the query that are successfully retrieved.
     * see https://en.wikipedia.org/wiki/Precision_and_recall#Recall
     */
    public class Recall extends Sensitivity {

        public Recall(ComparisonMethod method, double target) {
            super(method, target);
        }

    }
    /*
     *
     *
*/

    /**
     * Specificity (also called the true negative rate) measures the proportion of negatives that are correctly identified as such (e.g., the percentage of healthy people who are correctly identified as not having the condition).
     * see https://en.wikipedia.org/wiki/Sensitivity_and_specificity
     */
    public class Specificity extends ModelEvaluationMetricSubBase {

        public Specificity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {

            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] );
            if (denominator > 0)
                return (currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] ) / denominator);
            return 0.0;

        }
    }
    /**
     * Alias of Specificity
     * */
    public class TNR extends Specificity {public TNR(ComparisonMethod method, double target) {super(method, target);}}
    /**
     * The positive and negative predictive values (PPV and NPV respectively) are the proportions of positive and negative results in statistics and diagnostic tests that are true positive and true negative results, respectively.
     * The PPV and NPV describe the performance of a diagnostic test or other statistical measure
     *
     * https://en.wikipedia.org/wiki/Positive_and_negative_predictive_values
     *
     */
    public class NegativePredictiveValue extends ModelEvaluationMetricSubBase {

        public NegativePredictiveValue(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {

            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue= ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]));
            return 0.0;

        }
    }

    /**
     * Alias of NegativePredictiveValue
     * */
    public class NPV extends NegativePredictiveValue {public NPV(ComparisonMethod method, double target) {super(method, target);}}
    /**
     * Recall in information retrieval is the fraction of the documents that are relevant to the query that are successfully retrieved.
     *
     * see https://en.wikipedia.org/wiki/Precision_and_recall#Recall
     *
     */
    public class FallOut extends ModelEvaluationMetricSubBase {

        public FallOut(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            Double denominator = (double)(sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]);
            if (denominator > 0)
                return (currentValue = sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]/denominator);
            return 0.0;
        }
    }
    /**
     * Alias of FallOut
     * */
    public class FPR extends FallOut {public FPR(ComparisonMethod method, double target) {super(method, target);}}

    /**
     * The false discovery rate (FDR) is one way of conceptualizing the rate of type I errors in null hypothesis testing when conducting multiple comparisons.
     * see https://en.wikipedia.org/wiki/False_discovery_rate
     */
    public class FalseDiscoveryRate extends ModelEvaluationMetricSubBase {

        public FalseDiscoveryRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            Double denominator = (double)(sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]);
            if (denominator > 0)
                return ( currentValue= 1.0 - sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]);
            return 0.0;
        }
    }
    /**
     * Alias of FalseDiscoveryRate
     * */
    public class FDR extends FalseDiscoveryRate {public FDR(ComparisonMethod method, double target) {super(method, target);}}
    /**
     * In statistical hypothesis testing, a type I error is the incorrect rejection of a true null hypothesis (a "false positive"), while a type II error is incorrectly retaining a false null hypothesis (a "false negative").[1] More simply stated, a type I error is detecting an effect that is not present, while a type II error is failing to detect an effect that is present.
     * see https://en.wikipedia.org/wiki/Type_I_and_type_II_errors#False_positive_and_false_negative_rates
     */
    public class MissRate extends ModelEvaluationMetricSubBase {

        public MissRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]);
            if (denominator > 0)
                return ( currentValue= ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]) / denominator);
            return 0.0;
        }
    }
    /**
     * Alias of MissRate
     * */
    public class FNR extends MissRate {public FNR(ComparisonMethod method, double target) {super(method, target);}}
    /**
     * In statistical hypothesis testing, a type I error is the incorrect rejection of a true null hypothesis (a "false positive"), while a type II error is incorrectly retaining a false null hypothesis (a "false negative").[1] More simply stated, a type I error is detecting an effect that is not present, while a type II error is failing to detect an effect that is present.
     * see https://en.wikipedia.org/wiki/Type_I_and_type_II_errors#False_positive_and_false_negative_rates
     */
    public class F1Score extends ModelEvaluationMetricSubBase {

        public F1Score(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            double denominator = (( sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] *2.0) + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue= ( sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] *2.0) / denominator);
            return 0.0;
        }
    }

    /**
     *
     *
     */
    public class MatthewsCorrelationCoefficient extends ModelEvaluationMetricSubBase {

        public MatthewsCorrelationCoefficient(ComparisonMethod method, double target) {
            super(method, target);

        }

        @Override
        public Double calculate(int i) {
            if(classes.size()>2){
                loggerService.error("Unable to calculate Matthews Correlation Coefficient (MCC) for multi-class evaluation.");
                return -1.0;
            }
            long TP = sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()];
            long TN = sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()];
            long FP = sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()];
            long FN = sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()];
            double denominator = Math.sqrt((TP + FP)*(TP + FN)*(TN + FP)*(TN + FN));
            if (denominator > 0)
                return ( currentValue= ((double) (TN * TP)-(FP * FN)) / denominator);

            return 0.0;
        }

        @Override
        public double getNormalizedResult() {
            double normalizedVal = 0;
            long normTarget = Math.abs(Math.round(target+1))/2;
            long normCurrent = Math.abs(Math.round(currentValue+1))/2;

            switch (this.method){
                case Equal:
                   normalizedVal = Math.abs(normCurrent-normTarget)==0?1.0:0.0;
                    break;
                case Less:
                case LessEqual:
                    normalizedVal = (((double)normTarget)/(double)normCurrent);
                    break;
                case More:
                case MoreEqual:
                    normalizedVal = (((double)normCurrent)/(double)normTarget);
            }
            if(normalizedVal>1.0)
                normalizedVal =  1.0;
            return normalizedVal;
        }
    }
    /**
     *
     *
     */
    public class MCC extends MatthewsCorrelationCoefficient {

        public MCC(ComparisonMethod method, double target) {
            super(method, target);
        }
    }

    /**
     *
     *
     */
    public class Informedness extends ModelEvaluationMetricSubBase {

        public Informedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return (currentValue = sensitivity(i) + specificity(i) - 1);

        }

        public Double sensitivity(int i) {
            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
        public Double specificity(int i) {

            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] );
            if (denominator > 0)
                return (currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] ) / denominator);
            return 0.0;

        }
    }

    /**
     *
     *
     */
    public class Markedness extends ModelEvaluationMetricSubBase {

        public Markedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return (currentValue = precision(i) + negativePredictiveValue(i) - 1);
        }

        public Double precision(int i) {
            long denominator = ( sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]);
            if (denominator > 0)
                return (currentValue = ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]) / denominator);
            return 0.0;

        }
        private double negativePredictiveValue(int i) {


            long denominator = (sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] + sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( currentValue= ((double) sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]));
            return 0;

        }
    }




public class Samples extends eu.linksmart.services.event.ceml.evaluation.metrics.Samples {

    public Samples(ComparisonMethod method, double target) {
        super(method, target);
    }


}
    public class SlideAfter extends eu.linksmart.services.event.ceml.evaluation.metrics.Samples {

        public SlideAfter(ComparisonMethod method, double target) {
            super(method, target);
        }


        @Override
        public boolean isControlMetric() {
            return true;
        }
    }
}