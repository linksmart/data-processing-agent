package eu.linksmart.api.event.ceml.evaluation.metrics;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface MetricDefinition<T> {
    public T getThreshold();

    public String getName() ;


    public String getMethod();
}
