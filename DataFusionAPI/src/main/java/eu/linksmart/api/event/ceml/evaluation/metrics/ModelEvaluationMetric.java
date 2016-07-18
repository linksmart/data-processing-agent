package eu.linksmart.api.event.ceml.evaluation.metrics;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ModelEvaluationMetric extends EvaluationMetric<Double> {

    Double calculate();

}
