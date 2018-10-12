package eu.linksmart.api.event.ceml.evaluation.metrics;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ModelEvaluationMetric<T> extends EvaluationMetric<T> {

    T calculate();
    T calculate(int i);
}
