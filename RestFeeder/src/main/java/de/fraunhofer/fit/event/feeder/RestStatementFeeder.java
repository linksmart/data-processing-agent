package de.fraunhofer.fit.event.feeder;

import eu.almanac.event.datafusion.feeder.StatementFeeder;
import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.datafusion.components.CEPEngine;
import eu.linksmart.api.event.datafusion.components.Feeder;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController
public class RestStatementFeeder extends Component implements Feeder {
    protected Map<String, CEPEngine> dataFusionWrappers = new HashMap<>();
    protected static Logger loggerService = Utils.initLoggingConf(RestStatementFeeder.class);
    protected Configurator conf = Configurator.getDefaultConfig();

    protected static ObjectMapper parser =new ObjectMapper();

    public RestStatementFeeder() {
        super(RestStatementFeeder.class.getSimpleName(), "REST API for insert Statements into the CEP Engines", Feeder.class.getSimpleName());
        for (CEPEngine wrapper : CEPEngine.instancedEngines.values())
            dataFusionWrapperSignIn(wrapper);
    }

    @Override
    public boolean dataFusionWrapperSignIn(CEPEngine dfw) {

        dataFusionWrappers.put(dfw.getName(), dfw);

        //TODO: add code for the OSGi future
        return true;
    }

    @Override
    public boolean dataFusionWrapperSignOut(CEPEngine dfw) {
        dataFusionWrappers.remove(dfw.getName());

        //TODO: add code for the OSGi future
        return true;
    }



    @Override
    public boolean isDown() {
        return false;
    }

    @RequestMapping(value = "/statement/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatements() {

        return prepareHTTPResponse(StatementFeeder.getStatements());

    }


    @RequestMapping(value = "/statement/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatement(@PathVariable("id") String id) {
        return prepareHTTPResponse(StatementFeeder.getStatement(id));

    }

    @RequestMapping(value = "/statement/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatement(
            @RequestBody String statementString
    ) {
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, "DP_"+ UUID.randomUUID().toString().replace("-","_"),null));

    }
    @RequestMapping(value = "/statement/add", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createStatement(
            @RequestBody String statementString
    ) {
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, null,null));

    }
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatement(
            @RequestBody String statementString,
            @PathVariable("id") String id
    ) {
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, id,null));

    }
    @RequestMapping(value = "/statement/{cepEngine}/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatementIntoCep(
            @RequestBody String statementString,
            @PathVariable("cepEngine") String cepEngine
    ) {
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, null,cepEngine));

    }

    @RequestMapping(value = "/statement/{cepEngine}/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatementIntoCep(
            @RequestBody String statementString,
            @PathVariable("id") String id,
            @PathVariable("cepEngine") String cepEngine
    ) {
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, id,cepEngine));

    }
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeStatement(
            @PathVariable("id") String id
    ) {
        return prepareHTTPResponse(StatementFeeder.deleteStatement(id));

    }
    public static ResponseEntity<String>  prepareHTTPResponse( MultiResourceResponses<Statement> result){
        // preparing pointers
        Statement statement =null;

        if(!result.getResources().isEmpty())
            statement = result.getResources().values().iterator().next();

        // Preparing sync response
        if(statement!=null && statement.getStateLifecycle()== Statement.StatementLifecycle.SYNCHRONOUS && result.getOverallStatus()<400) {
            try {

                statement.getSynchronousResponse();
                result.addResponse(createSuccessMapMessage(statement.getID(), "Statement", statement.getID(), 200, "OK", "Statement Processed"));

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 500, "Intern Server Error", e.getMessage()));
            }

        }

        // returning error in case neither an error was produced nor success. This case theoretical cannot happen, if it does there is a program error.
        if(result.getResponses().isEmpty()) {
            result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 500, "Intern Server Error", "Unknown status"));
            loggerService.error("Impossible state reached");
        }
        // preparing location header
        URI uri= null;
        try {
            if(statement!=null)
                uri = new URI("/statement/"+statement.getID());
        } catch (URISyntaxException e) {
            loggerService.error(e.getMessage(),e);
            result.addResponse(createErrorMapMessage(DynamicConst.getId(),"Agent",500,"Intern Server Error",e.getMessage()));
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

    public static GeneralRequestResponse createErrorMapMessage(String generatedBy,String producerType,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt,DynamicConst.getId(),null,producerType,message,codeNo, "");
    }
    public static GeneralRequestResponse createSuccessMapMessage(String processedBy,String producerType,String id,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt,DynamicConst.getId(),processedBy,producerType,message,codeNo, "");
    }
    public static String toJsonString(Object message){

        try {
            return parser.writeValueAsString(message);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
            return "{\"Error\":\"500\",\"Error Text\":\"Internal Server Error\",\"Message\":\""+e.getMessage()+"\"}";
        }


    }
}
