package eu.almanac.event.datafusion.feeder;

import java.util.HashMap;
import java.util.Map;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.web.bind.annotation.*;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController
public class RestFeeder implements Feeder {
    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<>();
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();
    public RestFeeder(){
        dataFusionWrapperSignIn(EsperEngine.getEngine());
    }
    @Override
    public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {

        dataFusionWrappers.put(dfw.getName(), dfw);

        //TODO: add code for the OSGi future
        return true;
    }

    @Override
    public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {
        dataFusionWrappers.remove(dfw.getName());

        //TODO: add code for the OSGi future
        return true;
    }
    protected String feedStatement( Statement statement) {

        String retur ="";
        if (statement != null) {


            boolean success = true;
            for (DataFusionWrapper i : dataFusionWrappers.values()) {
                try {
                    i.addStatement(statement);

                } catch (StatementException e) {
                    loggerService.error(e.getMessage(), e);
                    retur+=e.getMessage()+"\n";
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    retur+=e.getMessage()+"\n";

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getHash() + " was successful");
                    retur+="Statement " + statement.getHash() + " was successful";
                }
            }

        }
        return retur;
    }

    @Override
    public boolean isDown() {
        return false;
    }
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
/*
    @RequestMapping(value="/statement/{id}", method= RequestMethod.POST)
    public String greeting(@RequestParam(value="statement") EsperQuery statement, @PathVariable("id") String id) {
        return feedStatement(statement);
    }*/
}
