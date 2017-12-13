package eu.linksmart.utility.mqtt;

import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Serializer;
import io.swagger.annotations.*;
import org.slf4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController
public class Rest2Mqtt {
    protected static Logger loggerService = Utils.initLoggingConf(Rest2Mqtt.class);
    protected Configurator conf = Configurator.getDefaultConfig();
   // public static String id = UUID.randomUUID().toString();

    static MqttRequestManager requestManager ;
    static {
        try {
            requestManager = new MqttRequestManager();
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            System.exit(-1);
        }
    }
    private static Serializer serializer = new DefaultSerializer();

    @ApiOperation(value = "request", nickname = "request")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "remoteRequest", value = "The request to be proxied", required = false, dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK 200: operation was executed successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 200, message = "Created 201: operation had been deployed successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-response 207: the operation got several successful responses", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: this exact resource already exists in this service", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Bad request: the body presents problems such as syntax or parsing error ", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: either the resource was not found or the one or more servers that provide the resource was not found", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update request", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Bad Request 400: parsing error"),
            @ApiResponse(code = 500, message = "General Error: any internal error. Several messages can be generated here", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: internal service or a server was not available", response = MultiResourceResponses.class)})
    @RequestMapping(value="/mqtt/**", method= RequestMethod.POST, consumes="application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> request(
            @RequestParam(name="n",defaultValue = "1", required = false) int n,
            @RequestParam(name="timeout",defaultValue = "30000", required = false) long timeout,
            @RequestParam(name="targets", required = false) String[] targets,
            @RequestBody(required = false) String remoteRequest, HttpServletRequest  request
    ) {
        MultiResourceResponses<Map> responses = new MultiResourceResponses<>();
        try {
            responses = requestManager.request(
                    ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).replace("/mqtt",""),
                    remoteRequest.getBytes(),
                    n,
                    timeout,
                    targets,
                    null
                    );
        } catch (Exception e) {
            responses.addResponse(createErrorMapMessage(requestManager.id, "Proxy", 400, "Bad Request", e.getMessage()));

        }
        return prepareHTTPResponse(responses);
    }
    public static ResponseEntity<String>  prepareHTTPResponse( MultiResourceResponses<Map> result){
        // preparing pointers
        Map statement =null;



        // returning error in case neither an error was produced nor success. This case theoretical cannot happen, if it does there is a program error.
        if(result.getResponses().isEmpty()) {
            result.addResponse(createErrorMapMessage(requestManager.id, "Proxy", 500, "Intern Server Error", "Unknown status"));
            loggerService.error("Impossible state reached");
        }

        // creating HTTP response
        return finalHTTPCreationResponse(result);
    }
    public static ResponseEntity<String> finalHTTPCreationResponse(MultiResourceResponses result){
        if (result.getResponses().size() == 1 ) {
                return ResponseEntity.status(HttpStatus.valueOf(result.getOverallStatus())).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));
        }
        if(result.containsSuccess())
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));


        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(toJsonString(result));

    }

    public static GeneralRequestResponse createErrorMapMessage(String generatedBy,String producerType,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt, requestManager.id,null,producerType,message,codeNo, "");
    }
    public static GeneralRequestResponse createSuccessMapMessage(String processedBy,String producerType,String id,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt, id,processedBy,producerType,message,codeNo, "");
    }
    public static String toJsonString(Object message){

        try {
            return serializer.toString(message);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
            return "{\"Error\":\"500\",\"Error Text\":\"Internal Server Error\",\"Message\":\""+e.getMessage()+"\"}";
        }


    }
}