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
    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<>();
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();
    protected ArrayList<String> filePaths = new ArrayList<>();
    private static Gson gson = new Gson();
    public PersistenceFeeder(){

    }
    public PersistenceFeeder(String... filePaths){

        loadFiles(filePaths);

    }
    void loadFiles(String... files){
        for (String f: files)
            if(filePaths.contains(f)){
                loadFile(f);
            }
    }
    void loadFile(String filePath){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);


            String everything = IOUtils.toString(inputStream);

            feed(everything);
        } catch (IOException e) {
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
    protected void feed(String rawData){
        PersistentBean persistentBean= gson.fromJson(rawData, PersistentBean.class);

        if(persistentBean!=null) {
            if (persistentBean.getStatements() != null && !persistentBean.getStatements().isEmpty()) {
                for (Statement stm : persistentBean.getStatements()) {
                    feedStatement(stm);
                }
            }
            if (persistentBean.getObservations() != null && !persistentBean.getObservations().isEmpty()) {
                for (String topic : persistentBean.getObservations().keySet()) {
                    feedEvent(topic,persistentBean.getObservations(topic));
                }
            }
        }


    }
    protected void  feedEvent(String topic,Observation observation){
        for (DataFusionWrapper i : dataFusionWrappers.values())
            i.addEvent(topic, observation, observation.getClass());

    }

    protected void feedStatement( Statement statement) {

        if (statement != null) {


            boolean success = true;
            for (DataFusionWrapper i : dataFusionWrappers.values()) {
                try {
                    i.addStatement(statement);

                } catch (StatementException e) {
                    loggerService.error(e.getMessage(), e);
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);

                    success = false;
                }
                if (success)
                    loggerService.info("Statement " + statement.getHash() + " was successful");

            }

        }
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


    @Override
    public boolean isDown() {
        return false;
    }

    @Override
    public boolean subscribeToTopic(String topic) {
        return false;
    }
}
