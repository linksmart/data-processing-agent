package eu.linksmart.services.event.feeder;

import eu.linksmart.api.event.components.Deserializer;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.BootstrappingBean;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.event.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 05.09.2016 a researcher of Fraunhofer FIT.
 */
public class BootstrappingBeanFeeder implements Feeder {

    protected static Deserializer deserializer = new DefaultDeserializer();
    static protected Logger loggerService = Utils.initLoggingConf(BootstrappingBeanFeeder.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    static public void feed(String rawData){
        //BootstrappingBean BootstrappingBean= gson.fromJson(rawData, BootstrappingBean.class);
        BootstrappingBean BootstrappingBean = null;
        try {
           // "[{\"name\":\"aggregatedConsumption\",\"statement\":\"select Event.factory(begin.bn, String.valueOf(begin.last.v), power.sumof(i=>cast(i.last.v,double)), cast(fin.last.t, long) - cast(begin.last.t,long) ) from pattern[ every begin=SenML(last.n='WS0024' and cast(last.v,int)!=0)-> ( (power=SenML(last.n='WSS0017')) until (fin=SenML(last.n='WS0024' and cast(last.v,int)=0))) ]\"}]"
            BootstrappingBean = deserializer.parse(rawData,BootstrappingBean.class);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
        }

            if(BootstrappingBean !=null) {
            if (BootstrappingBean.getStatements() != null && !BootstrappingBean.getStatements().isEmpty()) {
                for (Statement stm : BootstrappingBean.getStatements()) {
                    StatementFeeder.addNewStatement(stm,null,null);
                }
            }
            if (BootstrappingBean.getEvents() != null && !BootstrappingBean.getEvents().isEmpty()) {
                for (String topic : BootstrappingBean.getEvents().keySet()) {

                    String id= getThingID(topic);
                    for(EventEnvelope observation: BootstrappingBean.getEvent(topic)) {
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
