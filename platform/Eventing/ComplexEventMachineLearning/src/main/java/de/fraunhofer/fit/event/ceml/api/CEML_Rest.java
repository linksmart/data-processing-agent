package de.fraunhofer.fit.event.ceml.api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import de.fraunhofer.fit.event.ceml.core.CEML;
import de.fraunhofer.fit.event.ceml.intern.Const;
import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import eu.almanac.event.datafusion.feeder.StatementFeeder;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.StatementResponse;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import com.google.gson.*;
/**
 * Created by José Ángel Carvajal on 01.02.2016 a researcher of Fraunhofer FIT.
 */
public class CEML_Rest {


    /**
     * Created by angel on 13/11/15.
     */

    static private Configurator conf = Configurator.getDefaultConfig();
    static private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);

    static private Map<String, LearningRequest> requests = new Hashtable<>();
    static private ObjectMapper mapper = new ObjectMapper();


    static {

        // Add configuration file of the local package
        Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ArrayList<StatementResponse> feedLearningRequest(LearningRequest request){
        ArrayList<StatementResponse> responses;
        try {
            request.build();
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            responses = new ArrayList<StatementResponse>();
            responses.add(new StatementResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,false));
            return responses;
        }

        responses = StatementFeeder.feedStatements(request.getSupportStatements().values());
        responses.addAll( StatementFeeder.feedStatements(request.getLeaningStatements().values()));
        responses.addAll( StatementFeeder.feedStatements(request.getDeployStatements().values()));

        return  responses;


    }
    public static Response get(String name, String typeRequest) {
        String retur;
        try {
            if (name == null)
                retur = (new Gson()).toJson(requests.values());
            else if (requests.containsKey(name))
                switch (typeRequest) {
                    case "complete":
                        retur = mapper.writeValueAsString(requests.get(name));
                        break;
                    case "data":
                        retur = mapper.writeValueAsString(requests.get(name).getData());
                        break;
                    case "evaluation":
                        // retur =mapper.writeValueAsString(requests.get(name).getEvaluation());
                        retur = "";
                        break;
                    case "learning":
                        retur = mapper.writeValueAsString((requests.get(name).getLeaningStatements()));
                        break;
                    case "model":
                        retur = mapper.writeValueAsString(requests.get(name).getModel());
                        break;
                    case "deployment":
                        retur = mapper.writeValueAsString((requests.get(name).getDeployStatements()));
                        break;
                    default:
                        retur = mapper.writeValueAsString(requests.get(name));
                }
            else
                return new Response("Error 404 Not Found: Request with name " + name,HttpStatus.NOT_FOUND );

        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            return  new Response("Error 500 Intern Error: Error while executing method " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new  Response(retur, HttpStatus.OK);
    }

    public static Response update(String name, String body, String typeRequest) {
        Object retur = null;
        try {

            if (requests.containsKey(name)) {
                switch (typeRequest) {
                    case "":
                        requests.get(name).reBuild(mapper.readValue(body, LearningRequest.class));
                        retur = requests.get(name);
                        break;
                    case "evaluation":
                        // requests.get(name).getEvaluation().reBuild(mapper.readValue(body,EvaluatorBase.class));
                        //retur = requests.get(name).getEvaluation();
                        break;
                    case "learning":
                        ArrayList<String> learning = (new Gson()).fromJson(body, new TypeToken<ArrayList<String>>() {
                        }.getType());
                        requests.get(name).rebuildLearningStatements(learning);
                        retur = requests.get(name).getLeaningStatements();
                        break;
                    case "model":
                        //  requests.get(name).getModel().reBuild(mapper.readValue(body, Model.class));
                        retur = requests.get(name).getModel();
                        break;

                    case "regression":
                      /*TODO: missing features of regression*/
                        break;

                    case "classify":
                        //  Model mdl =  requests.get(name).getModel();
                        Map input = mapper.readValue(body, new TypeReference<Map<String, Object>>() {
                        });
                        //    retur = requests.get(name).getData().getInstances().attribute(LearningHandler.classify(input, requests.get(name)));
                        break;
                    case "deployment":
                        ArrayList<String> deployed = mapper.readValue(body, new TypeReference<ArrayList<String>>() {
                        });
                        requests.get(name).rebuildDeploymentStatements(deployed);
                        retur = requests.get(name).getDeployStatements();
                    default:
                        requests.get(name).reBuild(mapper.readValue(body, LearningRequest.class));
                        retur = requests.get(name);
                }


            } else {
                loggerService.warn("There is no learning request with name " + name);
                return new Response("Error 404 Not found: There is no request with given name" + name, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            return new Response("Error 500 Intern Error: Error while executing method " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if (retur != null)
            try {

                return new Response(mapper.writeValueAsString(retur), HttpStatus.OK);

            } catch (Exception e) {
                return new Response("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}", HttpStatus.MULTI_STATUS);
            }
        else
            return new Response("{\"message\":\"There was an unknown error!\"}", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    public static Response create(String name, String body, String requestType) {
        ArrayList<StatementResponse> responses=null;
        try {

            switch (requestType) {
                case "":
                    LearningRequest request = mapper.readValue(body, LearningRequest.class);
                    request.setName(name);
                    responses = feedLearningRequest(request);
                    break;
                default:
                    LearningRequest request1 = mapper.readValue(body, LearningRequest.class);
                    request1.setName(name);
                    responses = feedLearningRequest(request1);
            }

        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            return new Response("Error 500 Intern Error: Error while executing method " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if (responses != null)
            try {

                return new Response(mapper.writeValueAsString(responses), HttpStatus.MULTI_STATUS);

            } catch (Exception e) {
                return new Response("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}", HttpStatus.MULTI_STATUS);
            }
        else
            return new Response("{\"message\":\"There was an unknown error!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
    }






}
