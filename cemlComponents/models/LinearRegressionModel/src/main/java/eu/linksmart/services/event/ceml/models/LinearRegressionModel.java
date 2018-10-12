package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 20.04.2017 a researcher of Fraunhofer FIT.
 */
public class LinearRegressionModel extends RegressorModel<List<Number>,List<Number>,SimpleRegression> {

    static {
        Model.loadedModels.put(LinearRegressionModel.class.getSimpleName(),LinearRegressionModel.class);
    }
    public LinearRegressionModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }


    @Override
    public void learn(List<Number> xy) throws TraceableException, UntraceableException {
        learner.addData(xy.get(0).doubleValue(),xy.get(1).doubleValue());
    }


    @Override
    public Prediction< List<Number>> predict(List<Number> valueNothing) throws TraceableException, UntraceableException {
        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());

        return new PredictionInstance<>(Arrays.asList(learner.predict(valueNothing.get(0).doubleValue())),valueNothing,name+":"+this.getName(),evaluationMetrics);
    }

    @Override
    public Model<List<Number>, List<Number>, SimpleRegression> build() throws TraceableException, UntraceableException {
        learner = new SimpleRegression();
        return super.build();
    }
}
