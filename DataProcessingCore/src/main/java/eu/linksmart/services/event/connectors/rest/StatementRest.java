package eu.linksmart.services.event.connectors.rest;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.utils.configuration.Configurator;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController("statement")
public class StatementRest extends Component implements IncomingConnector {
    protected static Logger loggerService = LogManager.getLogger(StatementRest.class);
    protected Configurator conf = Configurator.getDefaultConfig();


    public StatementRest() {
        super(StatementRest.class.getSimpleName(), "REST API for insert Statements into the CEP Engines", Feeder.class.getSimpleName());


    }

    @ApiOperation(value = "getStatements", nickname = "getStatements")
   // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/statement/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success", response = MultiResourceResponses.class)})
    public ResponseEntity<String> getStatements() {

        return prepareHTTPResponse(StatementFeeder.getStatements());

    }

    @ApiOperation(value = "getStatement", nickname = "getStatement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Statement's ID", required = true, dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatement(@PathVariable("id") String id) {
        return prepareHTTPResponse(StatementFeeder.getStatement(id));

    }
    @ApiOperation(value = "getStatementLastOutput", nickname = "getStatementLastOutput")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Statement's ID", required = true, dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{id}/output", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatementLastOutput(@PathVariable("id") String id) {
        return finalHTTPCreationResponse(StatementFeeder.getStatementLastOutput(id),null);

    }
    @ApiOperation(value = "addStatement", nickname = "addStatement")
    @ApiImplicitParams({
           // @ApiImplicitParam(name = "ID", value = "Statement's ID (Default auto-generated)", required = false, dataType = "string", paramType = "body")//,
           // @ApiImplicitParam(name = "name", value = "Statement's name", required = true, dataType = "string", paramType = "body"),
           // @ApiImplicitParam(name = "statement", value = "Statement's query", required = true, dataType = "string", paramType = "body"),
           // @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
           // @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
           // @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
           // @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
           // @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
           // @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
           // @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatement(
            @RequestBody StatementInstance statement
    ) {
        //return prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, "DP_"+ UUID.randomUUID().toString().replace("-","_"),null));
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();
        result.addResources(statement.getId(),statement);
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statement, statement.getId(),null,result));

    }

    @ApiOperation(value = "executeStatement", nickname = "executeStatement")
    @ApiImplicitParams({
            // @ApiImplicitParam(name = "ID", value = "Statement's ID (Default auto-generated)", required = false, dataType = "string", paramType = "body")//,
            // @ApiImplicitParam(name = "name", value = "Statement's name", required = true, dataType = "string", paramType = "body"),
            // @ApiImplicitParam(name = "statement", value = "Statement's query", required = true, dataType = "string", paramType = "body"),
            // @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
            // @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
            // @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
            // @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
            // @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
            // @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
            // @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
            // @ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/execute/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> executeStatement(
            @RequestBody String statement
    ) {

        return prepareHTTPResponse(StatementFeeder.executeStatement(statement));

    }
    @ApiOperation(value = "createStatement", nickname = "createStatement")
    @ApiImplicitParams({
          /*  @ApiImplicitParam(name = "ID", value = "Statement's ID", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "Statement's name", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "statement", value = "Statement's query", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
            @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")*/
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created: Statement 'id' was successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 201, message = "Created: Statement 'id' was successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
            // @ApiResponse(code = 304, message = "Not Modified: Resource is identically to the update. No changes had being made", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
            // @ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            /*@ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),*/
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createStatement(
            @RequestBody StatementInstance statement
    ) {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();

        result.addResources(statement.getId(),statement);
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statement, statement.getId(),null,result));

    }
    @ApiOperation(value = "changeStatement", nickname = "changeStatement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ID", value = "Statement's ID", required = false, dataType = "string", paramType = "body")/*,
           @ApiImplicitParam(name = "ID", value = "Statement's ID", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "name", value = "Statement's name", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "statement", value = "Statement's query", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
            @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")*/
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK: Statement 'id' was successful (in case of an update of an existing statement)", response = MultiResourceResponses.class),
            @ApiResponse(code = 201, message = "Created: Statement 'id' was successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
           // @ApiResponse(code = 304, message = "Not Modified: Resource is identically to the update. No changes had being made", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
          //  @ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
          //  @ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatement(
            @RequestBody StatementInstance statement,
            @PathVariable("id") String id
    ) {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();
        result.addResources(statement.getId(),statement);
        statement.setId(id);
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statement, statement.getId(),null,result));

    }
    @ApiOperation(value = "addStatementInEngine", nickname = "addStatementInEngine")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cepEngine", value = "Target engine of for the statement", required = false, dataType = "string", paramType = "path")/*,
            @ApiImplicitParam(name = "name", value = "Statement's name", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "statement", value = "Statement's query", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
            @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")*/
    })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID or cepEngine doesn't exists \n The cep engine named 'cepEngine' doesn't exists", response = MultiResourceResponses.class),
          //  @ApiResponse(code = 404, message = "The cep engine named 'cepEngine' doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{cepEngine}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatementIntoCep(
            @RequestBody StatementInstance statement,
            @PathVariable("cepEngine") String cepEngine
    ) {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();
        result.addResources(statement.getId(),statement);
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statement, statement.getId(),cepEngine,result));


    }
    @ApiOperation(value = "changeStatementIntoCep", nickname = "changeStatementIntoCep")
    @ApiImplicitParams({
          //  @ApiImplicitParam(name = "ID", value = "Statement's ID", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "ID", value = "Statement's ID", required = true, dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "cepEngine", value = "Target CEP engine for the statement ", required = true, dataType = "string", paramType = "path"),
            /*@ApiImplicitParam(name = "name", value = "Statement's name", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "statement", value = "Statement's query", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "source", value = "Statement's source of events (DEPRECATED)", required = false, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "input", value = "Statement's input path for the event sources (DEPRECATED)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "output", value = "Statement's output of the compound events. Result of the streams generated by the statement property (Default /outgoing/'statementId'/'agentID')", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "CEHandler", value = "Statement's CEHandler that will process the handler (Default eu.linksmart.services.event.handler.ComplexEventHandler)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "StateLifecycle", value = "Statement's Lifecycle (Default RUN)", required = false, dataType = "[\"RUN\"|\"ONCE\"|\"SYNCHRONOUS\"|\"PAUSE\"|\"REMOVE\"]", paramType = "body"),
            @ApiImplicitParam(name = "scope", value = "Statement's scope of the outputs (Default tcp://localhost:1883)", required = false, dataType = "string[]", paramType = "body"),
            @ApiImplicitParam(name = "isRestOutput", value = "Indicates if the statement's Scope and Output are REST endpoints", required = false, dataType = "boolean", paramType = "body")
    */})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK: Statement 'id' was successful (in case of an update of an existing statement)", response = MultiResourceResponses.class),
            @ApiResponse(code = 201, message = "Created: Statement 'id' was successful", response = MultiResourceResponses.class),
            @ApiResponse(code = 207, message = "Multi-status", response = MultiResourceResponses.class),
            @ApiResponse(code = 304, message = "Not Modified: This exact statement already exists in this agent", response = MultiResourceResponses.class),
          //  @ApiResponse(code = 304, message = "Not Modified: Resource is identically to the update. No changes had being made", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Syntax error: 'reason'", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Not Found: The given ID doesn't exists", response = MultiResourceResponses.class),
           // @ApiResponse(code = 404, message = "The cep engine named 'cepEngine' doesn't exists", response = MultiResourceResponses.class),
            @ApiResponse(code = 409, message = "Conflict: The id sent in the request exists already. If want to be updated make an update/PUT request", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: The statement-property 'source' is not available in this agent version", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: 'CEP-engine that produce exception' Oops we have a problem", response = MultiResourceResponses.class),
            //@ApiResponse(code = 500, message = "General Error: Unknown Source Intern Server Error", response = MultiResourceResponses.class),
           // @ApiResponse(code = 500, message = "General Error: Unknown Status", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{cepEngine}/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatementIntoCep(
            @RequestBody StatementInstance statement,
            @PathVariable("id") String id,
            @PathVariable("cepEngine") String cepEngine
    ) {

        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();
        statement.setId(id);
        result.addResources(statement.getId(),statement);
        return prepareHTTPResponse(StatementFeeder.addNewStatement(statement, statement.getId(),cepEngine,result));

    }
    @ApiOperation(value = "removeStatement", nickname = "removeStatement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Statement's ID", required = true, dataType = "string", paramType = "path")
    //        ,@ApiImplicitParam(name = "statement", value = "Statement's ID", required = false, dataType = "string", paramType = "body")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = MultiResourceResponses.class),
    //        @ApiResponse(code = 400, message = "The delete statement has a syntax error", response = MultiResourceResponses.class),
            @ApiResponse(code = 404, message = "Provided ID doesn't exist in any CEP engine. ID:", response = MultiResourceResponses.class),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors", response = MultiResourceResponses.class),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement", response = MultiResourceResponses.class)})
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeStatement(
            @PathVariable("id") String id
      //      ,@RequestBody String statement
    ) {
        return prepareHTTPResponse(StatementFeeder.deleteStatement(id,/*statement*/null));

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
                result.addResponse(createSuccessMapMessage(statement.getId(), "Statement", statement.getId(), 200, "OK", "Statement Processed",statement.getOutput()));

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                result.addResponse(createErrorMapMessage(SharedSettings.getId(), "Agent", 500, "Intern Server Error", e.getMessage()));
            }

        }

        // returning error in case neither an error was produced nor success. This case theoretical cannot happen, if it does there is a program error.
        if(result.getResponses().isEmpty()) {
            result.addResponse(createErrorMapMessage(SharedSettings.getId(), "Agent", 500, "Intern Server Error", "Unknown status"));
            loggerService.error("Impossible state reached");
        }
        // preparing location header
        URI uri= null;
        try {
            if(statement!=null)
                uri = new URI("/statement/"+statement.getId());
        } catch (URISyntaxException e) {
            loggerService.error(e.getMessage(),e);
            result.addResponse(createErrorMapMessage(SharedSettings.getId(),"Agent",500,"Intern Server Error",e.getMessage()));
        }
        // creating HTTP response
        return finalHTTPCreationResponse(result,uri);
    }
    public static ResponseEntity<String> finalHTTPCreationResponse(MultiResourceResponses result,URI uri){
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
        return new GeneralRequestResponse(codeTxt, SharedSettings.getId(),null,producerType,message,codeNo);
    }
    public static GeneralRequestResponse createSuccessMapMessage(String processedBy,String producerType,String id,int codeNo, String codeTxt,String message,List<String> output){
        return new GeneralRequestResponse(codeTxt, SharedSettings.getId(),processedBy,producerType,message,codeNo, output);
    }
    public static String toJsonString(Object message){

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
}
