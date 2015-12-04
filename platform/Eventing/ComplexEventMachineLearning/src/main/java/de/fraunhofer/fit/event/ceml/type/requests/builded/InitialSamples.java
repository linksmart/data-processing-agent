package de.fraunhofer.fit.event.ceml.type.requests.builded;

public class InitialSamples extends EvaluationAlgorithmBase {

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