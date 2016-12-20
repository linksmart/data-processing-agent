package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.DynamicConst;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import net.razorvine.pyro.*;
import org.apache.commons.io.FileUtils;

/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends ClassifierModel<Map,Integer,PyroProxy> {

    private Process proc;
    private File pyroAdapter;
    static private int counter = 0;

    public ExternPythonPyro(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }

    @Override
    public ExternPythonPyro build() throws UntraceableException,TraceableException {

        ((DoubleTumbleWindowEvaluator)evaluator).setClasses( ((ClassesDescriptor)descriptors.getTargetDescriptors().get(0)).getClasses());

        try {
            Object backend = parameters.get("backend");

            pyroAdapter = new File(System.getProperty("java.io.tmpdir")+UUID.randomUUID().toString()+".py");
            FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource("pyroAdapter.py"), pyroAdapter);

            if(backend!=null) { // Path to script passed as parameter
                String[] cmd = {"python", "-u", pyroAdapter.getAbsolutePath()};
                proc = Runtime.getRuntime().exec(cmd);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String agentPyroURI = stdInput.readLine();

                new Thread(() -> {
                    String s = null;
                    try {
                        while ((s = stdInput.readLine()) != null) {
                            loggerService.info("Py: {}", s);
                        }
                        // errors
                        while ((s = stdError.readLine()) != null) {
                            loggerService.error("Py: {}", s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                learner = new PyroProxy(new PyroURI(agentPyroURI));
            } else { // Lookup a running agent
                NameServerProxy ns = NameServerProxy.locateNS(null);
                learner = new PyroProxy(ns.lookup("python-learning-agent"));
                ns.close();
            }

            learner.call("init", parameters.get("backend"));
            learner.call("build", parameters.get("classifier"));

        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }

        super.build();
        return this;
    }

    @Override
    public void learn(Map input) throws UnknownException {
        try {
            learner.call("learn", flatten(input));
        } catch (Exception e) {
            throw new UnknownException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public PredictionInstance<Integer> predict(Map input) throws UntraceableException, UnknownException {
        loggerService.info("COUNTER:"+Integer.toString(++counter));
        Integer res;
        try {
            res = (Integer) learner.call("predict", flatten(input));
        } catch (Exception e) {
            throw new UnknownException(this.getName(), "Pyro", e);
        }

        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());
        setLastPrediction(new PredictionInstance<>(res,input, DynamicConst.getId()+":"+this.getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values())));
        return (PredictionInstance<Integer>) lastPrediction;
    }

    @Override
    public void batchLearn(List<Map> input) throws TraceableException, UntraceableException{
        List<Map> flattened = input.stream().map(this::flatten).collect(Collectors.toList());

        try {
            learner.call("learn", flattened);
        } catch (Exception e) {
            throw new UnknownException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public List<Prediction<Integer>> batchPredict(List<Map> input) throws TraceableException, UntraceableException{
        List<Map> flattened = input.stream().map(this::flatten).collect(Collectors.toList());

        List<Prediction<Integer>> predictions = new ArrayList<>();
        try {
            predictions = (List<Prediction<Integer>>) learner.call("predict", flattened);
        } catch (Exception e) {
            throw new UnknownException(this.getName(), "Pyro", e);
        }

        return predictions;
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
    }


    @Override
    public void destroy() throws Exception {
        learner.close();
        if(proc!=null)
            proc.destroy();
        pyroAdapter.delete();
        super.destroy();
    }
}