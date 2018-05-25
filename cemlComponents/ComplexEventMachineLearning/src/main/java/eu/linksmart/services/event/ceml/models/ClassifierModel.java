package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.AgentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class ClassifierModel<Input,Output,LearningObject> extends ModelInstance<Input,Output,LearningObject>{


    public ClassifierModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets,parameters,new DoubleTumbleWindowEvaluator(targets),learner);
    }

    @Override
    public boolean isClassifier() {
        return true;
    }
}
