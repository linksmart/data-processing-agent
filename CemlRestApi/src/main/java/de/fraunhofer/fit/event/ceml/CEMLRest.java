package de.fraunhofer.fit.event.ceml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.linksmart.api.event.ceml.data.*;
import eu.linksmart.api.event.datafusion.GeneralRequestResponse;

import eu.linksmart.ceml.core.CEML;

import eu.linksmart.ceml.core.CEMLManager;
import eu.almanac.event.datafusion.utils.epl.StatementInstance;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelDeserializer;
import eu.linksmart.api.event.datafusion.MultiResourceResponses;
import eu.linksmart.api.event.datafusion.Statement;

import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import com.google.gson.*;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest extends Component{

    // private Configurator conf = Configurator.getDefaultConfig();
     private Logger loggerService = Utils.initLoggingConf(CEML.class);

    private Map<String, CEMLRequest> requests = new Hashtable<>();
    private ObjectMapper mapper = new ObjectMapper();


    public CEMLRest() {

        super(CEMLRest.class.getSimpleName(), "Provides a REST API for managing the Learning request", "CEML");
        // Add configuration file of the local package


        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // SimpleModule module = new SimpleModule("Model", Version.unknownVersion()).addAbstractTypeMapping(aClass, ModelAutoregressiveNewralNetwork.class);

        //.registerModule(new SimpleModule("Descriptors", Version.unknownVersion()).addAbstractTypeMapping(DataDescriptors.class, DataDefinition.class))
        mapper.registerModule(new SimpleModule("Descriptors", Version.unknownVersion()).addDeserializer(DataDescriptors.class, new DataDescriptorsDeserializer()).addSerializer(DataDescriptors.class, new DataDescriptorSerializer()))
                .registerModule(new SimpleModule("Statements", Version.unknownVersion()).addAbstractTypeMapping(Statement.class, StatementInstance.class))
                .registerModule(new SimpleModule("LearningStatements", Version.unknownVersion()).addAbstractTypeMapping(LearningStatement.class, eu.linksmart.ceml.statements.LearningStatement.class))
                .registerModule(new SimpleModule("Model", Version.unknownVersion()).addDeserializer(Model.class, new ModelDeserializer()))
                .registerModule(new SimpleModule("DataDescriptor", Version.unknownVersion()).addDeserializer(DataDescriptor.class, new DataDescriptorDeserializer()));
    }


    @RequestMapping(value="/ceml", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAll(     ) {

        return prepareHTTPResponse(CEML.get(null, null));
        }

  /*
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
                      //TODO: missing features of regression
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

    }*/

    @RequestMapping(value="/ceml/{name}", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRequest(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"complete"));

    }
    @RequestMapping(value="/ceml/{name}/data", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRequestData(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"data"));
    }
    @RequestMapping(value="/ceml/{name}/evaluation", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getEvaluation(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"evaluation"));
    }
    @RequestMapping(value="/ceml/{name}/learning", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLearning(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"learning"));

    }

    @RequestMapping(value="/ceml/{name}/deployment", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getDeployment(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"deployment"));
    }

    @RequestMapping(value="/ceml/{name}/model", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getModel(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.get(name,"model"));


    }
    @RequestMapping(value="/ceml/{name}/model/prediction", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> classifyWithModel(
            @PathVariable("name") String name
    ){

        return prepareHTTPResponse(CEML.get(name,"prediction"));

    }

    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){



            return prepareHTTPResponse(CEML.create(name, body, ""));

    }
    public <T> ResponseEntity<String>  prepareHTTPResponse( MultiResourceResponses<T> result){
        // preparing pointers
        Object statement =null;
        String statementID =null;
        if(!result.getResources().isEmpty()) {
            statement = result.getHeadResource();
            if(statement instanceof CEMLRequest)
                statementID = ((CEMLRequest)result.getHeadResource()).getName();
        }

        // returning error in case neither an error was produced nor success. This case theoretical cannot happen, if it does there is a program error.
        if(result.getResponses().isEmpty()) {
            result.addResponse(new GeneralRequestResponse("Error",DynamicConst.getId(),statementID, "Agent", "Intern Server Error", 500, "Unknown status"));
            loggerService.error("Impossible state reached");
        }
        // preparing location header
        URI uri= null;
        try {
            if(statement!=null)
                uri = new URI("/ceml/"+statementID);
        } catch (URISyntaxException e) {
            loggerService.error(e.getMessage(),e);

            result.addResponse(new GeneralRequestResponse("Error",DynamicConst.getId(),statementID, "Agent", "Intern Server Error", 500, e.getMessage()));
        }
        // creating HTTP response

        if (result.getResponses().size() == 1 ) {
            if (uri != null)
                return ResponseEntity.status(HttpStatus.valueOf(result.getOverallStatus())).location(uri).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));
            else
                return ResponseEntity.status(HttpStatus.valueOf(result.getOverallStatus())).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));
        }
        if(result.containsSuccess())
            if (uri != null)
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).location(uri).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));
            else
                return ResponseEntity.status(HttpStatus.MULTI_STATUS).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));


        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));

    }
    public  String toJsonString(Object message){

        try {
            return mapper.writeValueAsString(message);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
            return "{\"Error\":\"500\",\"Error Text\":\"Internal Server Error\",\"Message\":\""+e.getMessage()+"\"}";
        }


    }
    /*@RequestMapping(value="/ceml/{name}", method=  RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
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
    }*/



}
