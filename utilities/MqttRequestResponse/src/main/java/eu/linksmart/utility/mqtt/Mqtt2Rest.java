package eu.linksmart.utility.mqtt;

import eu.linksmart.api.event.types.impl.AsyncRequest;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.subscription.MqttMessageObserver;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.apache.http.client.fluent.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.12.2017 a researcher of Fraunhofer FIT.
 */
public class Mqtt2Rest implements MqttMessageObserver {
    private transient static final Logger loggerService = Utils.initLoggingConf(Mqtt2Rest.class);
    private transient final static String HTTP_METHOD_STRING = "method",HTTP_HEADERS_STRING = "headers", PATH_SUFFIX ="/rest/", BROKER_PROFILE = "default", SERVICE_WILL="will_message", SERVICE_WILL_TOPIC="will_topic", DEFAULT_TOPIC_STRUCTURE="return_topic_structure",TIMEOUT="timeout";
    private transient static StaticBroker broker;
    private transient static final Configurator conf = Configurator.getDefaultConfig();
    // public static String id = UUID.randomUUID().toString();

    static MqttRequestManager requestManager ;
    static {
        try {
            requestManager = new MqttRequestManager();
            broker = new StaticBroker(
                    BROKER_PROFILE,
                    conf.getString(SERVICE_WILL),
                    conf.getString(SERVICE_WILL_TOPIC)

            );
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            System.exit(-1);
        }
    }
    private static Serializer serializer = new DefaultSerializer();
    private static Deserializer deserializer = new DefaultDeserializer();

    @ApiOperation(value = "request", nickname = "request")
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
    @RequestMapping(value= PATH_SUFFIX +"**", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> get(
            HttpServletRequest request
    ) {
        return process(null,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> post(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> put(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {
        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> delete(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.HEAD, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> head(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.OPTIONS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> option(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> patch(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
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
    @RequestMapping(value=PATH_SUFFIX +"**", method= RequestMethod.TRACE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MultiResourceResponses> trace(
            @RequestBody(required = false) String remoteRequest, HttpServletRequest request
    ) {

        return process(remoteRequest,request);
    }
    private static ResponseEntity<MultiResourceResponses> process(String remoteRequest, HttpServletRequest request){
        MultiResourceResponses<Map> responses = new MultiResourceResponses<>();
        Map<String, Object> properties= new HashMap<>();
        String key;
        properties.put(HTTP_METHOD_STRING, request.getMethod());
        properties.put(HTTP_HEADERS_STRING, new HashMap<String,String>());
        for(Enumeration<String> i = request.getHeaderNames();i.hasMoreElements(); )
            ((Map)properties.get(HTTP_HEADERS_STRING)).put(key=i.nextElement(), request.getHeader(key));


        try {
            responses = requestManager.request(
                    ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)),
                    remoteRequest.getBytes(),
                    1,
                    30000,
                    null,
                    properties
            );
        } catch (Exception e) {
            responses.addResponse(createErrorMapMessage(requestManager.id, "Proxy", 400, "Bad Request", e.getMessage()));

        }
        return prepareHTTPResponse(responses);
    }
    public static ResponseEntity<MultiResourceResponses>  prepareHTTPResponse( MultiResourceResponses<Map> result){
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
    public static ResponseEntity<MultiResourceResponses> finalHTTPCreationResponse(MultiResourceResponses result){
        if (result.getResponses().size() == 1 ) {
            return ResponseEntity.status(HttpStatus.valueOf(result.getOverallStatus())).contentType(MediaType.APPLICATION_JSON).body(result);
        }
        if(result.containsSuccess())
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).contentType(MediaType.APPLICATION_JSON).body(result);


        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(result);

    }

    public static GeneralRequestResponse createErrorMapMessage(String generatedBy, String producerType, int codeNo, String codeTxt, String message){
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

    @Override
    public void update(String topic, MqttMessage message) {

        try {
            AsyncRequest originalRequest = deserializer.deserialize(message.getPayload(), AsyncRequest.class);
            Request request =selectMethod(originalRequest.getProperty(HTTP_METHOD_STRING).toString(),topic.replace(PATH_SUFFIX,""));
            Map<String,String> headers = (Map<String, String>) originalRequest.getProperty(HTTP_HEADERS_STRING);
            if(headers!=null)
                headers.forEach((k,v)->request.addHeader(k,v));

            request.bodyByteArray(originalRequest.getResource());

            Response response = request.execute();

            broker.publish(originalRequest.getReturnEndpoint(),response.returnContent().asBytes());

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private Request selectMethod(String method, String uri){
        Request request;
        switch (method.toUpperCase()){
            case "GET":
                request = Request.Get(uri);
                break;
            case "POST":
                request = Request.Post(uri);
                break;
            case "PUT":
                request = Request.Put(uri);
                break;
            case "DELETE":
                request = Request.Delete(uri);
                break;
            case "PATCH":
                request = Request.Patch(uri);
                break;
            case "OPTIONS":
                request = Request.Options(uri);
                break;
            case "HEAD":
                request = Request.Head(uri);
                break;
            case "TRACE":
                request = Request.Trace(uri);
                break;
            default:
                request = Request.Get(uri);
        }
        return request;
    }
}
