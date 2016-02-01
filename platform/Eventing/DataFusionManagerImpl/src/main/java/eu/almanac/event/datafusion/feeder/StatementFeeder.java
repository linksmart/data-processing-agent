package eu.almanac.event.datafusion.feeder;

import eu.linksmart.api.event.datafusion.StatementResponse;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by angel on 26/11/15.
 */
public class StatementFeeder implements Feeder {
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(StatementFeeder.class);
    protected static Configurator conf =  Configurator.getDefaultConfig();


    private StatementFeeder() {
       // super(StatementFeeder.class.getSimpleName(), "Provide intern infrastructure to feed the CEP with learning DF statements ", Feeder.class.getSimpleName(),"CEML");
    }

    @Override
    public boolean dataFusionWrapperSignIn(CEPEngine dfw) {
        return false;
    }

    @Override
    public boolean dataFusionWrapperSignOut(CEPEngine dfw) {
        return false;
    }

    @Override
    public boolean isDown() {
        return true;
    }
    public static ArrayList<StatementResponse> feedStatements(Collection<Statement> statements) {
        boolean success =true;
        String retur="";
        ArrayList<StatementResponse> response = new ArrayList<>();
            for (Statement statement : statements) {
                ArrayList<StatementResponse> aux =feedStatement(statement);
                response.addAll(aux);
            }

        return response;
    }
    public static ArrayList<StatementResponse> feedStatement(Statement statement){
        boolean success =true;


        ArrayList<StatementResponse> response = new ArrayList<>();
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {

                try {
                   if( !dfw.addStatement(statement))
                       response.add(new StatementResponse("Unexpected error in statement " + statement.getName() +" with hash "+ statement.getHash()+ " in engine "+dfw.getName(), HttpStatus.INTERNAL_SERVER_ERROR,false));

                } catch (StatementException e) {

                    loggerService.error(e.getMessage(), e);
                    response.add(new StatementResponse(e.getMessage(), HttpStatus.BAD_REQUEST,e.getErrorTopic(),false));


                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    response.add(new StatementResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,false));

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getHash() + " was successful");
                    response.add(new StatementResponse("Statement " + statement.getName() +" with hash "+ statement.getHash()+ " in engine "+dfw.getName()+" was processed successfully", HttpStatus.OK,true));

                }

        }
        return response;
    }
    static public  ArrayList<StatementResponse> pauseStatements(Collection<Statement> statements) {

        ArrayList<StatementResponse> response = new ArrayList<>();


        for (Statement statement : statements) {
            ArrayList<StatementResponse> aux =pauseStatement(statement);
            response.addAll(aux);
        }

        return response;
    }
    static public  ArrayList<StatementResponse> pauseStatement(Statement statement){

        ArrayList<StatementResponse> response = new ArrayList<>();
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {


            ArrayList<StatementResponse> aux =pauseStatement(statement.getHash());
            response.addAll(aux);

        }
        return response;
    }
    static public  ArrayList<StatementResponse> pauseStatement(String hash){
        return changeStatement(hash,Statement.StatementLifecycle.PAUSE);
    }

    static public  ArrayList<StatementResponse> removeStatements(Collection<Statement> statements){
          ArrayList<StatementResponse> response = new ArrayList<>();


        for (Statement statement : statements) {
            ArrayList<StatementResponse> aux =removeStatement(statement);
            response.addAll(aux);
        }

        return response;
    }
    static public  ArrayList<StatementResponse> removeStatement(Statement statement){

        ArrayList<StatementResponse> response = new ArrayList<>();
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {


            ArrayList<StatementResponse> aux =removeStatement(statement.getHash());
            response.addAll(aux);

        }
        return response;
    }
    static public  ArrayList<StatementResponse> removeStatement(String hash){
       return changeStatement(hash,Statement.StatementLifecycle.REMOVE);
    }
   static public  ArrayList<StatementResponse> startStatements(Collection<Statement> statements){
       ArrayList<StatementResponse> response = new ArrayList<>();


            for (Statement statement : statements) {
                ArrayList<StatementResponse> aux =startStatement(statement);
                response.addAll(aux);
            }

        return response;
    }
    static public  ArrayList<StatementResponse> startStatement(Statement statement){
        ArrayList<StatementResponse> response = new ArrayList<>();
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {


            ArrayList<StatementResponse> aux =startStatement(statement.getHash());
            response.addAll(aux);

        }
        return response;
    }
    static public  ArrayList<StatementResponse> startStatement(String hash){
        return changeStatement(hash, Statement.StatementLifecycle.RUN);
    }
    static public  ArrayList<StatementResponse> changeStatement(String hash, Statement.StatementLifecycle action){
        boolean success =true;
        ArrayList<StatementResponse> response = new ArrayList<>();
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {

            try {
                boolean res= false;
                switch (action){
                    case RUN:
                        res=dfw.startStatement(hash);
                        break;
                    case PAUSE:
                        res=dfw.pauseStatement(hash);
                        break;
                    case REMOVE:
                        res=dfw.removeStatement(hash);
                        break;
                }
                if(res)
                    response.add(new StatementResponse("Unexpected error in statement with hash "+ hash+ " in engine "+dfw.getName(), HttpStatus.INTERNAL_SERVER_ERROR,false));

            } catch (StatementException e) {
                loggerService.error(e.getMessage(), e);
                response.add(new StatementResponse(e.getMessage(), HttpStatus.BAD_REQUEST,e.getErrorTopic(),false));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                response.add(new StatementResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,false));
                success = false;
            }
            if (success) {
                loggerService.info("Statement " + hash + " was successful");
                response.add(new StatementResponse("Statement  with hash "+ hash+ " in engine "+dfw.getName()+" was processed successfully", HttpStatus.OK,true));
            }

        }
        return response;
    }


}
