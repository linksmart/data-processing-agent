package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.event.datafusion.feeder.type.PersistentBean;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;

import eu.linksmart.gc.utils.configuration.Configurator;
import org.slf4j.Logger;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class PersistenceFeeder extends Component implements Feeder, EventFeederLogic {
    static protected Logger loggerService = Utils.initLoggingConf(PersistenceFeeder.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    protected List<String> filePaths = new ArrayList<>();
    private static ObjectMapper mapper = new ObjectMapper();

    public PersistenceFeeder(String... filePaths){
        super(PersistenceFeeder.class.getSimpleName(),"Feeder that inserts statements and Events at loading time", Feeder.class.getSimpleName());

        for(String f: filePaths)
            if(f!=null&&!f.equals(""))
                this.filePaths.add(f);

    }
    private void loadFiles(CEPEngine dfw){
        for (String f: filePaths)
                loadFile(f,dfw);

    }
    static void loadFile(String filePath, CEPEngine dfw){
        InputStream inputStream = null;
        try {
            boolean found =false;
            File f = new File(filePath);
            if(f.exists() && !f.isDirectory()) {

                inputStream = new FileInputStream(filePath);

                found =true;
            }else {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                inputStream = classloader.getResourceAsStream(filePath);
                if(inputStream.markSupported())
                    found =true;
            }
            if(!found)
                loggerService.warn("There is no persistency file ");
            else
                feed(IOUtils.toString(inputStream), dfw);
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
    private static String getThingID(String topic){
        String [] aux = topic.split("/");
        return aux[aux.length-2];
    }
    static protected void feed(String rawData,CEPEngine dfw){
        //PersistentBean persistentBean= gson.fromJson(rawData, PersistentBean.class);
        PersistentBean persistentBean= null;
        try {
            persistentBean = mapper.readValue(rawData, PersistentBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(persistentBean!=null) {
            if (persistentBean.getStatements() != null && !persistentBean.getStatements().isEmpty()) {
                for (Statement stm : persistentBean.getStatements()) {
                    feedStatement(stm,dfw);
                }
            }
            if (persistentBean.getObservations() != null && !persistentBean.getObservations().isEmpty()) {
                for (String topic : persistentBean.getObservations().keySet()) {

                    String id= getThingID(topic);
                    for(Observation observation: persistentBean.getObservations(topic)) {
                        observation.setId(id);
                        dfw.addEvent(topic, observation, observation.getClass());
                    }

                }
            }
        }


    }

    static protected void feedStatement( Statement statement, CEPEngine dfw) {

        if (statement != null) {

            try {
                dfw.addStatement(statement);
                loggerService.info("Statement " + statement.getID() + " was successful");
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);

            }


        }
    }

    @Override
    public boolean dataFusionWrapperSignIn(CEPEngine dfw) {
        loadFiles(dfw);
        //TODO: add code for the OSGi future
        return true;
    }

    @Override
    public boolean dataFusionWrapperSignOut(CEPEngine dfw) {

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
