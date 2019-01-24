package eu.linksmart.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import io.swagger.client.ApiClient;
import io.swagger.client.api.ScApi;
import net.razorvine.pyro.NameServerProxy;
import net.razorvine.pyro.PyroException;
import net.razorvine.pyro.PyroProxy;
import net.razorvine.pyro.PyroURI;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by José Ángel Carvajal on 06.02.2018 a researcher of Fraunhofer FIT.
 */
public class PythonPyroBase<T> extends ClassifierModel<T, Object, PyroProxy> {
    public transient static Logger loggerService = LogManager.getLogger(PythonPyroBase.class);
    protected Process proc;
    static protected Process orchProc;
    protected File pyroAdapter;
    static protected File pyroOrchFile;
    protected int counter = 0;
    protected final static String pyroAdapterFilename = "pyroAdapter.py";
    private static final Configurator conf = Configurator.getDefaultConfig();
    protected final static String pythonPath;

    static {
        String path = "";
        try {
            path = conf.getString(Const.PYTHON_PATH);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        path.trim();
        if (!path.isEmpty() && path.charAt(path.length() - 1) != File.separator.charAt(0))
            path = path + File.separator;
        pythonPath = path;
    }

    protected Request orchestrator = null;
    protected Object baseModel = null;

    public PythonPyroBase(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, null);
        baseModel = learner;
    }

    @Override
    public PythonPyroBase build() throws TraceableException, UntraceableException {

        ScApi SCclient;

        try {
            LinkedHashMap backend = (LinkedHashMap) parameters.get("Backend");

            if ((boolean) backend.getOrDefault("Lookup", false)) {
                // Lookup a running agent
                loggerService.info("Looking up '{}' in name server '{}'", backend.get("RegisteredName"), backend.get("NameServer"));
                NameServerProxy ns = NameServerProxy.locateNS((String) backend.get("NameServer"));
                learner = new PyroProxy(ns.lookup((String) backend.get("RegisteredName")));
                ns.close();

                if (backend.get("Orchestrator") != null) {
                    String lernerUri, orchURL;
                    if (backend.get("OrchestratorName") != null) {
                        // search for existing orchestrator
                        loggerService.info("Looking up '{}' in name server '{}'", backend.get("OrchestratorName"), backend.get("NameServer"));
                        if (Utils.isRestAvailable(conf.getString(eu.linksmart.services.utils.constants.Const.LINKSMART_SERVICE_CATALOG_ENDPOINT))) {
                            ApiClient apiClient = new ApiClient();
                            apiClient.setBasePath(conf.getString(eu.linksmart.services.utils.constants.Const.LINKSMART_SERVICE_CATALOG_ENDPOINT));
                            SCclient = new ScApi(apiClient);
                            orchURL = SCclient.idGet(String.valueOf(backend.containsValue("OrchestratorName"))).getApis().get("HTTP");
                        } else {
                            throw new StatementException(this.getName(), "Bad Request", "The orchestration was unable to be obtain from Service Catalog");
                        }
                    } else if (backend.get("OrchestratorURL") != null) {
                        // search for existing orchestrator
                        loggerService.info("Looking up '{}' in name server '{}'", backend.get("OrchestratorURL"));
                        orchURL = String.valueOf(backend.get("OrchestratorURL")) + "/pyro/";
                        // orchestrator = new PyroProxy(ns.lookup((String)backend.get("OrchestratorName")));

                    } else {
                        throw new StatementException(this.getName(), "Bad Request", "The orchestration option was given but the orchestrator was not provided");
                    }

                    Request request = Request.Post(orchURL).bodyString(SharedSettings.getSerializer().toString(backend.get("Orchestrator")), ContentType.APPLICATION_JSON);
                    Map response = SharedSettings.getDeserializer().parse(request.execute().returnContent().asString(), Map.class);
                    lernerUri = response.get("uri").toString();
                    learner = new PyroProxy(new PyroURI(lernerUri));

                    // loggerService.info("Orchestrator: {}:{} {}", orchestrator.hostname, orchestrator.port, orchestrator.pyroHandshake);

                }
            } else {
                //create temp python script for the pythonAdapter
                pyroAdapter = copyPyScripts(pyroAdapterFilename);
                //run an adapter form scratch
                learner = constructProxy(pyroAdapter, proc, backend.getOrDefault("ModuleName", "").toString());
            }

            loggerService.info("Learner: {}:{} {}", learner.hostname, learner.port, learner.pyroHandshake);
            if (baseModel != null)
                learner.call("importModel", baseModel);
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

    private File copyPyScripts(String pyScriptFilename) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + pyScriptFilename);
        file.deleteOnExit();
        FileUtils.copyURLToFile(this.getClass().getClassLoader().getResource(pyScriptFilename), file);
        return file;
    }

    private PyroProxy constructProxy(File file, Process proc, String moduleName) throws IOException, InternalException {

        // LinkedHashMap backend = (LinkedHashMap)parameters.get("Backend");

        loggerService.info("Saved script to {}", file.getAbsolutePath());

        // Path to script is passed as parameter
        String[] cmd = {pythonPath + "python", "-u", file.getAbsolutePath(),
                "--bname=" + moduleName,
                "--bpath=" + file.getAbsolutePath(),
                "--orch=" + ("Orchestrator".equals(moduleName))};

        String agentPyroURI = Utils.runGetLastOutput(cmd, moduleName, loggerService);

        return new PyroProxy(new PyroURI(agentPyroURI));
    }

    @Override
    public synchronized void learn(T input) throws TraceableException, UntraceableException {
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
    public synchronized Prediction<Object> predict(T input) throws TraceableException, UntraceableException {
        try {
            return toPrediction(input, (Object) learner.call("predict", input));
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public void batchLearn(List<T> input, List<T> targetLabel) throws TraceableException, UntraceableException {
        try {
            learner.call("batchLearn", input, targetLabel);
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public void learn(T input, T targetLabel) throws TraceableException, UntraceableException {
        try {
            learner.call("learn", input, targetLabel);
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (Exception e) {
            throw new InternalException(this.getName(), "Pyro", e);
        }
    }

    @Override
    public synchronized void batchLearn(List<T> input) throws TraceableException, UntraceableException {
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
    public synchronized List<Prediction<Object>> batchPredict(List<T> input) throws TraceableException, UntraceableException {
        counter += (int) parameters.get("RetrainEvery");

        try {
            List<Object> res = (List<Object>) learner.call("batchPredict", input);

            if (input.size() != res.size())
                throw new InternalException(this.getName(), "Pyro", "Batch predictions are not the same size as inputs.");

            return IntStream.range(0, input.size()).mapToObj(i -> toPrediction(input.get(i), res.get(i))).collect(Collectors.toList());
        } catch (PyroException e) {
            loggerService.error(e._pyroTraceback);
            throw new InternalException(this.getName(), "Pyro", e);
        } catch (UnsupportedOperationException e){
            loggerService.error("Pyro integration: "+e.getMessage());
            try{
                List<Object> res = new ArrayList<>();
                for(T i:input){
                    res.add(learner.call("predict", i));
                }
                return IntStream.range(0, input.size()).mapToObj(i -> toPrediction(input.get(i), res.get(i))).collect(Collectors.toList());
            }catch (Exception ex){
                loggerService.error("Pyro integration: "+e.getMessage());
            }
        }catch (Exception e) {
            loggerService.error("Pyro integration: "+e.getMessage());
        }
        return Collections.singletonList(new PredictionInstance<>());
    }

    @Override
    public synchronized void destroy() throws Exception {
        learner.call("destroy");
        learner.close();
        if (proc != null)
            proc.destroy();
        if (pyroAdapter != null && pyroAdapter.exists())
            pyroAdapter.delete();
        super.destroy();
    }

    // Convert Object prediction to Prediction<Object>
    private Prediction<Object> toPrediction(Object input, Object response) {
        return new PredictionInstance<>(response, input, SharedSettings.getId() + ":" + this.getName(), new ArrayList<>(evaluator.getEvaluationAlgorithms().values()));
    }
}
