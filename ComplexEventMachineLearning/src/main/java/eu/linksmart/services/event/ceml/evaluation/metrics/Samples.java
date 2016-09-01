package eu.linksmart.services.event.ceml.evaluation.metrics;

import eu.linksmart.services.event.ceml.evaluation.metrics.base.ModelEvaluationMetricBase;

public class Samples extends ModelEvaluationMetricBase {

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
        }
        @Override
        public Double calculate() {
            return currentValue++;
        }

    @Override
    public boolean isControlMetric() {
        return true;
    }
}