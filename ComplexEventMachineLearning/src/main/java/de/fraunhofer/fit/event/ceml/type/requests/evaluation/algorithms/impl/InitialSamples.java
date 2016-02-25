package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

public class InitialSamples extends ModelEvaluationAlgorithmBase {

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