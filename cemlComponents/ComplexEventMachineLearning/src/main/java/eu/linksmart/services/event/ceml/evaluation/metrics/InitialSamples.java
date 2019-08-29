package eu.linksmart.services.event.ceml.evaluation.metrics;

import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;

public class InitialSamples extends ModelEvaluationMetricBase {

    public InitialSamples(double target) {
        super(target);
        method = ComparisonMethod.More;
    }

    public InitialSamples() {
        super(0.0);
    }

    @Override
    public Double calculate() {
        if (currentValue < target)
            currentValue++;
        return currentValue;
    }


    @Override
    public boolean isControlMetric() {
        return true;
    }
}