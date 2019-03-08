package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.api.event.ceml.evaluation.ClassificationEvaluationValue;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.metrics.ClassificationMetrics;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.metrics.*;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.*;

/**
 * Created by angel on 1/12/15.
 */
@JsonDeserialize(as = WindowEvaluatorDeserializer.class)
public class WindowEvaluator extends GenericEvaluator<Number> implements Evaluator<Number> {
    public WindowEvaluator() {
        super();
    }
  //  @JsonProperty
    private double[][] confusionMatrix;
 //   @JsonProperty
    private List classes;
 //   @JsonProperty
    private long[][] sequentialConfusionMatrix;
    private long[][] initialSamplesMatrix;



    public void setInitialConfusionMatrix(long[][] initialConfusionMatrix) {
        this.initialConfusionMatrix = initialConfusionMatrix;
    }

    @JsonIgnore
    private   long[][] initialConfusionMatrix = null;

    public WindowEvaluator(Collection<String> namesClasses, List<TargetRequest> targets) {
        super(targets);
        classes = new ArrayList<>(namesClasses);
    }
    @Override
    public double evaluate(List<Number> predicted, List<Number> actual) {
        // The evaluation only works when the classes are mutually exclusive
        if(predicted.size()!=actual.size() && actual.size()!=1)
            throw new UnsupportedOperationException("The evaluation only supports mutually exclusive classes.");
        int prediction=predicted.get(0).intValue(),groundTruth= actual.get(0).intValue();
        confusionMatrix[prediction][groundTruth]++;

        for (int i = 0; i < classes.size(); i++) {
            if (i==groundTruth && groundTruth == prediction) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()]++;
            } else if (i!=groundTruth && i != prediction) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()]++;
            } else if (i==groundTruth && i != prediction) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()]++;
            } else if ( i!=groundTruth && i == prediction) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()]++;
            }


        }

        return calculateEvaluationMetrics(groundTruth);


    }


    protected double calculateEvaluationMetrics(Number evaluatedClass) {
        double accumulateMetric = 0;
        int i = 0;
        for (EvaluationMetric algorithm : evaluationAlgorithms.values()) {

            if (algorithm instanceof ModelEvaluationMetric)
                ((ModelEvaluationMetric) algorithm).calculate();
            else if (algorithm instanceof ClassEvaluationMetric)
                ((ClassEvaluationMetric) algorithm).calculate(evaluatedClass.intValue());
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

        super.build();
        try {

            if (classes == null || classes.isEmpty())
                throw new StatementException(this.getClass().getName(), this.getClass().getCanonicalName(), "Classes is a mandatory field for WindowEvaluator");

            confusionMatrix = new double[classes.size()][classes.size()];
            sequentialConfusionMatrix = new long[classes.size()][4];

            calculateInitialConfusionMatrix();

        } catch (Exception e) {
            throw new UnknownUntraceableException(e.getMessage(), e);
        }




        return this;
    }

    private void calculateInitialConfusionMatrix() {
        if (initialSamplesMatrix != null && initialSamplesMatrix.length == classes.size()) {
            for (int i = 0; i < initialSamplesMatrix.length; i++)
                for (int j = 0; j < initialSamplesMatrix[0].length; j++)
                    for (int k = 0; k < initialSamplesMatrix[(int) initialSamplesMatrix[i][j]].length; k++) {
                   /* if(k ==i && k==j)
                        sequentialConfusionMatrix[k][ClassificationEvaluationValue.truePositives.ordinal()] = initialSamplesMatrix[i][j];
                    else if(k==i && k!=j )
                        sequentialConfusionMatrix[k][ClassificationEvaluationValue.falseNegatives.ordinal()] = initialSamplesMatrix[i][j];
                    else if(k!=i && k==j ){
                        sequentialConfusionMatrix[k][ClassificationEvaluationValue.falsePositives.ordinal()] = initialSamplesMatrix[i][j];
                    } else
                        sequentialConfusionMatrix[k][ClassificationEvaluationValue.trueNegatives.ordinal()] = initialSamplesMatrix[i][j];*/

                        evaluate(Collections.singletonList(i), Collections.singletonList(j));
                    }


        } else if (initialConfusionMatrix != null && initialConfusionMatrix.length == classes.size()){
            sequentialConfusionMatrix = initialConfusionMatrix;
            double n=0;
            for(int i=0;i<sequentialConfusionMatrix[0].length;i++)
                n += sequentialConfusionMatrix[0][i];
            evaluationAlgorithms.values().stream().forEach(a -> ((ModelEvaluationMetric) a).calculate());
            evaluationAlgorithms.get("SlideAfter").setCurrentValue(n);
        }else {
            for (int i = 0; i < classes.size(); i++) {
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.truePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.trueNegatives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falsePositives.ordinal()] = 0;
                sequentialConfusionMatrix[i][ClassificationEvaluationValue.falseNegatives.ordinal()] = 0;
            }
        }
        initialConfusionMatrix = initialSamplesMatrix = null;
    }
    public void setInitialSamplesMatrix(long[][] initialSamplesMatrix) {
        this.initialSamplesMatrix = initialSamplesMatrix;


    }

    public double[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(double[][] confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    public List getClasses() {
        return classes;
    }

    public void setClasses(List classes) {
        this.classes = classes;
    }

    public long[][] getSequentialConfusionMatrix() {
        return sequentialConfusionMatrix;
    }

    public void setSequentialConfusionMatrix(long[][] sequentialConfusionMatrix) {
        this.sequentialConfusionMatrix = sequentialConfusionMatrix;
    }

    public long[][] getInitialSamplesMatrix() {
        return initialSamplesMatrix;
    }

    public long[][] getInitialConfusionMatrix() {
        return initialConfusionMatrix;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    public abstract class ModelEvaluationMetricSubBase extends ModelEvaluationMetricBase {

        public ModelEvaluationMetricSubBase( double target) {
            super(target);

        }
        public ModelEvaluationMetricSubBase() {
            super(0.5);

        }
        @Override
        public Double calculate() {
            double acc = 0;
            for (int i = 0; i < classes.size(); i++)
                acc += calculate(i);

            return currentValue = acc / (double) classes.size();

        }
    }

    /**
     *
     * See ClassificationMetrics for documentation and definition of the metrics
     *
     * */


     public class Accuracy extends ModelEvaluationMetricSubBase {
        public Accuracy(double target) {
            super(target);
        }

        public Accuracy() {
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.accuracy(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */ public class ACC extends Accuracy {public ACC(double target) {super(target);}}

    public class Precision extends ModelEvaluationMetricSubBase {
        public Precision() {
            super();
        }
        public Precision(double target) {
            super(target);
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.precision(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class PPV extends Precision {public PPV(double target) {super(target);}}


    public class Sensitivity extends ModelEvaluationMetricSubBase {
        public Sensitivity(double target) {
            super(target);
        }
        public Sensitivity() {
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.sensitivity(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */ public class TPR extends Sensitivity {public TPR(double target) {super(target);}
        public TPR() {
            super();
        }
    }
    /* Alias */ public class Recall extends Sensitivity {public Recall(double target) {super(target);} public Recall(){super();}}


    public class Specificity extends ModelEvaluationMetricSubBase {
        public Specificity(double target) {
            super(target);
        }

        public Specificity(){
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.specificity(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class TNR extends Specificity {public TNR(double target) {super(target);} public TNR(){super();}}

    public class NegativePredictiveValue extends ModelEvaluationMetricSubBase {
        public NegativePredictiveValue(double target) {
            super(target);
        }

        public NegativePredictiveValue(){
            super();
        }
        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.negativePredictiveValue(sequentialConfusionMatrix[i]);

        }
    }
    /* Alias */ public class NPV extends NegativePredictiveValue {public NPV(double target) {super(target);} public NPV(){super();}}

    public class FallOut extends ModelEvaluationMetricSubBase {

        public FallOut(double target) {
            super(target);
        }

        public FallOut(){
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.fallOut(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FPR extends FallOut {public FPR(double target) {super(target);} public FPR(){super();}}


    public class FalseDiscoveryRate extends ModelEvaluationMetricSubBase {

        public FalseDiscoveryRate(double target) {
            super(target);
        }

        public FalseDiscoveryRate(){
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.falseDiscoveryRate(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FDR extends FalseDiscoveryRate {public FDR(double target) {super(target);} public FDR(){super();}}


    public class MissRate extends ModelEvaluationMetricSubBase {

        public MissRate(double target) {
            super(target);
        }

        public MissRate(){
            super();
        }

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.missRate(sequentialConfusionMatrix[i]);
        }
    }
    /* Alias */public class FNR extends MissRate {public FNR(double target) {super(target);} public FNR(){super();}}


    public class F1Score extends ModelEvaluationMetricSubBase {

        public F1Score(double target) {
            super(target);
        }

        public F1Score(){super();}

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.f1Score(sequentialConfusionMatrix[i]);
        }
    }


    public class MatthewsCorrelationCoefficient extends ModelEvaluationMetricSubBase {

        public MatthewsCorrelationCoefficient(double target) {
            super(target);
        }

        public MatthewsCorrelationCoefficient(){
            super();

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
    /* Alias */public class MCC extends MatthewsCorrelationCoefficient {public MCC(double target) {super(target);} public MCC(){super();}}

    public class Informedness extends ModelEvaluationMetricSubBase {

        public Informedness(double target) {
            super(target);
        }

        public Informedness(){super();}

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.informedness(sequentialConfusionMatrix[i]);

        }
    }


    public class Markedness extends ModelEvaluationMetricSubBase {
        public Markedness(double target) {
            super( target);
        }

        public Markedness(){super();}

        @Override
        public Double calculate(int i) {
            return ClassificationMetrics.markedness(sequentialConfusionMatrix[i]);
        }
    }

    public class Samples extends eu.linksmart.services.event.ceml.evaluation.metrics.Samples {

        public Samples(double target) {
            super( target);
            method = ComparisonMethod.More;
        }

        public Samples(){super(0);}
    }

    public class SlideAfter extends eu.linksmart.services.event.ceml.evaluation.metrics.Samples {
        public SlideAfter(double target) {
            super( target);
            method = ComparisonMethod.More;
        }
        public SlideAfter(){
            super(100);
            method = ComparisonMethod.More;
        }
        @Override
        public boolean isControlMetric() {
            return true;
        }
    }
}