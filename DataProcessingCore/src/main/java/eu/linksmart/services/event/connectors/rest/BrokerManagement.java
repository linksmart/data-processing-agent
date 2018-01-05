package eu.linksmart.services.event.connectors.rest;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 04.01.2018 a researcher of Fraunhofer FIT.
 */
@RestController("broker")
public class BrokerManagement extends Component {

    protected transient Logger loggerService = AgentUtils.initLoggingConf(this.getClass());
    protected transient Configurator conf =  Configurator.getDefaultConfig();
    public BrokerManagement() {
        super(EventRest.class.getSimpleName(), "REST API manage broker configurations", Feeder.class.getSimpleName());

    }
    @ApiOperation(value = "getBrokerConfigurations", nickname = "getBrokerConfigurations")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-conf/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = BrokerConfiguration.class, responseContainer = "Map[String, BrokerConfiguration]"),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> get() {

        try {
            return new ResponseEntity<>(BrokerConfiguration.loadConfigurations(), new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
    @ApiOperation(value = "getBrokerConfiguration", nickname = "getBrokerConfiguration")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-conf/{alias}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = BrokerConfiguration.class),
                    @ApiResponse(code = 404, message = "Not Found", response = String.class ),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> getConf(
            @PathVariable("alias") String alias
    ) {

        try {
            if(!BrokerConfiguration.contains(alias))
                return  new ResponseEntity<>("{\"error\":\"Alias doesn't exist!\", \"code\":404}", new HttpHeaders(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(BrokerConfiguration.loadConfigurations().get(alias), new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
    @ApiOperation(value = "removeBrokerConfiguration", nickname = "removeBrokerConfiguration")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-conf/{alias}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = BrokerConfiguration.class),
                    @ApiResponse(code = 404, message = "Not Found", response = String.class ),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> removeConf(
            @PathVariable("alias") String alias
    ) {

        try {
            if(!BrokerConfiguration.contains(alias))
                return  new ResponseEntity<>("{\"error\":\"Alias doesn't exist!\", \"code\":404}", new HttpHeaders(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(BrokerConfiguration.remove(alias), new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\"}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
    @ApiOperation(value = "getBrokerConfigurations", nickname = "getBrokerConfigurations")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-conf/{alias}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = BrokerConfiguration.class),
                    @ApiResponse(code = 409, message = "Conflict", response = String.class ),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> put(
            @RequestBody BrokerConfiguration configuration,
            @PathVariable("alias") String alias
    ) {

        try {
            if(BrokerConfiguration.contains(alias))
                return  new ResponseEntity<>("{\"error\":\"Alias already exist\", \"code\":409}", new HttpHeaders(), HttpStatus.CONFLICT);
            configuration.setAlias(alias);
            BrokerConfiguration.put(alias, configuration);
            return new ResponseEntity<>(configuration, new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }

    @ApiOperation(value = "getBrokerIncomingConnections", nickname = "getBrokerIncomingConnections")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-connections/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List[String]"),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> getConnections() {

        try {
            Set<String> connection = new HashSet<>();

            MqttIncomingConnectorService.getReference().getListeners().forEach((alias, map)->map.forEach((topic, observer)->connection.add(alias+":"+(observer.getClass().getSimpleName().equals("")?alias.substring(0, 1).toUpperCase() + alias.substring(1)+"MqttObserver":observer.getClass().getSimpleName()))));

            return new ResponseEntity<>(connection, new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
    @ApiOperation(value = "putBrokerIncomingConnections", nickname = "putBrokerIncomingConnections")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-connections/{alias}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = Map.class),
                    @ApiResponse(code = 409, message = "Conflict", response = String.class ),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = String.class )
            }
    )

    public ResponseEntity<?> putConnection(
            @PathVariable("alias") String alias
    ) {

        try {
            if(MqttIncomingConnectorService.getReference().getListeners().containsKey(alias))
                return  new ResponseEntity<>("{\"error\":\"The connection already exist\", \"code\":409}", new HttpHeaders(), HttpStatus.CONFLICT);

            DataProcessingCore.addEventConnection(Arrays.asList(alias));
            Map<String,String> ret = new HashMap<>();
            ret.put("alias",alias);
            return new ResponseEntity<>(ret, new HttpHeaders(), HttpStatus.OK) ;
        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
    @ApiOperation(value = "removeBrokerIncomingConnections", nickname = "removeBrokerIncomingConnections")
    // @RequestMapping(method = RequestMethod.GET, path="/greeting", produces = "application/json")
    @RequestMapping(value = "/broker-connections/{alias}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Success", response = Map.class),
                    @ApiResponse(code = 404, message = "Not Found", response = Map.class ),
                    @ApiResponse(code = 500, message = "Internal Server Error", response = Map.class )
            }
    )

    public ResponseEntity<?> removeConnection(
            @PathVariable("alias") String alias
    ) {

        try {
             MqttIncomingConnectorService.getReference().removeListener(alias);

            Map<String,String> ret = new HashMap<>();
            ret.put("alias",alias);

            if(!MqttIncomingConnectorService.getReference().getListeners().containsKey(alias))
                return new ResponseEntity<>(ret, new HttpHeaders(), HttpStatus.OK) ;
            else
                return new ResponseEntity<>("{\"Accepted\":\"The operation had being performed but there are more connections open that can and should not be removed!\", \"code\":500}", new HttpHeaders(), HttpStatus.ACCEPTED) ;

        } catch (Exception e) {
            return  new ResponseEntity<>("{\"error\":\""+e.getMessage()+"\", \"code\":500}", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR) ;
        }


    }
}
