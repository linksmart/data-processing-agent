package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ModelEvaluationAlgorithm  extends EvaluationAlgorithm<Double>{

    Double calculate();

}
