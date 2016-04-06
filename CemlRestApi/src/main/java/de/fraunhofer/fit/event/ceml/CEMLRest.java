package de.fraunhofer.fit.event.ceml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import de.fraunhofer.fit.event.ceml.api.CemlJavaAPI;
import de.fraunhofer.fit.event.ceml.core.CEML;

import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.StatementResponse;

import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import com.google.gson.*;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest extends Component{

    // private Configurator conf = Configurator.getDefaultConfig();
     private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);

    private Map<String, LearningRequest> requests = new Hashtable<>();
    private ObjectMapper mapper = new ObjectMapper();


    public CEMLRest() {

        super(CEMLRest.class.getSimpleName(), "Provides a REST API for managing the Learning request", "CEML");
        // Add configuration file of the local package


        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    @RequestMapping(value="/ceml", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAll(     ) {

        return get(null,null);
        }

    private ResponseEntity<String> get(String name, String typeRequest){
        String retur;
        try {
            if(name ==null)
                retur = (new Gson()).toJson(requests.values());
            else if(requests.containsKey(name))
                switch (typeRequest){
                    case "complete":
                        retur =mapper.writeValueAsString( requests.get(name));
                        break;
                    case "data":
                        retur =mapper.writeValueAsString(requests.get(name).getData());
                        break;
                    case "evaluation":
                       // retur =mapper.writeValueAsString(requests.get(name).getEvaluation());
                        retur="";
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
                return new ResponseEntity<>("Error 404 Not Found: Request with name "+name, HttpStatus.NOT_FOUND);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(retur,HttpStatus.OK);
    }
    private ResponseEntity<String> update(String name, String body, String typeRequest){
        Object retur =null;
        try {

            if(requests.containsKey(name)){
                switch (typeRequest){
                    case "":
                        requests.get(name).reBuild(mapper.readValue(body, LearningRequest.class));
                        retur = requests.get(name);
                        break;
                    case "evaluation":
                       // requests.get(name).getEvaluation().reBuild(mapper.readValue(body,EvaluatorBase.class));
                        //retur = requests.get(name).getEvaluation();
                        break;
                    case "learning":
                        ArrayList<String> learning = (new Gson()).fromJson(body, new TypeToken<ArrayList<String>>(){}.getType());
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
                        Map input = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
                    //    retur = requests.get(name).getData().getInstances().attribute(LearningHandler.classify(input, requests.get(name)));
                        break;
                    case "deployment":
                        ArrayList<String> deployed = mapper.readValue(body, new TypeReference<ArrayList<String>>(){});
                        requests.get(name).rebuildDeploymentStatements(deployed);
                        retur = requests.get(name).getDeployStatements();
                    default:
                        requests.get(name).reBuild(mapper.readValue(body, LearningRequest.class));
                        retur = requests.get(name);
                }



            }else {
                loggerService.warn("There is no learning request with name " + name);
                return new ResponseEntity<>("Error 404 Not found: There is no request with given name"+ name, HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(retur!=null)
            try {

                return new ResponseEntity<>(mapper.writeValueAsString(retur),HttpStatus.OK);

            }catch (Exception e){
                return new ResponseEntity<>("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}",HttpStatus.MULTI_STATUS);
            }
        else
            return new ResponseEntity<>("{\"message\":\"There was an unknown error!\"}",HttpStatus.INTERNAL_SERVER_ERROR);

    }
    private ResponseEntity<String> create(String name, String body, String requestType){
        ArrayList<StatementResponse> retur ;
        try {

            switch (requestType){
                case "":
                    LearningRequest request = mapper.readValue(body, LearningRequest.class);
                    request.setName(name);
                    retur= CemlJavaAPI.feedLearningRequest(request);
                    break;
                default:
                    LearningRequest request1 = mapper.readValue(body,LearningRequest.class);
                    request1.setName(name);
                    retur= CemlJavaAPI.feedLearningRequest(request1);
            }

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("Error 500 Intern Error: Error while executing method "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        if(retur!=null)
            try {

                return new ResponseEntity<>(mapper.writeValueAsString(retur),HttpStatus.ACCEPTED);

            }catch (Exception e){
                return new ResponseEntity<>("{\"message\":\"The process was done correctly. Unfortunately, the finally representation in not available!\"}",HttpStatus.MULTI_STATUS);
            }
        else
            return new ResponseEntity<>("{\"message\":\"There was an unknown error!\"}",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @RequestMapping(value="/ceml/{name}", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRequest(
            @PathVariable("name") String name
    ){

        return get(name,"complete");
    }
    @RequestMapping(value="/ceml/{name}/data", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRequestData(
            @PathVariable("name") String name
    ){
        return get(name, "data");
    }
    @RequestMapping(value="/ceml/{name}/evaluation", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getEvaluation(
            @PathVariable("name") String name
    ){
        return get(name, "evaluation");
    }
    @RequestMapping(value="/ceml/{name}/learning", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLearning(
            @PathVariable("name") String name
    ){
        return get(name,"learning");
    }

    @RequestMapping(value="/ceml/{name}/deployment", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDeployment(
            @PathVariable("name") String name
    ){
        return get(name,"deployment");
    }

    @RequestMapping(value="/ceml/{name}/model", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getModel(
            @PathVariable("name") String name
    ){
        return get(name,"model");
    }
    @RequestMapping(value="/ceml/{name}/model/classify", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> classifyWithModel(
            @PathVariable("name") String name
    ){
        return get(name,"classify");
    }
    @RequestMapping(value="/ceml/{name}/regression", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> predictWithModel(
            @PathVariable("name") String name
    ){
        return get(name,"regression");
    }
    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){



        return create(name,body,"");
    }
    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
      return update(name, body, "");
    }

    @RequestMapping(value="/ceml/{name}/model", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateModel(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name, body, "model");
    }

    @RequestMapping(value="/ceml/{name}/evaluation", method=  RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateEvaluation(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name, body, "evaluation");
    }

    @RequestMapping(value="/ceml/{name}/learning", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateLearning(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name,body,"learning");
    }
    @RequestMapping(value="/ceml/{name}/deployment", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateDeployment(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return update(name,body,"deployment");
    }



}
