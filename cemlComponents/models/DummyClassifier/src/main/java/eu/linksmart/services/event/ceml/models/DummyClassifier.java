package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 05.10.2018 a researcher of Fraunhofer FIT.
 */
public class DummyClassifier extends ClassifierModel<List<Number>,Number,Function<List<Number>,Integer>> {


    private long printEvery = 10, counterLearn = 0, counterPredict = 0;

    public DummyClassifier(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
        this.learner = numbers -> numbers.stream().mapToInt(i -> ((Number) i).intValue()).sum() % 2;
    }

    final static Logger loggerService = LogManager.getLogger(DummyClassifier.class);

    @Override
    public void learn(List<Number> numbers) throws TraceableException, UntraceableException {
        if (Math.floorMod(counterLearn, printEvery) == 0) {
            loggerService.info("learning input arrives ");
            loggerService.info(numbers);
            //counterLearn=0;
        }
        counterLearn++;
    }

    @Override
    public Prediction<Number> predict(List<Number> numbers) throws TraceableException, UntraceableException {
        if (Math.floorMod(counterPredict, printEvery) == 0) {
            loggerService.info("predict input arrives ");
            loggerService.info(numbers);
            //counterPredict = 0;
        }
        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());
        counterPredict++;
        return new PredictionInstance<>(learner.apply(numbers), numbers, this.getName(), evaluationMetrics);
    }

    public void setPrintEvery(long printEvery) {
        this.printEvery = printEvery;
    }

    public long getPrintEvery() {
        return printEvery;
    }
}
