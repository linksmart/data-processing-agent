package de.fraunhofer.fit.event.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.datafusion.components.CEPEngine;
import eu.linksmart.api.event.datafusion.components.Feeder;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 24.08.2015 a researcher of Fraunhofer FIT.
 */
@RestController
public class RestEventFeeder extends Component implements Feeder {
    protected Map<String,CEPEngine> dataFusionWrappers = new HashMap<>();
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();

    private ObjectMapper mapper = new ObjectMapper();

    public RestEventFeeder() {
        super(RestEventFeeder.class.getSimpleName(),"REST API for insert HTTP Events into the CEP Engines", Feeder.class.getSimpleName());
        for(CEPEngine wrapper: CEPEngine.instancedEngines.values())
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

    @RequestMapping(value="event/{federation}/{pi}/{ver}/observation/{thingId}/{streamId}", method= RequestMethod.POST, consumes="application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addObservation(
            @RequestBody String rawEvent,
            @PathVariable("federation") String federation,
            @PathVariable("pi") String pi,
            @PathVariable("ver") String version,
            @PathVariable("thingId") String thingId,
            @PathVariable("streamId") String streamId
    ) {
        if( dataFusionWrappers.size()==0)
            return new ResponseEntity<>("Service Unavailable: No Data-Fusion engine has been deployed", HttpStatus.SERVICE_UNAVAILABLE);
        Observation event;
        int count =0;
        String engines="",error="";
        try {
            if(mapper==null)
                mapper = new ObjectMapper();

            //try {

            event = mapper.readValue(rawEvent,Observation.class);
            event.setId(thingId);
        }catch(Exception e){
            loggerService.error(e.getMessage(),e);
            return new ResponseEntity<>("The Observation sent is not OGC SensorThing compliant",HttpStatus.BAD_REQUEST);
        }
        for (CEPEngine i : dataFusionWrappers.values())
            try {

                if(i.addEvent(federation+"/"+pi+"/"+version+"/observation/"+thingId+"/"+streamId, event, event.getClass()))
                    count++;
                else
                    engines+=i.getName()+", ";

            }catch (Exception e){
                loggerService.error(e.getMessage(),e);
                error+=e.getMessage()+"\n";
                engines+=i.getName()+", ";

            }
        return getStandardResponse(engines,error, thingId,count,dataFusionWrappers.size());
    }
    private static ResponseEntity<String> getStandardResponse(String engines,String error, String success, int count, int dfwCount){

        if (count==0 && error.equals("") )
            return new ResponseEntity<>("The provided ID no not exist",HttpStatus.BAD_REQUEST);
        else  if(count== dfwCount)
            return new ResponseEntity<>("OK 200: Process with Statement ID "+success+" took place successfully in "+String.valueOf(count)+" engines",HttpStatus.OK);
        else if(error.equals(""))
            return new ResponseEntity<>("Multi-status 207: some of the engines did not found the requested ID ("+engines+") with your request",HttpStatus.MULTI_STATUS);
        else if (!error.equals("")&&count!=0)
            return new ResponseEntity<>("Multi-status 207: some of the engines did not succeed  ("+engines+") with your request. Errors below:\n"+error,HttpStatus.MULTI_STATUS);

        return new ResponseEntity<>("Error 500"+error,HttpStatus.MULTI_STATUS);
    }

}
