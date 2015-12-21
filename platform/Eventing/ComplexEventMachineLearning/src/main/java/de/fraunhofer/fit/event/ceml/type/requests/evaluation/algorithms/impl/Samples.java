package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

public class Samples extends EvaluationAlgorithmBase {

        public Samples(ComparisonMethod method, double target) {
            super(method, target);
        }

        @Override
        public double calculate() {
            return currentValue++;
        }


}