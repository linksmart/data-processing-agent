package eu.linksmart.services.event.ceml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.ceml.models.serialization.NNDeserialier;
import eu.linksmart.services.event.ceml.models.serialization.NNSerialier;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.api.event.ceml.data.*;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;

import eu.linksmart.services.event.ceml.core.CEML;

import eu.linksmart.api.event.types.impl.StatementInstance;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelDeserializer;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;

import eu.linksmart.services.utils.function.Utils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
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
import java.util.UUID;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest extends Component implements IncomingConnector{

    // private Configurator conf = Configurator.getDefaultConfig();
     private Logger loggerService = Utils.initLoggingConf(CEML.class);

    private Map<String, CEMLRequest> requests = new Hashtable<>();
    private ObjectMapper mapper = CEML.getMapper();


    public CEMLRest() {

        super(CEMLRest.class.getSimpleName(), "Provides a REST API for managing the Learning request", "CEML");
        // Add configuration file of the local package


        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // SimpleModule module = new SimpleModule("Model", Version.unknownVersion()).addAbstractTypeMapping(aClass, ModelAutoregressiveNewralNetwork.class);

        //.registerModule(new SimpleModule("Descriptors", Version.unknownVersion()).addAbstractTypeMapping(DataDescriptors.class, DataDefinition.class))
        mapper.registerModule(new SimpleModule("Descriptors", Version.unknownVersion()).addDeserializer(DataDescriptors.class, new DataDescriptorsDeserializer()).addSerializer(DataDescriptors.class, new DataDescriptorSerializer()))
                .registerModule(new SimpleModule("Statements", Version.unknownVersion()).addAbstractTypeMapping(Statement.class, StatementInstance.class))
                .registerModule(new SimpleModule("LearningStatements", Version.unknownVersion()).addAbstractTypeMapping(LearningStatement.class, eu.linksmart.services.event.ceml.statements.LearningStatement.class))
                .registerModule(new SimpleModule("Model", Version.unknownVersion()).addDeserializer(Model.class, new ModelDeserializer()))
                .registerModule(new SimpleModule("DataDescriptor", Version.unknownVersion()).addDeserializer(DataDescriptor.class, new DataDescriptorDeserializer()))
                .registerModule(new SimpleModule("DNNModel", Version.unknownVersion()).addDeserializer(MultiLayerNetwork.class, new NNDeserialier() ))
                .registerModule(new SimpleModule("SNNModel", Version.unknownVersion()).addSerializer(MultiLayerNetwork.class, new NNSerialier() ));

    }


    @RequestMapping(value="/ceml", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAll(     ) {

        return prepareHTTPResponse(CEML.get(null, null));
        }
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

    @CrossOrigin
    @RequestMapping(value="/ceml/{name}/model/prediction", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> classifyWithModel(
            @PathVariable("name") String name
    ){

        return prepareHTTPResponse(CEML.get(name,"prediction"));

    }

    @RequestMapping(value="/ceml/add", method=  RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @RequestBody() String body
    ){
        return prepareHTTPResponse(CEML.create("CEML_" + UUID.randomUUID().toString().replace("-", "_"), body, ""));
    }
    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @PathVariable("name") String name,
            @RequestBody() String body
    ){
        return prepareHTTPResponse(CEML.create(name, body, ""));
    }
    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteRequest(
            @PathVariable("name") String name
    ){
        return prepareHTTPResponse(CEML.delete(name, ""));
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

    @Override
    public boolean isUp() {
        return true;
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
