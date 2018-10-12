package eu.linksmart.api.event.ceml.prediction;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 18.01.2018 a researcher of Fraunhofer FIT.
 */
public class PredictionBuilder<T> implements EventBuilder<String, T, Prediction<T>> {


    @Override
    public Class BuilderOf() {
        return PredictionInstance.class;
    }
}
