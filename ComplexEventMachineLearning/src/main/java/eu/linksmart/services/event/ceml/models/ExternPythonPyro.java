package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.DynamicConst;

import java.io.*;
import java.util.*;
import java.util.List;
import net.razorvine.pyro.*;
import org.apache.commons.io.FileUtils;

/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends ClassifierModel<Map,Integer,PyroProxy> {

    private Process proc;
    private File pyroAdapter;

    public ExternPythonPyro(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }

    @Override
    public ExternPythonPyro build() throws UntraceableException,TraceableException {

        ((DoubleTumbleWindowEvaluator)evaluator).setClasses( ((ClassesDescriptor)descriptors.getTargetDescriptors().get(0)).getClasses());

        try {
            String backendScript = (String) parameters.get("backendScript");

            pyroAdapter = new File(System.getProperty("java.io.tmpdir")+UUID.randomUUID().toString()+".py");
            FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource("pyroAdapter.py"), pyroAdapter);

            if(backendScript!=null) { // Path to script passed as parameter
                String[] cmd = {"python", "-u", pyroAdapter.getAbsolutePath(), backendScript};
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

            // build model
            learner.call("build", parameters.get("classifier"));
//            learner.call("pre_train", parameters.get("trainingFiles"));

        } catch (PyroException e) {
            throw new InternalException(this.getName(), "PyroException", e);
        } catch (IOException e) {
            throw new UnknownException(this.getName(), "IOException", e);
        }

        super.build();
        return this;
    }

    @Override
    public void learn(Map input) throws UnknownException {
        try {
            learner.call("learn", flatten(input));
        } catch (PyroException | IOException e) {
            throw new UnknownException(this.getName(), "PyroException", e);
        }
    }

    @Override
    public PredictionInstance<Integer> predict(Map input) throws UntraceableException, UnknownException {

        Integer res = null;
        try {
            res = (Integer) learner.call("predict", flatten(input));
        } catch (PyroException | IOException e) {
            throw new UnknownException(this.getName(), "PyroException", e);
        }

        Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());

        setLastPrediction(new PredictionInstance<>(res,input, DynamicConst.getId()+":"+this.getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values())));
        return (PredictionInstance<Integer>) lastPrediction;
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