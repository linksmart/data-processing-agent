package de.fraunhofer.fit.event.feeder;

import com.google.gson.Gson;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

@RestController
public class RestStatementFeeder implements Feeder {
    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<>();
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();

    protected Gson gson = new Gson();
    public RestStatementFeeder(){

        for(DataFusionWrapper wrapper: DataFusionWrapper.instancedEngines.values())
            dataFusionWrapperSignIn(wrapper);
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
    public ResponseEntity<String> status() {

        return new ResponseEntity<>("{" +
                "\"status\":{" +
                        "\"text\":\"Data-Fusion REST Manager is running!\"," +
                        "\"value\":true" +
                    "}" +
                "}",HttpStatus.OK);
    }
    @RequestMapping(value = "/statement/", method= RequestMethod.GET)
    public ResponseEntity<String> getStatements() {

        Map<String,Map<String,Statement>> statements = new Hashtable<>();
        if( dataFusionWrappers.size()==0)
            return new ResponseEntity<>("Service Unavailable: No Data-Fusion engine has been deployed",HttpStatus.SERVICE_UNAVAILABLE);

        String error="";
        for(DataFusionWrapper dfw:dataFusionWrappers.values())
            try {
                Map<String,Statement> aux =  dfw.getStatements();
                if(!aux.isEmpty()) {
                    statements.put(dfw.getName(),aux);
                }

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                error+=e.getMessage()+"\n";
            }

        if (statements.size()==0 && error.equals("") )
            return new ResponseEntity<>("The provided ID no not exist",HttpStatus.OK);
        else  if(statements.size()== dataFusionWrappers.size())
            return new ResponseEntity<>(gson.toJson(statements),HttpStatus.OK);
        else if(error.equals(""))
            return new ResponseEntity<>(gson.toJson(statements),HttpStatus.MULTI_STATUS);
        else if (!error.equals("")&&statements.size()!=0)
            return new ResponseEntity<>(gson.toJson(statements)+error,HttpStatus.MULTI_STATUS);

        return new ResponseEntity<>("Error 500: "+error,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @RequestMapping(value="/statement/{id}", method= RequestMethod.GET)
    public ResponseEntity<String> getStatement(@PathVariable("id") String id) {
        if(id == null ||id.equals("") )
            return getStatements();
        Map<String,Statement> statements = new Hashtable<>();
        //Type stringStringMap = new TypeToken<Map<String, Statement>>(){}.getType();
        if( dataFusionWrappers.size()==0)
            return new ResponseEntity<>("Service Unavailable: No Data-Fusion engine has been deployed",HttpStatus.SERVICE_UNAVAILABLE);

        String error="";
        for(DataFusionWrapper dfw:dataFusionWrappers.values())
            try {
                Map<String,Statement> aux =  dfw.getStatements();
                if(!aux.isEmpty() && aux.containsKey(id)) {
                    statements.put(dfw.getName(), aux.get(id));
                }

            }
            catch (Exception e) {

                loggerService.error(e.getMessage(),e);
                error+=e.getMessage()+"\n";
            }

        if (statements.size()==0 && error.equals("") )
            return new ResponseEntity<>("The provided ID no not exist",HttpStatus.BAD_REQUEST);
        else  if(statements.size()== dataFusionWrappers.size())
            return new ResponseEntity<>(gson.toJson(statements),HttpStatus.OK);
        else if(error.equals(""))
            return new ResponseEntity<>(gson.toJson(statements),HttpStatus.MULTI_STATUS);
        else if (!error.equals("")&&statements.size()!=0)
            return new ResponseEntity<>(gson.toJson(statements)+error,HttpStatus.MULTI_STATUS);


        return new ResponseEntity<>("Error 500"+error,HttpStatus.MULTI_STATUS);

    }
    private static ResponseEntity<String> getStandardResponse(String engines,String error, String success, int count, int dfwCount){

        if(dfwCount==0)
            return new ResponseEntity<>("There is no CEP engine available to process your request",HttpStatus.SERVICE_UNAVAILABLE);
        else if (count==0 && error.equals("") )
            return new ResponseEntity<>("The provided ID no not exist",HttpStatus.BAD_REQUEST);
        else  if(count== dfwCount && error.equals(""))
            return new ResponseEntity<>("OK 200: Process with Statement ID "+success+" took place successfully in "+String.valueOf(count)+" engines",HttpStatus.OK);
        else if(error.equals(""))
            return new ResponseEntity<>("Multi-status 207: some of the engines did not found the requested ID ("+engines+") with your request",HttpStatus.MULTI_STATUS);
        else if (!error.equals("")&&count!=dfwCount)
            return new ResponseEntity<>("Multi-status 207: some of the engines did not succeed  ("+engines+") with your request. Errors below:\n"+error,HttpStatus.MULTI_STATUS);

        return new ResponseEntity<>("Error 500: "+error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @RequestMapping(value="/statement/add", method= RequestMethod.POST)
    public ResponseEntity<String> addStatement(
            @RequestBody String statementString
    ) {
        if( dataFusionWrappers.size()==0)
            return new ResponseEntity<>("Service Unavailable: No Data-Fusion engine has been deployed",HttpStatus.SERVICE_UNAVAILABLE);
        Statement statement = gson.fromJson(statementString,EPLStatement.class);
        int count =0;
        String engines="",error="";
        for(DataFusionWrapper dfw:dataFusionWrappers.values())
            try {
                if(!dfw.addStatement(statement)) {
                    error += "Ups we have a problem." + "\n";
                }
                count++;
            }catch (StatementException se){
                return new ResponseEntity<>(se.getMessage(),HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                error+=e.getMessage()+"\n";
            }
        return getStandardResponse(engines,error, statement.getHash(),count,dataFusionWrappers.size());
    }

    @RequestMapping(value="/statement/{id}", method= RequestMethod.DELETE)
    public ResponseEntity<String> removeStatement(
            @PathVariable("id") String id
    ) {
        if( dataFusionWrappers.size()==0)
            return new ResponseEntity<>("Service Unavailable: No Data-Fusion engine has been deployed",HttpStatus.SERVICE_UNAVAILABLE);
        int count =0;
        String engines="",error="";

        for(DataFusionWrapper dfw:dataFusionWrappers.values())
            try {
                if (dfw.removeStatement(id))
                    count++;
                else
                    engines+=dfw.getName()+", ";

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
               error+=e.getMessage()+"\n";
            }
       return getStandardResponse(engines,error, id,count,dataFusionWrappers.size());
    }
}
