package eu.linksmart.services.event.ceml.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.AgentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class ClassifierModel<Input,Output,LearningObject> extends ModelInstance<Input,Output,LearningObject> {


    public ClassifierModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets,parameters,new DoubleTumbleWindowEvaluator(targets),learner);
        ((DoubleTumbleWindowEvaluator)evaluator).setInitialConfusionMatrix((long[][]) parameters.getOrDefault("initialConfusionMatrix",null));
        ((DoubleTumbleWindowEvaluator)evaluator).setInitialSamplesMatrix((long[][]) parameters.getOrDefault("initialSamplesMatrix",null));
    }





    @Override
    public boolean isClassifier() {
        return true;
    }

    @Override
    public ClassifierModel build()throws TraceableException, UntraceableException {

        ((DoubleTumbleWindowEvaluator)evaluator).setInitialConfusionMatrix((long[][]) parameters.getOrDefault("initialConfusionMatrix",null));
        ((DoubleTumbleWindowEvaluator)evaluator).setInitialSamplesMatrix((long[][]) parameters.getOrDefault("initialSamplesMatrix",null));
        try {// try legacy first!
           // ((DoubleTumbleWindowEvaluator) evaluator).setClasses(((ClassesDescriptor) descriptors.getTargetDescriptors().get(0)).getClasses());
            ((DoubleTumbleWindowEvaluator) evaluator).setClasses(new ArrayList(schema.getTargets()));
        }catch (Exception e){
            // Try to get target from list of items: In this case target is only 1 element the list (the last)
            if(schema.getType().equals("array") && schema.getTargetSize() == 1 && schema.getItems().get(schema.getSize()-1).isTarget())
                ((DoubleTumbleWindowEvaluator) evaluator).setClasses(new ArrayList<>(schema.getItems().get(schema.getSize()-1).getTargets()));
            else if ( ( schema.getType().equals("object") || schema.getType().equals("map") ) && schema.getTargets()!=null && schema.getTargets().size()==1) // Try to get target from object marked as target: In this case only one target should be marked
                ((DoubleTumbleWindowEvaluator) evaluator).setClasses(new ArrayList(schema.getTargets()));
            else if ( ( schema.getType().equals("object") || schema.getType().equals("map") ) && schema.getTargets()!=null && schema.getTargets().size()>1) // Try to get target from object marked as target: In this case only one target should be marked
                ((DoubleTumbleWindowEvaluator) evaluator).setClasses(new ArrayList(schema.getTargets()));
            else
                throw new UnknownException(name,this.getClass().getCanonicalName(),"unhandled class creation for this data schema");
        }
        evaluator.build();
        return this;
    }
}
