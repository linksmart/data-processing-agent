package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.services.event.ceml.evaluation.evaluators.RegressionEvaluator;
import eu.linksmart.services.event.intern.AgentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class RegressorModel<Input extends Collection,Output extends Collection,LearningObject> extends ModelInstance<Input,Output,LearningObject> {
    private transient Logger loggerService = LogManager.getLogger(this.getClass().getClass());

    public RegressorModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets,parameters,new RegressionEvaluator(targets),learner);
    }

    @Override
    public boolean isRegressor() {
        return true;
    }
}
