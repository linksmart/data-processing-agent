package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.DynamicConst;

import java.util.*;
import java.io.IOException;
import java.util.List;
import net.razorvine.pyro.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends ModelInstance<Map,Integer,Object> {

    private Process proc;

    public ExternPythonPyro(List<TargetRequest> targets,Map<String,Object> parameters, Object learner) {
        super(targets,parameters,new DoubleTumbleWindowEvaluator(targets),learner);
    }

    @Override
    public ExternPythonPyro build() throws UntraceableException,TraceableException {
//        CEML.getMapper().readValues()
        learner = new PyroProxy();
        ((DoubleTumbleWindowEvaluator)evaluator).setClasses( ((ClassesDescriptor)descriptors.getTargetDescriptors().get(0)).getClasses());

        try {
            String agentScript = (String) parameters.get("agentScript");
            if(agentScript!=null) { // Path to script passed as parameter
                String[] cmd = {"python", "-u", agentScript};
                proc = Runtime.getRuntime().exec(cmd);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String agentPyroURI = stdInput.readLine();
                learner = new PyroProxy(new PyroURI(agentPyroURI));
            } else { // Lookup a running agent
                NameServerProxy ns = NameServerProxy.locateNS(null);
                learner = new PyroProxy(ns.lookup("python-learning-agent"));
                ns.close();
            }

//            if(((PyroProxy)learner).pyroMethods.size() + ((PyroProxy)learner).pyroOneway.size() == 0)
//                throw new UnknownUntraceableException("Agent unavailable.");

            // build model
            ((PyroProxy)learner).call("build", parameters.get("classifier"));
            ((PyroProxy)learner).call("pre_train", parameters.get("trainingFiles"));

        } catch (IOException e) {
            throw new UnknownUntraceableException(e.getMessage(), e);
        }

        super.build();
        return this;
    }

    @Override
    public boolean learn(Map input) throws Exception {
        ((PyroProxy)learner).call("learn", flatten(input));
        return true;
    }

    @Override
    public PredictionInstance<Integer> predict(Map input) throws Exception {
<<<<<<< HEAD

        Integer res = (Integer) ((PyroProxy)learner).call("predict", flatten(input));

        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());

        return new PredictionInstance<>(res, input, this.getName() + ":" + this.getClass().getSimpleName(), evaluationMetrics);
//        return new PredictionInstance<>();
    }


    private Map flatten(Map input){
        ArrayDeque entries = (ArrayDeque<HashMap>) ((HashMap)input.get("measurements")).get("e");
        HashMap<String, Double> measurements = new HashMap<>();
        for(Iterator itr = entries.iterator();itr.hasNext();){
            HashMap e = (HashMap) itr.next();
            if(e.get("v") instanceof Double)
                measurements.put((String)e.get("n"),(Double) e.get("v"));
            else
                measurements.put((String)e.get("n"),(Double) ((Integer)e.get("v")).doubleValue());
        }
        return measurements;
=======
        System.out.println(CEML.getMapper().writeValueAsString(input));
        setLastPrediction(new PredictionInstance<>(1,input, DynamicConst.getId()+":"+this.getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values())));
        return (PredictionInstance<Integer>) lastPrediction;
    }

    @Override
    public boolean isClassificator() {
        return true;
>>>>>>> 0c1270f7fbe262f2f607e3d31dd60d8238331347
    }

    @Override
    public void destroy() throws Exception {
        ((PyroProxy)learner).close();
        proc.destroy();
        super.destroy();
    }
}