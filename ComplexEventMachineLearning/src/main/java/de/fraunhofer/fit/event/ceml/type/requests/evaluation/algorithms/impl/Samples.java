package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

public class Samples extends ModelEvaluationAlgorithmBase {

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public Double calculate() {
            return currentValue++;
        }


}