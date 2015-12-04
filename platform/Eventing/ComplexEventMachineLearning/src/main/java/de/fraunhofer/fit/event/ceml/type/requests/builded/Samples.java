package de.fraunhofer.fit.event.ceml.type.requests.builded;

public class Samples extends EvaluationAlgorithmBase {

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            return currentValue++;
        }
    }