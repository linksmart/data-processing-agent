package de.fraunhofer.fit.event.feeder;

import eu.almanac.event.datafusion.feeder.StatementFeeder;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.codehaus.jackson.map.ObjectMapper;


/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController
public class RestStatementFeeder extends Component implements Feeder {
    protected Map<String, CEPEngine> dataFusionWrappers = new HashMap<>();
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected Configurator conf = Configurator.getDefaultConfig();

    protected ObjectMapper parser =new ObjectMapper();

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

        return StatementFeeder.prepareHTTPResponse(StatementFeeder.getStatements());

    }


    @RequestMapping(value = "/statement/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatement(@PathVariable("id") String id) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.getStatement(id));

    }

    @RequestMapping(value = "/statement/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatement(
            @RequestBody String statementString
    ) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, null,null));

    }

    @RequestMapping(value = "/statement/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatement(
            @RequestBody String statementString,
            @PathVariable("id") String id
    ) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, id,null));

    }
    @RequestMapping(value = "/statement/{cepEngine}/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addStatementIntoCep(
            @RequestBody String statementString,
            @PathVariable("cepEngine") String cepEngine
    ) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, null,cepEngine));

    }

    @RequestMapping(value = "/statement/{cepEngine}/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeStatementIntoCep(
            @RequestBody String statementString,
            @PathVariable("id") String id,
            @PathVariable("cepEngine") String cepEngine
    ) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.addNewStatement(statementString, id,cepEngine));

    }
    @RequestMapping(value = "/statement/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeStatement(
            @PathVariable("id") String id
    ) {
        return StatementFeeder.prepareHTTPResponse(StatementFeeder.deleteStatement(id));

    }

}
