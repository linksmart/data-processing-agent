package eu.linksmart.test.event.ceml.evaluation;

import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.ceml.evaluation.evaluators.RegressionEvaluator;
import eu.linksmart.services.event.ceml.evaluation.evaluators.WindowEvaluator;
import eu.linksmart.services.event.ceml.evaluation.metrics.ClassificationMetrics;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by José Ángel Carvajal on 15.02.2017 a researcher of Fraunhofer FIT.
 */
public class EvaluationTest {

    @Test
    public void classificationMetricsTest(){
        long[] confusionMatrix = {5,17,2,3}; // https://en.wikipedia.org/wiki/Confusion_matrix#Table_of_confusion
        // {TP, TN, FP, FN}
        assertEquals("Testing Accuracy with confusion matrix [[TP, FN],[FP, TN]] -> ACC([[5, 3], [2, 17]])*100 == ",81L,Math.round(ClassificationMetrics.accuracy(confusionMatrix)*100));
        assertEquals("Testing F1Score with confusion matrix [[TP, FN],[FP, TN]] -> F1([[5, 3], [2, 17]])*100 == ",67L,Math.round(ClassificationMetrics.f1Score(confusionMatrix)*100));

        assertEquals("Testing FallOut with confusion matrix [[TP, FN],[FP, TN]] -> FPR([[5, 3], [2, 17]])*100 == ",11L,Math.round(ClassificationMetrics.fallOut(confusionMatrix)*100));
        assertEquals("Testing matthewsCorrelationCoefficient with confusion matrix [[TP, FN],[FP, TN]] -> FDR([[5, 3], [2, 17]])*100 ==",29L,Math.round(ClassificationMetrics.falseDiscoveryRate(confusionMatrix)*100));

        assertEquals("Testing Accuracy with confusion matrix [[TP, FN],[FP, TN]] -> MCC([[5, 3], [2, 17]])*100 == ",54L, Math.round(ClassificationMetrics.matthewsCorrelationCoefficient(confusionMatrix) * 100));
        assertEquals("Testing missRate with confusion matrix [[TP, FN],[FP, TN]] -> FNR([[5, 3], [2, 17]])*100 == ",38L,Math.round(ClassificationMetrics.missRate(confusionMatrix)*100));
        assertEquals("Testing negativePredictiveValue with confusion matrix [[TP, FN],[FP, TN]] -> NPV([[5, 3], [2, 17]])*100 == ",85L,Math.round(ClassificationMetrics.negativePredictiveValue(confusionMatrix)*100));

        assertEquals("Testing precision with confusion matrix [[TP, FN],[FP, TN]] -> PPV([[5, 3], [2, 17]])*100 == ",71L,Math.round(ClassificationMetrics.precision(confusionMatrix)*100));

        // https://en.wikipedia.org/wiki/Sensitivity_and_specificity#Worked_example
        assertEquals("Testing sensitivity with confusion matrix [[TP, FN],[FP, TN]] -> TPR([[5, 3], [2, 17]])*100 == ",67L ,Math.round(ClassificationMetrics.sensitivity(new long[]{20L, 1820L, 180L, 10L})*100)) ;
        assertEquals("Testing recall with confusion matrix [[TP, FN],[FP, TN]] -> TPR([[5, 3], [2, 17]])*100 == ",67L ,Math.round(ClassificationMetrics.recall(new long[]{20L, 1820L, 180L, 10L})*100)) ;
        // https://en.wikipedia.org/wiki/Sensitivity_and_specificity#Worked_example
        assertEquals("Testing specificity with confusion matrix [[TP, FN],[FP, TN]] -> TNR([[5, 3], [2, 17]])*100 == ",91L ,Math.round(ClassificationMetrics.specificity(new long[]{20L, 1820L, 180L, 10L})*100));


        assertEquals("Testing informedness with confusion matrix [[TP, FN],[FP, TN]] -> BM([[5, 3], [2, 17]])*100 ==",52L,Math.round(ClassificationMetrics.informedness(confusionMatrix)*100));
        assertEquals("Testing markedness with confusion matrix [[TP, FN],[FP, TN]] -> MK([[5, 3], [2, 17]])*100 == ",56L,Math.round(ClassificationMetrics.markedness(confusionMatrix)*100));


    }
    @Test
    public void windowEvaluatorTest(){


        WindowEvaluator evaluator = new WindowEvaluator(Arrays.asList("Cat","Dog","Rabbit"),Arrays.asList(new TargetRequest(0.8,"Accuracy","More"), new TargetRequest(26,"SlideAfter","More")));

        try {
            evaluator.build();
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

        feedClassificationEvaluator(evaluator);

        assertTrue("Testing WindowEvaluator with (Accuracy > 0.8 && SlideAfter > 26 ) == ", evaluator.isDeployable());


    }

    @Test
    public void doubleWindowEvaluatorTest() {

        DoubleTumbleWindowEvaluator evaluator = new DoubleTumbleWindowEvaluator(Arrays.asList(
                new TargetRequest(0.8, "Accuracy", "More"),
                new TargetRequest(0.65, "Precision", "More"),
                new TargetRequest(0.85, "Specificity", "More"),
                new TargetRequest(0.85, "NegativePredictiveValue", "More"),
                new TargetRequest(0.10, "FallOut", "More"),
                new TargetRequest(0.34, "FalseDiscoveryRate", "Less"),
                new TargetRequest(0.33, "MissRate", "Less"),
                new TargetRequest(0.65, "F1Score", "More"),
                new TargetRequest(0.51, "MatthewsCorrelationCoefficient", "More"),
                new TargetRequest(0.52, "Informedness", "More"),
                new TargetRequest(0.51, "Markedness", "More"),
                new TargetRequest(27, "SlideAfter", "More")));
        evaluator.setClasses(Arrays.asList("Cat", "Dog", "Rabbit"));
        try {
            evaluator.build();
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

        feedClassificationEvaluator(evaluator);

        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Accuracy").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Precision").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Specificity").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("NegativePredictiveValue").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", false, evaluator.getEvaluationAlgorithms().get("FalseDiscoveryRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", false, evaluator.getEvaluationAlgorithms().get("MissRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("F1Score").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MatthewsCorrelationCoefficient").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Informedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Markedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.SlideAfter.isReady() == ", false, evaluator.getEvaluationAlgorithms().get("SlideAfter").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.isReady() == ", false, evaluator.isDeployable());
        assertEquals("Testing DoubleTumbleWindowEvaluator.readyToSlide() == ", false, evaluator.readyToSlide());
        evaluator.evaluate(Collections.singletonList(0), Collections.singletonList(0));
        testValuesOfMetrics(evaluator);
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Accuracy").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Precision").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Specificity").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("NegativePredictiveValue").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("FalseDiscoveryRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MissRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("F1Score").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MatthewsCorrelationCoefficient").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Informedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Markedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.SlideAfter.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("SlideAfter").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.isReady() == ", true, evaluator.isDeployable());
        assertEquals("Testing DoubleTumbleWindowEvaluator.readyToSlide() == ", false, evaluator.readyToSlide());


        feedClassificationEvaluator(evaluator);
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Accuracy").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Precision").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Specificity").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("NegativePredictiveValue").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("FalseDiscoveryRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MissRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("F1Score").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MatthewsCorrelationCoefficient").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Informedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Markedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.SlideAfter.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("SlideAfter").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.isReady() == false", true, evaluator.isDeployable());
        assertEquals("Testing DoubleTumbleWindowEvaluator.readyToSlide() == false", false, evaluator.readyToSlide());
        evaluator.evaluate(Collections.singletonList(0), Collections.singletonList(0));
        testValuesOfMetrics(evaluator);
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Accuracy").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Precision").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Specificity").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("NegativePredictiveValue").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("FalseDiscoveryRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MissRate").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("F1Score").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("MatthewsCorrelationCoefficient").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Informedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.accuracy.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("Markedness").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.SlideAfter.isReady() == ", true, evaluator.getEvaluationAlgorithms().get("SlideAfter").isReady());
        assertEquals("Testing DoubleTumbleWindowEvaluator.isReady() == ", true, evaluator.isDeployable());
        assertEquals("Testing DoubleTumbleWindowEvaluator.readyToSlide() == ", false, evaluator.readyToSlide());
    }
    private void feedClassificationEvaluator(Evaluator evaluator){
        // https://en.wikipedia.org/wiki/Confusion_matrix#Example
        int [][] samples = {{5,3,0},{2,3,1},{0,2,11}};
        for (int i = 0; i < samples.length; i++)
            for (int j = 0; j < samples[i].length; j++)
                for (int k = 0; k < samples[i][j]; k++) {
                    evaluator.evaluate(Collections.singletonList(i), Collections.singletonList(j));
                    testValuesOfMetrics(evaluator);
                }
    }
    private void testValuesOfMetrics(Evaluator evaluator){
        for (EvaluationMetric<Number> metric : (Collection<EvaluationMetric<Number>>)evaluator.getEvaluationAlgorithms().values()){
            assertNotEquals(0,metric.getResult());
        }
    }

    @Test
    public void regressionEvaluatorTest(){
        RegressionEvaluator regressionEvaluator = new RegressionEvaluator(Arrays.asList(new TargetRequest(37, "RMSE", "less"), new TargetRequest(35, "MAE", "less")));
        try {
            regressionEvaluator.build();
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

        regressionEvaluator.evaluate(
                Arrays.asList(10.0,20.0,30.0,40.0,50.0,60.0),Arrays.asList(40.0,40.0,40.0,40.0,40.0,40.0)); //RMSE=19,49 , MAE=18
        assertEquals("Testing RegressionEvaluator.RMSE.isReady() == ", true, regressionEvaluator.getEvaluationAlgorithms().get("RMSE").isReady());
        assertEquals("Testing regressionEvaluator.MAE.isReady() == ", true, regressionEvaluator.getEvaluationAlgorithms().get("MAE").isReady());


        regressionEvaluator.evaluate(
                Arrays.asList(10.0,20.0,30.0,40.0,50.0,60.0),Arrays.asList(90.0,91.0,92.0,93.0,93.0,93.0)); //RMSE=44.07 , MAE=36

        assertEquals("Testing RegressionEvaluator.RMSE.isReady() == ", false, regressionEvaluator.getEvaluationAlgorithms().get("RMSE").isReady());
        assertEquals("Testing regressionEvaluator.MAE.isReady() == ", false, regressionEvaluator.getEvaluationAlgorithms().get("MAE").isReady());

        regressionEvaluator.evaluate(
                Arrays.asList(10.0,20.0,30.0,40.0,50.0,60.0),Arrays.asList(10.0,20.0,30.0,40.0,50.0,60.0)); //RMSE=35.98 , MAE=24

        assertEquals("Testing RegressionEvaluator.RMSE.isReady() == ", true, regressionEvaluator.getEvaluationAlgorithms().get("RMSE").isReady());
        assertEquals("Testing regressionEvaluator.MAE.isReady() == ", true, regressionEvaluator.getEvaluationAlgorithms().get("MAE").isReady());

    }
}
