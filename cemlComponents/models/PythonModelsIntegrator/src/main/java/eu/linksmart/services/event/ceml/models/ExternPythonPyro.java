package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.intern.DynamicConst;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.razorvine.pyro.*;
import org.apache.commons.io.FileUtils;

/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends ClassifierModel<Object,Integer,PyroProxy> {

    private Process proc;
    private File pyroAdapter;
    private int counter = 0;
    private final String pyroAdapterFilename = "pyroAdapter.py";

    public ExternPythonPyro(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }

    @Override
    public ExternPythonPyro build() throws TraceableException, UntraceableException {

        ((DoubleTumbleWindowEvaluator)evaluator).setClasses( ((ClassesDescriptor)descriptors.getTargetDescriptors().get(0)).getClasses());

        try {
            LinkedHashMap backend = (LinkedHashMap)parameters.get("Backend");

            if((boolean)backend.get("Lookup")) {
                // Lookup a running agent
                loggerService.info("Looking up '{}' in name server '{}'", backend.get("RegisteredName"), backend.get("NameServer"));
                NameServerProxy ns = NameServerProxy.locateNS((String)backend.get("NameServer"));
                learner = new PyroProxy(ns.lookup((String)backend.get("RegisteredName")));
                ns.close();
            } else {
                pyroAdapter = new File(System.getProperty("java.io.tmpdir")+File.separator+UUID.randomUUID().toString()+"-"+pyroAdapterFilename);
                pyroAdapter.deleteOnExit();
                FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource(pyroAdapterFilename), pyroAdapter);
                loggerService.info("Saved script to {}", pyroAdapter.getAbsolutePath());

                // Path to script is passed as parameter
                String[] cmd = {"python", "-u", pyroAdapter.getAbsolutePath(),
                        "--bname="+(String)backend.get("ModuleName"),
                        "--bpath="+(String)backend.get("ModulePath")};
                proc = Runtime.getRuntime().exec(cmd);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                String agentPyroURI = stdInput.readLine();
                if(agentPyroURI == null || !agentPyroURI.startsWith("PYRO")) {
                    String s = null;
                    while ((s = stdError.readLine()) != null) {
                        loggerService.error("Proc: {}", s);
                    }
                    throw new InternalException(this.getName(), "Pyro", "Expected PYRO:obj@host:port but got: " + agentPyroURI);
                }

                new Thread(() -> {
                    String s = null;
                    try {
                        while ((s = stdInput.readLine()) != null) {
                            loggerService.info("Proc: {}", s);
                        }
                        // errors
                        while ((s = stdError.readLine()) != null) {
                            loggerService.error("Proc: {}", s);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                learner = new PyroProxy(new PyroURI(agentPyroURI));
            }

            loggerService.info("Learner: {}:{} {}", learner.hostname, learner.port, learner.pyroHandshake);
            learner.call("build", parameters.get("Classifier"));

        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }

        super.build();
        return this;
    }

    @Override
    public synchronized void learn(Object input) throws TraceableException, UntraceableException {
        try {
            learner.call("learn", input);
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public synchronized Prediction<Integer> predict(Object input) throws TraceableException, UntraceableException {
        loggerService.info("Total Events: "+Integer.toString(++counter)+
                "\t"+((HashMap)((HashMap)parameters.get("Classifier")).get("production_layout")).get("type")+"<-"+((HashMap)input).get("type"));

        try {
            return toPrediction(input, (Integer) learner.call("predict", input));
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public synchronized void batchLearn(List<Object> input) throws TraceableException, UntraceableException{
        try {
            learner.call("batchLearn", input);
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public synchronized List<Prediction<Integer>> batchPredict(List<Object> input) throws TraceableException, UntraceableException{
        counter += (int) parameters.get("RetrainEvery");
        loggerService.info("Total Events: "+Integer.toString(counter));

        try {
            List<Integer> res = (List<Integer>) learner.call("batchPredict", input);

            if (input.size() != res.size())
                throw new InternalException(this.getName(), "Pyro", "Batch predictions are not the same size as inputs.");

            return IntStream.range(0, input.size()).mapToObj(i -> toPrediction(input.get(i), res.get(i))).collect(Collectors.toList());
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public synchronized void destroy() throws Exception {
        learner.call("destroy");
        learner.close();
        if(proc!=null)
            proc.destroy();
        if(pyroAdapter.exists())
            pyroAdapter.delete();
        super.destroy();
    }

    // Convert integer prediction to Prediction<Integer>
    private Prediction<Integer> toPrediction(Object input, Integer response) {
        return new PredictionInstance<>(response,input, DynamicConst.getId()+":"+this.getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values()));
    }
}