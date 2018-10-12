package eu.linksmart.services.event.connectors.rest;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.feeders.EventFeeder;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by José Ángel Carvajal on 04.01.2018 a researcher of Fraunhofer FIT.
 */
@RestController("event")
public class EventRest extends Component implements IncomingConnector {
    public EventRest() {
        super(EventRest.class.getSimpleName(), "REST API for insert Events into the CEP Engines", Feeder.class.getSimpleName());


    }
    @ApiOperation(value = "addEvent", nickname = "addEvent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", value = "Event's topic as is define in the configuration", required = true, dataType = "string", paramType = "path")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK 200: event had being fed", response = MultiResourceResponses.class),
            @ApiResponse(code = 400, message = "Bad Request 400: parsing error"),
            @ApiResponse(code = 500, message = "General Error: Any internal error produced by the engine. Usually uncontrolled/unexpected errors"),
            @ApiResponse(code = 503, message = "Service Unavailable: No CEP engine found to deploy statement")})
    @RequestMapping(value="/event/**", method= RequestMethod.POST, consumes="application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addEvent(
            @RequestBody String rawEvent, HttpServletRequest request
    ) {
        // todo update this this API to return MultiResourceResponses
        if(  CEPEngine.instancedEngines.size()==0)
            return new ResponseEntity<>("Service Unavailable: No CEP engine has been deployed", HttpStatus.SERVICE_UNAVAILABLE);
        try {
            EventFeeder.getFeeder().feed(
                    (((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))).replace("/event",""),
                    rawEvent);
        } catch (StatementException e) {
            return new ResponseEntity<>("Bad Request 400: parsing error:"+e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (TraceableException | UntraceableException e) {
            return new ResponseEntity<>("Internal Server Error 500:",HttpStatus.OK);
        }
        return new ResponseEntity<>("OK 200: event had being fed",HttpStatus.OK);
    }

    @Override
    public boolean isUp() {
        return true;
    }
}
