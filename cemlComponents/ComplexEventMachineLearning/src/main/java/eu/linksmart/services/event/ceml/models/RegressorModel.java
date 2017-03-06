package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.services.event.ceml.evaluation.evaluators.RegressionEvaluator;
import eu.linksmart.services.event.intern.Utils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class RegressorModel<Input,Output,LearningObject> extends ModelInstance<Input,Output,LearningObject> {
    private transient Logger loggerService = Utils.initLoggingConf(this.getClass().getClass());

    public RegressorModel(List<TargetRequest> targets, Map<String, Object> parameters, Evaluator evaluator, Object learner) {
        super(targets,parameters,new RegressionEvaluator(targets),learner);
    }

    @Override
    public boolean isRegressor() {
        return true;
    }
}
