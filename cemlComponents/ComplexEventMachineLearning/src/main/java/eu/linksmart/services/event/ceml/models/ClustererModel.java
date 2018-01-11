package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.services.event.intern.AgentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class ClustererModel<Input,Output,LearningObject> extends ModelInstance<Input,Output,LearningObject> {
    private transient Logger loggerService = LogManager.getLogger(this.getClass().getClass());

    public ClustererModel(List<TargetRequest> targets, Map<String, Object> parameters, Evaluator evaluator, Object learner) {
        super(targets, parameters, evaluator, learner);
        loggerService.error("Class not implemented yet!!");
    }
    // TBD
}
