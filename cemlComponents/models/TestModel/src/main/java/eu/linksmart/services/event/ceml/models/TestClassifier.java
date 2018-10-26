package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 26.10.2018 a researcher of Fraunhofer FIT.
 */
public class TestClassifier extends ClassifierModel  {
    private transient Logger loggerService = LogManager.getLogger(TestClassifier.class);
    public TestClassifier(List targets, Map parameters, Object learner) {
        super(targets, parameters, learner);
    }

    @Override
    public void learn(Object o) throws TraceableException, UntraceableException {
        loggerService.info("Input Instance class " + o.getClass());
        if(o instanceof Collection ){
            loggerService.info("Input Instance Collection with size " + ((Collection)(o)).size());

        }


    }

    @Override
    public Prediction predict(Object o) throws TraceableException, UntraceableException {
        loggerService.info("Input Instance class " + o.getClass());
        if(o instanceof Collection ){
            loggerService.info("Input Instance Collection with size " + ((Collection)(o)).size());

        }
        return new PredictionInstance();
    }
}
