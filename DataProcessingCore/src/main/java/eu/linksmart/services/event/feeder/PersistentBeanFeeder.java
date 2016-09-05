package eu.linksmart.services.event.feeder;

import eu.linksmart.api.event.components.Deserializer;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.feeder.type.PersistentBean;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.event.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 05.09.2016 a researcher of Fraunhofer FIT.
 */
public class PersistentBeanFeeder implements Feeder {

    protected static Deserializer deserializer = new DefaultDeserializer();
    static protected Logger loggerService = Utils.initLoggingConf(PersistentBeanFeeder.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    static public void feed(String rawData){
        //PersistentBean persistentBean= gson.fromJson(rawData, PersistentBean.class);
        PersistentBean persistentBean= null;
        try {
            persistentBean = deserializer.parse(rawData,PersistentBean.class);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
        }

        if(persistentBean!=null) {
            if (persistentBean.getStatements() != null && !persistentBean.getStatements().isEmpty()) {
                for (Statement stm : persistentBean.getStatements()) {
                    StatementFeeder.addNewStatement(stm,null,null);
                }
            }
            if (persistentBean.getObservations() != null && !persistentBean.getObservations().isEmpty()) {
                for (String topic : persistentBean.getObservations().keySet()) {

                    String id= getThingID(topic);
                    for(EventEnvelope observation: persistentBean.getObservations(topic)) {
                        observation.setId(id);
                        try {
                           EventFeeder.feed(topic,observation);
                        } catch (TraceableException | UntraceableException e) {
                            loggerService.error(e.getMessage(),e);
                        }
                    }

                }
            }
        }


    }
    private static String getThingID(String topic){
        String [] aux = topic.split("/");
        return aux[aux.length-2];
    }
}
