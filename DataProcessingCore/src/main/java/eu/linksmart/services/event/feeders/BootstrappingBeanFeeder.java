package eu.linksmart.services.event.feeders;

import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.services.event.core.StatementInstance;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.core.BootstrappingBean;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 05.09.2016 a researcher of Fraunhofer FIT.
 */
public class BootstrappingBeanFeeder implements Feeder<BootstrappingBean> {

    static protected Logger loggerService = Utils.initLoggingConf(BootstrappingBeanFeeder.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    static {
        Feeder.feeders.put(BootstrappingBean.class.getCanonicalName(),new BootstrappingBeanFeeder());
        SharedSettings.getDeserializer().defineClassToInterface(EventEnvelope.class, Observation.class);
        SharedSettings.getDeserializer().defineClassToInterface(Statement.class, StatementInstance.class);
    }
    static public void feed(String rawData) {
        //bootstrappingBean bootstrappingBean= gson.fromJson(rawData, bootstrappingBean.class);
        loggerService.info("Bootstrapping events and statements...");
        BootstrappingBean bootstrappingBean = null;
        try {
            // "[{\"name\":\"aggregatedConsumption\",\"statement\":\"select Event.factory(begin.bn, String.valueOf(begin.last.v), power.sumof(i=>cast(i.last.v,double)), cast(fin.last.t, long) - cast(begin.last.t,long) ) from pattern[ every begin=SenML(last.n='WS0024' and cast(last.v,int)!=0)-> ( (power=SenML(last.n='WSS0017')) until (fin=SenML(last.n='WS0024' and cast(last.v,int)=0))) ]\"}]"
            bootstrappingBean = SharedSettings.getDeserializer().parse(rawData, BootstrappingBean.class);
        } catch (IOException e) {
            loggerService.error(e.getMessage(), e);
        }

        feed(bootstrappingBean);

        loggerService.info("...Bootstrapped!");
    }
    private static String getThingID(String topic){
        String [] aux = topic.split("/");
        return aux[aux.length-2];
    }

    @Override
    public void feed(String topicURI, String payload) throws TraceableException, UntraceableException {
        feed(payload);
    }

    @Override
    public void feed(String topicURI, BootstrappingBean BootstrappingBean) throws TraceableException, UntraceableException {
        feed(BootstrappingBean);
    }
    static public void feed(BootstrappingBean bootstrappingBean){
        if (bootstrappingBean != null) {
            if (bootstrappingBean.getStatements() != null && !bootstrappingBean.getStatements().isEmpty()) {
                for (Statement stm : bootstrappingBean.getStatements()) {
                    if (    !StatementFeeder.addNewStatement(stm, null, null, null).containsSuccess() &&
                             stm.isEssential()){
                        loggerService.error("Fail loading statement "+stm.getId()+" in persistence process and the programmatic exit is set");
                        System.exit(-1);
                    }



                }
            }
          /*  if(bootstrappingBean.getLearningRequests()!=null){
                if(Feeder.feeders.containsKey("eu.linksmart.services.event.ceml.core.CEML"))
                    bootstrappingBean.getLearningRequests().stream().forEach(i->{
                        try {
                            Feeder.feeders.get("eu.linksmart.services.event.ceml.core.CEML").feed(i.getName(),i);

                        }catch (Exception e){
                            loggerService.error(e.getMessage(),e);

                        }
                    });
                    //Feeder.feeders.get("eu.linksmart.services.event.ceml.core.CEML").feed();
            }*/
            if (bootstrappingBean.getEvents() != null && !bootstrappingBean.getEvents().isEmpty()) {
                for (String topic : bootstrappingBean.getEvents().keySet()) {

                    String id = getThingID(topic);
                    for (EventEnvelope observation : bootstrappingBean.getEvent(topic)) {
                        observation.setId(id);
                        try {
                            EventFeeder.getFeeder().feed(topic, observation);
                        } catch (TraceableException | UntraceableException e) {
                            loggerService.error(e.getMessage(), e);
                        }
                    }

                }
            }
        }
    }
}
