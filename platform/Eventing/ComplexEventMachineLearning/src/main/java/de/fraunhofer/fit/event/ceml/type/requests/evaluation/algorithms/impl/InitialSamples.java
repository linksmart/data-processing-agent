package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

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