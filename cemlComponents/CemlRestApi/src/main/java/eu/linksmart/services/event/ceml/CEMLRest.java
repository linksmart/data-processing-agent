package eu.linksmart.services.event.ceml;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;

import eu.linksmart.services.event.ceml.core.CEML;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;

import eu.linksmart.services.utils.function.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.List;
import java.util.ArrayList;

/**
 * Created by angel on 13/11/15.
 */
@RestController
public class CEMLRest extends Component implements IncomingConnector{

    // private Configurator conf = Configurator.getDefaultConfig();
     private Logger loggerService = LogManager.getLogger(CEML.class);

    private Map<String, CEMLRequest> requests = new Hashtable<>();


    public CEMLRest() {

        super(CEMLRest.class.getSimpleName(), "Provides a REST API for managing the Learning request", "CEML");


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

    @RequestMapping(value="/ceml/", method=  RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @RequestBody() String body
    ){
        return prepareHTTPResponse(CEML.create("CEML_" + UUID.randomUUID().toString().replace("-", "_"), body, ""));
    }
    @RequestMapping(value="/ceml/{name}", method=  RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRequest(
            @PathVariable("name") String name,
            @RequestBody CEMLManager cemlRequest
    ){
        cemlRequest.setName(name);
        return prepareHTTPResponse(CEML.create(cemlRequest));
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
            List<String> topics = new ArrayList<String>();
            topics.add("Unknown status");
            result.addResponse(new GeneralRequestResponse("Error", SharedSettings.getId(),statementID, "Agent", "Intern Server Error", 500, topics));
            loggerService.error("Impossible state reached");
        }
        // preparing location header
        URI uri= null;
        try {
            if(statement!=null)
                uri = new URI("/ceml/"+statementID);
        } catch (URISyntaxException e) {
            loggerService.error(e.getMessage(),e);
            List<String> topics = new ArrayList<String>();
            topics.add(e.getMessage());
            result.addResponse(new GeneralRequestResponse("Error", SharedSettings.getId(),statementID, "Agent", "Intern Server Error", 500, topics));
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
            return SharedSettings.getSerializer().toString(message);
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
