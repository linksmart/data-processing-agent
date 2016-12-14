package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.DynamicConst;

import java.util.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends ModelInstance<Map,Integer,Object> {


    public ExternPythonPyro(List<TargetRequest> targets,Map<String,Object> parameters, Object learner) {
        super(targets,parameters,new DoubleTumbleWindowEvaluator(targets),learner);
    }

    @Override
    public ExternPythonPyro build() throws UntraceableException,TraceableException {
//        CEML.getMapper().readValues()
        learner = new Object();
        ((DoubleTumbleWindowEvaluator)evaluator).setClasses( ((ClassesDescriptor)descriptors.getTargetDescriptors().get(0)).getClasses());
        super.build();
        return this;
    }

    @Override
    public boolean learn(Map input) throws Exception {

        return true;
    }

    @Override
    public PredictionInstance<Integer> predict(Map input) throws Exception {
        System.out.println(CEML.getMapper().writeValueAsString(input));
        setLastPrediction(new PredictionInstance<>(1,input, DynamicConst.getId()+":"+this.getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values())));
        return (PredictionInstance<Integer>) lastPrediction;
    }

    @Override
    public boolean isClassifier() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
    }
}