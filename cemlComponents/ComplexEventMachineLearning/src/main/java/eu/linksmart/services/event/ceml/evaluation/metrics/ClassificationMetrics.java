package eu.linksmart.services.event.ceml.evaluation.metrics;

import eu.linksmart.api.event.ceml.evaluation.ClassificationEvaluationValue;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;

/**
 * Created by José Ángel Carvajal on 15.02.2017 a researcher of Fraunhofer FIT.
 */
public interface ClassificationMetrics {
    /**
     *
     * Precision is a description of random errors, a measure of statistical variability.
     * see https://en.wikipedia.org/wiki/Accuracy_and_precision
     *
     */
  
        static double accuracy(long[] confusionMatrix) {

            double denominator = confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] +
                    confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()] +
                    confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()]+
                    confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()];
            if (denominator > 0)
                return (confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()])
                        / denominator;
            return 0.0;
        }

    /**
     *Accuracy has two definitions:
     *  1. more commonly, it is a description of systematic errors, a measure of statistical bias;
     *  2. alternatively, ISO defines accuracy as describing both types of observational error above (preferring the term trueness for the common definition of accuracy).
     *  https://en.wikipedia.org/wiki/Accuracy_and_precision
     *
     */
 

        static double precision(long[] confusionMatrix) {
            long denominator = ( confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()]);
            if (denominator > 0)
                return ((double) confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()]) / denominator;
            return 0.0;

        }
    
  
    /**
     * Sensitivity (also called the true positive rate, the recall, or probability of detection[1] in some fields) measures the proportion of positives that are correctly identified as such (e.g., the percentage of sick people who are correctly identified as having the condition).
     * see https://en.wikipedia.org/wiki/Sensitivity_and_specificity
     */
        static double sensitivity(long[] confusionMatrix) {
            long denominator = (confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return (  ((double) confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()]) / (double)denominator);
            return 0.0;

        }
    /**
     * Recall in information retrieval is the fraction of the documents that are relevant to the query that are successfully retrieved.
     * see https://en.wikipedia.org/wiki/Precision_and_recall#Recall
     */
    static double recall(long[] confusionMatrix) {
        return sensitivity(confusionMatrix);
    }
    

    /**
     * Specificity (also called the true negative rate) measures the proportion of negatives that are correctly identified as such (e.g., the percentage of healthy people who are correctly identified as not having the condition).
     * see https://en.wikipedia.org/wiki/Sensitivity_and_specificity
     */

        static double specificity(long[] confusionMatrix) {

            long denominator = (confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()] );
            if (denominator > 0)
                return ( ((double) confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()] ) / denominator);
            return 0.0;

        }
   
      /**
     * The positive and negative predictive values (PPV and NPV respectively) are the proportions of positive and negative results in statistics and diagnostic tests that are true positive and true negative results, respectively.
     * The PPV and NPV describe the performance of a diagnostic test or other statistical measure
     *
     * https://en.wikipedia.org/wiki/Positive_and_negative_predictive_values
     *
     */
  
        static double negativePredictiveValue(long[] confusionMatrix) {

            long denominator = (confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( ((double) confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()])/(double)denominator);
            return 0.0;

        }
    

   
    /**
     * Recall in information retrieval is the fraction of the documents that are relevant to the query that are successfully retrieved.
     *
     * see https://en.wikipedia.org/wiki/Precision_and_recall#Recall
     *
     */

        static double fallOut(long[] confusionMatrix) {
            Double denominator = (double) (confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()]);
            if (denominator > 0)
                return (confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()] / denominator);
            return 0.0;
        }


    /**
     * The false discovery rate (FDR) is one way of conceptualizing the rate of type I errors in null hypothesis testing when conducting multiple comparisons.
     * see https://en.wikipedia.org/wiki/False_discovery_rate
     */

        static double falseDiscoveryRate(long[] confusionMatrix) {
            Double denominator = (double)(confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()]);
            if (denominator > 0)
                return ( (double)confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()])/denominator;
            return 0.0;
        }

    /**
     * In statistical hypothesis testing, a type I error is the incorrect rejection of a true null hypothesis (a "false positive"), while a type II error is incorrectly retaining a false null hypothesis (a "false negative").[1] More simply stated, a type I error is detecting an effect that is not present, while a type II error is failing to detect an effect that is present.
     * see https://en.wikipedia.org/wiki/Type_I_and_type_II_errors#False_positive_and_false_negative_rates
     */
        static double missRate(long[] confusionMatrix) {
            long denominator = (confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()]);
            if (denominator > 0)
                return ( ((double) confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()]) / denominator);
            return 0.0;
        }


    /**
     * In statistical hypothesis testing, a type I error is the incorrect rejection of a true null hypothesis (a "false positive"), while a type II error is incorrectly retaining a false null hypothesis (a "false negative").[1] More simply stated, a type I error is detecting an effect that is not present, while a type II error is failing to detect an effect that is present.
     * see https://en.wikipedia.org/wiki/Type_I_and_type_II_errors#False_positive_and_false_negative_rates
     */

        static double f1Score(long[] confusionMatrix) {
            double denominator = (( confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] *2.0) + confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()] + confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()]);
            if (denominator > 0)
                return ( ( confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()] *2.0) / denominator);
            return 0.0;
        }


    /**
     * The Matthews correlation coefficient is used in machine learning as a measure of the quality of binary (two-class) classifications, introduced by biochemist Brian W. Matthews in 1975.
     * It takes into account true and false positives and negatives and is generally regarded as a balanced measure which can be used even if the classes are of very different sizes.
     * The MCC is in essence a correlation coefficient between the observed and predicted binary classifications; it returns a value between −1 and +1. A coefficient of +1 represents a perfect prediction, 0 no better than random prediction and −1 indicates total disagreement between prediction and observation.
     * The statistic is also known as the phi coefficient. MCC is related to the chi-square statistic for a 2×2 contingency table
     *
     * https://en.wikipedia.org/wiki/Matthews_correlation_coefficient
     */

        static double matthewsCorrelationCoefficient(long[] confusionMatrix) {

            long TP = confusionMatrix[ClassificationEvaluationValue.truePositives.ordinal()];
            long TN = confusionMatrix[ClassificationEvaluationValue.trueNegatives.ordinal()];
            long FP = confusionMatrix[ClassificationEvaluationValue.falsePositives.ordinal()];
            long FN = confusionMatrix[ClassificationEvaluationValue.falseNegatives.ordinal()];
            double denominator = Math.sqrt((TP + FP)*(TP + FN)*(TN + FP)*(TN + FN));
            if (denominator > 0)
                return ( ((double) (TN * TP)-(FP * FN)) / denominator);

            return 0.0;
        }





    /**
     * Youden's J statistic (also called Youden's index) is a single statistic that captures the performance of a dichotomous diagnostic test. Informedness is its generalization to the multiclass case and estimates the probability of an informed decision.
     * https://en.wikipedia.org/wiki/Youden%27s_J_statistic
     */

        static double informedness(long[] confusionMatrix) {
            return ( sensitivity(confusionMatrix) + specificity(confusionMatrix) - 1);

        }




    /**
     * https://en.wikipedia.org/wiki/Markedness
     * In statistics and psychology, the social science concept of markedness is quantified as a measure of how much one variable is marked as a predictor or possible cause of another, and is also known as Δp (deltaP) in simple two-choice cases.
     *
     */


        static double markedness(long[] confusionMatrix) {
            return ( precision(confusionMatrix) + negativePredictiveValue(confusionMatrix) - 1);
        }



}
