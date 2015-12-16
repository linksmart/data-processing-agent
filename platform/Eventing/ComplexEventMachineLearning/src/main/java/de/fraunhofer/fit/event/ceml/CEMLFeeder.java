package de.fraunhofer.fit.event.ceml;

import de.fraunhofer.fit.event.ceml.type.requests.builded.LearningRequest;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.Collection;

/**
 * Created by angel on 26/11/15.
 */
public class CEMLFeeder extends Component implements Feeder {
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(CEMLFeeder.class);
    protected static Configurator conf =  Configurator.getDefaultConfig();

    public CEMLFeeder() {
        super(CEMLFeeder.class.getSimpleName(), "Provide intern infrastructure to feed the CEP with learning DF statements ", Feeder.class.getSimpleName(),"CEML");
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
    public static String feedStatements(Collection<Statement> statements){
        boolean success =true;
        String retur="";
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {
            for (Statement statement : statements) {
                try {
                    dfw.addStatement(statement);

                } catch (StatementException e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getHash() + " was successful");
                    retur += "Statement " + statement.getName() + " was successful";
                }
            }
        }
        return retur;
    }
    public static String feedLearningRequest(LearningRequest request){
        try {
            request.build();
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return e.getMessage();
        }

        String retur ="";

         return feedStatements(request.getLeaningStatements().values()) + "\n"+ feedStatements(request.getDeployStatements().values()) ;


    }
    static public String pauseStatements(Collection<Statement> statements){
        boolean success =true;
        String retur="";
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {
            for (Statement statement : statements) {
                try {
                    dfw.pauseStatement(statement.getHash());

                } catch (StatementException e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getHash() + " was successful");
                    retur += "Statement " + statement.getName() + " was successful";
                }
            }
        }
        return retur;
    }
   static public String startStatements(Collection<Statement> statements){
        boolean success =true;
        String retur="";
        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {
            for (Statement statement : statements) {
                try {
                    dfw.startStatement(statement.getHash());

                } catch (StatementException e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    retur += e.getMessage() + "\n";

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getHash() + " was successful");
                    retur += "Statement " + statement.getName() + " was successful";
                }
            }
        }
        return retur;
    }

}
