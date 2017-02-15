package eu.linksmart.test.event.ceml.evaluation;

import eu.linksmart.services.event.ceml.evaluation.metrics.ClassificationMetrics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by José Ángel Carvajal on 15.02.2017 a researcher of Fraunhofer FIT.
 */
public class MetricsTest {

    @Test
    public void classificationMetricsTest(){
        long[] confusionMatrix = {5,17,2,3}; // https://en.wikipedia.org/wiki/Confusion_matrix#Table of confusion
        // {TP, TN, FP, FN}
        assertEquals(81L,Math.round(ClassificationMetrics.accuracy(confusionMatrix)*100) );
        assertEquals(67L,Math.round(ClassificationMetrics.f1Score(confusionMatrix)*100)  );

        assertEquals(11L,Math.round(ClassificationMetrics.fallOut(confusionMatrix)*100)  );
        assertEquals(29L,Math.round(ClassificationMetrics.falseDiscoveryRate(confusionMatrix)*100)  );

        assertEquals(54L, Math.round(ClassificationMetrics.matthewsCorrelationCoefficient(confusionMatrix) * 100) );
        assertEquals(38L,Math.round(ClassificationMetrics.missRate(confusionMatrix)*100) );
        assertEquals(85L,Math.round(ClassificationMetrics.negativePredictiveValue(confusionMatrix)*100)  );

        assertEquals(71L,Math.round(ClassificationMetrics.precision(confusionMatrix)*100)  );

        // https://en.wikipedia.org/wiki/Sensitivity_and_specificity#Worked example
        assertEquals(67L ,Math.round(ClassificationMetrics.sensitivity(new long[]{20L, 1820L, 180L, 10L})*100)  ) ;
        assertEquals(67L ,Math.round(ClassificationMetrics.recall(new long[]{20L, 1820L, 180L, 10L})*100)  ) ;
        // https://en.wikipedia.org/wiki/Sensitivity_and_specificity#Worked example
        assertEquals(91L ,Math.round(ClassificationMetrics.specificity(new long[]{20L, 1820L, 180L, 10L})*100)  );

        //
        assertEquals(52L,Math.round(ClassificationMetrics.informedness(confusionMatrix)*100)  );
        //
        assertEquals(56L,Math.round(ClassificationMetrics.markedness(confusionMatrix)*100)  );


    }
}
