package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ClassEvaluationAlgorithm extends EvaluationAlgorithm{

    double calculate(int classIndex);

    Double[] getTarget();
    void setTarget(Double[] target);
    Double[] getResult();

    boolean isClassReady(int classIndex);
}
