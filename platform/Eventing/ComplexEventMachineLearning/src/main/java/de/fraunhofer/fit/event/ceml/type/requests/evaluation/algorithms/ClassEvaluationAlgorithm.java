package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ClassEvaluationAlgorithm extends EvaluationAlgorithm<Double[]>{

    Double calculate(int classIndex);


    boolean isClassReady(int classIndex);
}
