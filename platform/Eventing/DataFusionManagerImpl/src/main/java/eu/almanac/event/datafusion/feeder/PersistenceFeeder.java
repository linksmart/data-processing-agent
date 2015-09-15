package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import eu.almanac.event.datafusion.feeder.type.PersistentBean;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class PersistenceFeeder implements Feeder, EventFeederLogic {
    static protected LoggerService loggerService = Utils.initDefaultLoggerService(PersistenceFeeder.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    protected ArrayList<String> filePaths = new ArrayList<>();
    private static Gson gson = new Gson();
    private PersistenceFeeder(){

    }
    public PersistenceFeeder(String... filePaths){

        for(String f: filePaths)
            this.filePaths.add(f);

    }
    private void loadFiles(DataFusionWrapper dfw){
        for (String f: filePaths)
                loadFile(f,dfw);

    }
    static void loadFile(String filePath, DataFusionWrapper dfw){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);


            String everything = IOUtils.toString(inputStream);

            feed(everything, dfw);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                loggerService.error(e.getMessage(), e);
            }
        }
    }
    static protected void feed(String rawData,DataFusionWrapper dfw){
        PersistentBean persistentBean= gson.fromJson(rawData, PersistentBean.class);

        if(persistentBean!=null) {
            if (persistentBean.getStatements() != null && !persistentBean.getStatements().isEmpty()) {
                for (Statement stm : persistentBean.getStatements()) {
                    feedStatement(stm,dfw);
                }
            }
            if (persistentBean.getObservations() != null && !persistentBean.getObservations().isEmpty()) {
                for (String topic : persistentBean.getObservations().keySet()) {
                    dfw.addEvent(topic, persistentBean.getObservations(topic), persistentBean.getObservations(topic).getClass());

                }
            }
        }


    }

    static protected void feedStatement( Statement statement, DataFusionWrapper dfw) {

        if (statement != null) {

            try {
                dfw.addStatement(statement);
                loggerService.info("Statement " + statement.getHash() + " was successful");
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);

            }


        }
    }

    @Override
    public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {
        loadFiles(dfw);
        //TODO: add code for the OSGi future
        return true;
    }

    @Override
    public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {

        //TODO: add code for the OSGi future
        return true;
    }


    @Override
    public boolean isDown() {
        return false;
    }

    @Override
    public boolean subscribeToTopic(String topic) {
        return false;
    }
}
