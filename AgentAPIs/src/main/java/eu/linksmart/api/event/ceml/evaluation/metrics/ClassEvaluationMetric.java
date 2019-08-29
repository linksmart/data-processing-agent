package eu.linksmart.api.event.ceml.evaluation.metrics;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public interface ClassEvaluationMetric<T> extends EvaluationMetric<T[]> {

    T calculate(int classIndex);
    T getClassResult(int classIndex);
    boolean isClassReady(int classIndex);
}
