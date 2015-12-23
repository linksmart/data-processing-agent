package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

public class InitialSamples extends ModelEvaluationAlgorithmBase {

        public InitialSamples(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            if(currentValue>target)
                currentValue--;
            return currentValue;
        }


}