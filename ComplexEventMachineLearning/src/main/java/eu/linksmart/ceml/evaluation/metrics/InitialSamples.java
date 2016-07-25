package eu.linksmart.ceml.evaluation.metrics;

public class InitialSamples extends ModelEvaluationMetricBase {

        public InitialSamples(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            if(currentValue<target)
                currentValue++;
            return currentValue;
        }


    @Override
    public boolean isControlMetric() {
        return true;
    }
}