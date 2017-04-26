package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 20.04.2017 a researcher of Fraunhofer FIT.
 */
public class LinearRegressionModel extends RegressorModel<List<Double>,List<Double>,SimpleRegression> {

    static {
        Model.loadedModels.put(LinearRegressionModel.class.getSimpleName(),LinearRegressionModel.class);
    }
    public LinearRegressionModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }


    @Override
    public void learn(List<Double> xy) throws TraceableException, UntraceableException {
        learner.addData(xy.get(0),xy.get(1));
    }


    @Override
    public Prediction< List<Double>> predict(List<Double> valueNothing) throws TraceableException, UntraceableException {
        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());

        return new PredictionInstance<>(Arrays.asList(learner.predict(valueNothing.get(0))),valueNothing,this.getName(),evaluationMetrics);
    }

    @Override
    public Model<List<Double>, List<Double>, SimpleRegression> build() throws TraceableException, UntraceableException {
        learner = new SimpleRegression();
        return super.build();
    }
}
