package eu.linksmart.services.event.ceml.core;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.api.FileCemlAPI;
import eu.linksmart.services.event.connectors.PersistenceService;
import eu.linksmart.services.event.types.PersistentRequestInstance;
import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.exceptions.ErrorResponseException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.types.*;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.*;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelDeserializer;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.components.AnalyzerComponent;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.ceml.api.MqttCemlAPI;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.event.ceml.intern.Const;
import org.apache.commons.math3.filter.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by angel on 13/11/15.
 */
public class CEML implements AnalyzerComponent , Feeder<CEMLRequest> {

    static AnalyzerComponent info;
    static transient private Configurator conf = Configurator.getDefaultConfig();
    static transient private Logger loggerService = LogManager.getLogger(CEML.class);

    static private Map<String, CEMLRequest> requests = new Hashtable<>();

    private static Map<String, KalmanFilter> filters = new Hashtable<>();

    static {
        System.out.println("\n" +
                "╔═╗ ╔═╗ ╔╦╗ ╔╦  \n" +
                "║   ╠═  ║║║ ║  \n" +
                "╩═╝ ╩═╝ ╩ ╩ ╩═╝ \n" );
        // Add configuration file of the local package
        Configurator.addConfFile(Const.CEML_DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();

        SharedSettings.setLs_code("LA");
        SharedSettings.getSerializer().addModule("Descriptors",DataDescriptors.class,new DataDescriptorSerializer());
        SharedSettings.getDeserializer().addModule("Descriptors",DataDescriptors.class,new DataDescriptorsDeserializer());

        SharedSettings.getSerializer().addModule("Statements",Statement.class,StatementInstance.class);
        SharedSettings.getDeserializer().addModule("Statements",Statement.class,StatementInstance.class);

        SharedSettings.getDeserializer().addModule("Model",Model.class, new ModelDeserializer());

        SharedSettings.getSerializer().addModule("LearningStatements",LearningStatement.class, eu.linksmart.services.event.ceml.statements.LearningStatement.class);
        SharedSettings.getDeserializer().addModule("LearningStatements",LearningStatement.class, eu.linksmart.services.event.ceml.statements.LearningStatement.class);


        SharedSettings.getDeserializer().addModule("DataDescriptor",DataDescriptor.class,new DataDescriptorDeserializer());

        if(conf.containsKeyAnywhere(Const.CEML_INIT_BOOTSTRAPPING)&& SharedSettings.isFirstLoad())
            (new FileCemlAPI(conf.getString(Const.CEML_INIT_BOOTSTRAPPING))).loadFiles();
        if(conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED)&& conf.getBoolean(Const.PERSISTENT_ENABLED) && !SharedSettings.isFirstLoad()) {
            PersistenceService fileFeeder = new PersistenceService(PersistentRequestInstance.getPersistentFile());

            fileFeeder.loadFiles();
            List<CEMLManager> requests = new ArrayList<>();
            if(fileFeeder.getRequests(CEMLManager.class.getCanonicalName())!=null)
                requests.add((CEMLManager) fileFeeder.getRequests(CEMLManager.class.getCanonicalName()));
            if(!requests.isEmpty())
                requests.forEach(CEML::create);
        }
        try {
            Class.forName(MqttCemlAPI.class.getCanonicalName());
        }catch (Exception e){
            e.printStackTrace();
        }
        Feeder.feeders.put(CEML.class.getCanonicalName(), new CEML());
        // bootstrap requests
        loggerService.info("The CEML has started in the Agent with ID "+ SharedSettings.getId());
    }

    private CEML(){}
    
    public static MultiResourceResponses<CEMLRequest> feedLearningRequest(CEMLRequest request){
        MultiResourceResponses<CEMLRequest> responses = new MultiResourceResponses<>();
        try {
            request.build();
        } catch (ErrorResponseException e) {
            loggerService.error(e.getMessage(),e);
            responses = new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse(e.getRequestResponse().getHeadline(),e.getRequestResponse().getAgentID(),e.getErrorProducerId(),e.getErrorProducerType(), e.getMessage(), e.getRequestResponse().getStatus()));
            return responses;
        }catch (StatementException e) {
            loggerService.error(e.getMessage(),e);
            responses = new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse("Bad Request", SharedSettings.getId(),e.getErrorProducerId(),e.getErrorProducerType(), e.getMessage(), 400));
            return responses;
        }catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            responses = new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),e.getErrorProducerId(),e.getErrorProducerType(), e.getMessage(), 500));
            return responses;
        }catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            responses = new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),null,"Agent", e.getMessage(), 500));
            return responses;
        }

        responses.addResources(request.getName(),request);
        responses.addResponse(new GeneralRequestResponse("Created", SharedSettings.getId(),request.getName(), "Learning Request", "Created",201 ));
        return responses;


    }
    public static MultiResourceResponses<CEMLRequest> create(String name, String body, String requestType)  {
        MultiResourceResponses<CEMLRequest> result ;
        CEMLRequest request;
        try {
            switch (requestType){


                case "":
                default:
                    request = SharedSettings.getDeserializer().parse(body,CEMLManager.class);
                    request.setName(name);
                    result = create(request);
            }
        } catch (IOException e){
            loggerService.error(e.getMessage(), e);
            result = new MultiResourceResponses<>();
            result.addResponse(new GeneralRequestResponse("Bad Request", SharedSettings.getId(),name, "Learning Request","Error 400 Bad Request: Error while parsing request  " + e.getMessage(),400 ));

        }
        catch (Exception e){
            loggerService.error(e.getMessage(), e);
            result = new MultiResourceResponses<>();
            result.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),name, "Learning Request","Error 500 Intern Error: Error while executing method " + e.getMessage(),500 ));

        }

        return result;
    }
    public static MultiResourceResponses<CEMLRequest> create( CEMLRequest request) {
        MultiResourceResponses<CEMLRequest> responses;
        if(request.getName()!=null)
            try {
                responses=feedLearningRequest(request);
                responses.addResources(request.getName(),request);
                requests.put(request.getName(),request);
            } catch (Exception e) {
                responses= new MultiResourceResponses<>();
                responses.addResources(request.getName(),request);
                loggerService.error(e.getMessage(), e);
                responses.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),request.getName(), "Learning Request","Error 500 Intern Error: Error while executing method " + e.getMessage(),500 ));

            }
        else{
            responses= new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse("Bad Request", SharedSettings.getId(),request.getName(), "Agent","The request does not have a the mandatory property name!",500 ));

        }


        return responses;
    }
    public static MultiResourceResponses<CEMLRequest> delete(String name, String requestType) {
        MultiResourceResponses<CEMLRequest> result = new MultiResourceResponses<>();
        CEMLRequest request;
        try {
            switch (requestType){


                case "":
                default:
                    if (requests.containsKey(name)) {
                        request =requests.get(name);
                        requests.remove(name);
                        request.destroy();
                        result.addResponse(new GeneralRequestResponse("OK", SharedSettings.getId(), name, "Learning Request", "Success 200 OK: request with name " + name + " was deleted", 200));
                        result.addResources(name,request);
                    }else {
                        result.addResponse(new GeneralRequestResponse("Bad Request", SharedSettings.getId(), name, "Learning Request", "Error 400 Bad Request: request with name " + name + " does not exists", 400));
                    }


            }

        }catch (Exception e){
            loggerService.error(e.getMessage(), e);
            result.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),name, "Learning Request","Error 500 Intern Error: Error while executing method " + e.getMessage(),500 ));

        }

        return result;
    }
    public  static MultiResourceResponses<Object> get(String name, String typeRequest) {
        String retur;
        MultiResourceResponses<Object> result = new MultiResourceResponses<>();
        try {
            if (name == null) {
                Map<String,Object> map = new Hashtable<>();
                map.putAll(requests);
                result.setResources(map);
            }else if (requests.containsKey(name))

                switch (typeRequest) {
                    case "data":

                        result.addResources(name, requests.get(name).getDescriptors());
                        break;
                    case "evaluation":
                        result.addResources(requests.get(name).getModel().getEvaluator().getClass().getSimpleName(), requests.get(name).getModel().getEvaluator());

                        break;
                    case "learning":
                        requests.get(name).getLearningStream().forEach(s->result.addResources(((Object) s.hashCode()).toString(), s));

                        break;
                    case "model":
                        result.addResources(requests.get(name).getModel().getClass().getSimpleName(), requests.get(name).getModel());
                        break;
                    case "deployment":
                        requests.get(name).getDeploymentStream().forEach(s->result.addResources(((Object) s.hashCode()).toString(), s));

                        break;

                    case "auxiliary":
                        requests.get(name).getAuxiliaryStream().forEach(s->result.addResources(((Object) s.hashCode()).toString(), s));

                        break;
                    case "prediction":
                        result.addResources(requests.get(name).getLastPrediction().getPredictedBy(),requests.get(name).getLastPrediction());
                        break;

                    case "complete":
                    default:
                        result.addResources(name, requests.get(name));
                }
            else
                result.addResponse( new GeneralRequestResponse("Not Found", SharedSettings.getId(),name, "Learning Request","Error 404 Not Found: Request with name " + name,404 ));

        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse( new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(),name, "Learning Request","Error 500 Intern Error: Error while executing method " + e.getMessage(),500 ));


        }
        result.addResponse( new GeneralRequestResponse("OK", SharedSettings.getId(),name, "Learning Request", "OK",200 ));
        return result;
    }
    static public Observation LastPrediction(String request){
        return Observation.factory(requests.get(request).getLastPrediction(),"Prediction",request, SharedSettings.getId());
    }
    static public Observation PredictUsing(String request,Object input){
        try {
            Object aux = input;
            List<EventEnvelope> orgInput= null;
            if(input instanceof ArrayList) {
                ArrayList aux1= (ArrayList)input;
                if(!aux1.isEmpty()&& aux1.get(1) instanceof EventEnvelope) {
                    orgInput = (ArrayList<EventEnvelope>) aux1;
                    aux = orgInput.stream().map(i -> i.getValue()).collect(Collectors.toList());
                }else
                    aux = input;
            }if (input instanceof EventEnvelope[]){
               orgInput = new ArrayList<>(Arrays.asList((EventEnvelope[])input));
                aux = orgInput.stream().map(i -> i.getValue()).collect(Collectors.toList());

            }else if(input instanceof Object[])
                aux=Arrays.asList((Object[])input);

            Prediction prediction = requests.get(request).getModel().predict(aux);
            prediction.setOriginalInput(input);

            requests.get(request).setLastPrediction(prediction);
            if(orgInput==null)
                return Observation.factory(prediction,"Prediction",request, SharedSettings.getId());
            else {
                return Observation.factory(prediction,"Prediction",request, SharedSettings.getId(), orgInput.get(orgInput.size()-1).getDate().getTime());
            }
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return Observation.factory(e.getMessage(),"Error",request, SharedSettings.getId());
        }
    }
    static void report(String id, String message){
        if(conf.getBoolean(Const.CEML_GenerateReports))
            MqttCemlAPI.getMeDefault().reportFeedback(id,message);
    }
    static private Map<String,Number> lastKnown = new Hashtable<>();
    static public Number filter(String filterName,Double measurement) {
        return filter(filterName,(Number) measurement);
    }
    static public Number filter(String filterName,Integer measurement) {
       return filter(filterName, (Number)measurement);
    }
    static public Number filter(String filterName,Number measurement) {
        if (!filters.containsKey(filterName))
            initFilter(filterName);

        KalmanFilter filter = filters.get(filterName);
        // predict the state estimate one time-step ahead
        // optionally provide some control input
        filter.predict();


        // obtain measurement vector z
        RealVector z = new ArrayRealVector(1, measurement.doubleValue());

        // correct the state estimate with the latest measurement
        filter.correct(z);

        double[] stateEstimate = filter.getStateEstimation();

        return stateEstimate[0];
    }
    static public Observation filter(String filterName,Observation measurement){
        try {
            if (measurement.getValue() instanceof Number)
                return Observation.factory(filter(filterName, ((Number) measurement.getResultValue())), measurement.getResultType().toString(), measurement.getDatastream().getId(), measurement.getSensor().getId(),measurement.getDate().getTime());
            if (measurement.getValue() instanceof String) {
                Double value = null;
                try {
                    value = Double.valueOf(((String) measurement.getValue()));
                    return Observation.factory(filter(filterName, value), measurement.getResultType().toString(), measurement.getDatastream().getId(), measurement.getSensor().getId(),measurement.getDate().getTime());
                } catch (Exception ignored) {
                }
            }
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }

        loggerService.error("The provided Observation is neither an Number or a known type that can be converted to a Number");

        return measurement;


    }
    static protected void initFilter(String filterName){
        // A = [ 1 ]
        RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
        // no control input
        RealMatrix B = null;
        // H = [ 1 ]
        RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
        // Q = [ 0 ]
        RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0.01 });
        // R = [ 0 ]
        RealMatrix R = new Array2DRowRealMatrix(new double[] { 0.5 });

        ProcessModel pm
                = new DefaultProcessModel(A, B, Q, new ArrayRealVector(new double[] {0.0}), null);
        MeasurementModel mm = new DefaultMeasurementModel(H, R);
        KalmanFilter filter = new KalmanFilter(pm, mm);
        filters.put(filterName,filter);
    }


    @Override
    public void feed(String topicURI, String payload) throws TraceableException, UntraceableException {
        create(topicURI,payload,"");
    }

    @Override
    public void feed(String topicURI, CEMLRequest payload) throws TraceableException, UntraceableException {
        create(payload);
    }
}
