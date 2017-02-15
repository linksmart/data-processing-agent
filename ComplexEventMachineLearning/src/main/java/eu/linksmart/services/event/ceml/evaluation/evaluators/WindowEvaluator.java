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
import eu.linksmart.services.event.ceml.evaluation.metrics.ClassificationMetrics;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.*;

import java.util.*;

/**
 * Created by angel on 1/12/15.
 */
public class WindowEvaluator extends GenericEvaluator<Integer> implements Evaluator<Integer> {

    @JsonProperty
    private double[][] confusionMatrix;
    @JsonProperty
    private List<String> classes;
    @JsonProperty
    private long[][] sequentialConfusionMatrix;


    public WindowEvaluator(Collection<String> namesClasses, List<TargetRequest> targets) {
        super(targets);
        classes = new ArrayList<>(namesClasses);
    }

    @Override
    public double evaluate(Integer predicted, Integer actual) {
        confusionMatrix[actual][predicted]++;

        for (int i = 0; i < classes.size(); i++) {
            if (actual.equals(i) && actual.equals(predicted)) {
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


    protected double calculateEvaluationMetrics(int evaluatedClass) {
        double accumulateMetric = 0;
        int i = 0;
        for (EvaluationMetric algorithm : evaluationAlgorithms.values()) {

            if (algorithm instanceof ModelEvaluationMetric)
                ((ModelEvaluationMetric) algorithm).calculate();
            else if (algorithm instanceof ClassEvaluationMetric)
                ((ClassEvaluationMetric) algorithm).calculate(evaluatedClass);
            else
                loggerService.error("Evaluation algorithm " + algorithm.getClass().getName() + " is an instance of an unknown algorithm class");

            if (!algorithm.isControlMetric()) {
                accumulateMetric += algorithm.getNormalizedResult();
                i++;
            }

        }
        return accumulateMetric / (evaluationAlgorithms.size() - i);

    }


    public boolean readyToSlide() {
        return evaluationAlgorithms.get(SlideAfter.class.getSimpleName()).isReady();
    }


    protected void reset() {
        for (int i = 0; i < classes.size(); i++) {
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
            sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
        }


        evaluationAlgorithms.values().forEach(eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric::reset);

    }

    @Override
    public WindowEvaluator build() throws TraceableException, UntraceableException {

        try {

            if (classes == null || classes.isEmpty())
                throw new StatementException(this.getClass().getName(), this.getClass().getCanonicalName(), "Classes is a mandatory field for WindowEvaluator");

            confusionMatrix = new double[classes.size()][classes.size()];
            sequentialConfusionMatrix = new long[classes.size()][4];
            for (int i = 0; i < classes.size(); i++) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
            }
        } catch (Exception e) {
            throw new UnknownUntraceableException(e.getMessage(), e);
        }


        super.build();


        return this;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    public abstract class ModelEvaluationMetricSubBase extends ModelEvaluationMetricBase {

        public ModelEvaluationMetricSubBase(EvaluationMetric.ComparisonMethod method, double target) {
            super(ComparisonMethod.More, target);

        }

        @Override
        public Double calculate() {
            double acc = 0;
            for (int i = 0; i < classes.size(); i++)
                acc = calculate(i);

            return acc / (double) classes.size();

        }
    }

    /**
     *
     * See ClassificationMetrics for documentation and definition of the metrics
     *
     * */


     public class Accuracy extends ModelEvaluationMetricSubBase {
        public Accuracy(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.accuracy(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */ public class ACC extends Accuracy {public ACC(ComparisonMethod method, double target) {super(method, target);}}

    public class Precision extends ModelEvaluationMetricSubBase {
        public Precision(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.precision(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class PPV extends Precision {public PPV(ComparisonMethod method, double target) {super(method, target);}}


    public class Sensitivity extends ModelEvaluationMetricSubBase {
        public Sensitivity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.sensitivity(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */ public class TPR extends Sensitivity {public TPR(ComparisonMethod method, double target) {super(method, target);}}
    /* Alias */ public class Recall extends Sensitivity {public Recall(ComparisonMethod method, double target) {super(method, target);}}


    public class Specificity extends ModelEvaluationMetricSubBase {
        public Specificity(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.specificity(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class TNR extends Specificity {public TNR(ComparisonMethod method, double target) {super(method, target);}}

    public class NegativePredictiveValue extends ModelEvaluationMetricSubBase {
        public NegativePredictiveValue(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.negativePredictiveValue(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class NPV extends NegativePredictiveValue {public NPV(ComparisonMethod method, double target) {super(method, target);}}

    public class FallOut extends ModelEvaluationMetricSubBase {

        public FallOut(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.fallOut(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FPR extends FallOut {public FPR(ComparisonMethod method, double target) {super(method, target);}}


    public class FalseDiscoveryRate extends ModelEvaluationMetricSubBase {

        public FalseDiscoveryRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.falseDiscoveryRate(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FDR extends FalseDiscoveryRate {public FDR(ComparisonMethod method, double target) {super(method, target);}}


    public class MissRate extends ModelEvaluationMetricSubBase {

        public MissRate(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.missRate(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FNR extends MissRate {public FNR(ComparisonMethod method, double target) {super(method, target);}}


    public class F1Score extends ModelEvaluationMetricSubBase {

        public F1Score(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.f1Score(sequentialConfusionMatrix[i]);
        }
    }


    public class MatthewsCorrelationCoefficient extends ModelEvaluationMetricSubBase {

        public MatthewsCorrelationCoefficient(ComparisonMethod method, double target) {
            super(method, target);

        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.matthewsCorrelationCoefficient(sequentialConfusionMatrix[i]);
        }

        @Override
        public double getNormalizedResult() {
            double normalizedVal = 0;
            long normTarget = Math.abs(Math.round(target + 1)) / 2;
            long normCurrent = Math.abs(Math.round(currentValue + 1)) / 2;

            switch (this.method) {
                case Equal:
                    normalizedVal = Math.abs(normCurrent - normTarget) == 0 ? 1.0 : 0.0;
                    break;
                case Less:
                case LessEqual:
                    normalizedVal = (((double) normTarget) / (double) normCurrent);
                    break;
                case More:
                case MoreEqual:
                    normalizedVal = (((double) normCurrent) / (double) normTarget);
            }
            if (normalizedVal > 1.0)
                normalizedVal = 1.0;
            return normalizedVal;
        }
    }
    /* Alias */public class MCC extends MatthewsCorrelationCoefficient {public MCC(ComparisonMethod method, double target) {super(method, target);}}

    public class Informedness extends ModelEvaluationMetricSubBase {

        public Informedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.informedness(sequentialConfusionMatrix[i]);

        }
    }


    public class Markedness extends ModelEvaluationMetricSubBase {
        public Markedness(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.markedness(sequentialConfusionMatrix[i]);
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