package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public abstract class IncomingMqttObserver implements Observer {

    protected long debugCount=0;
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();



    protected StaticBroker brokerService;

    public IncomingMqttObserver()  {

    }
    public StaticBroker getBrokerService() {
        return brokerService;
    }

    public void setBrokerService(StaticBroker brokerService) {
        this.brokerService = brokerService;
    }
    @Override
    public void update(Observable topic, Object mqttMessage)  {

        debugCount=(debugCount+1)%Long.MAX_VALUE;
        try {
            if(debugCount%conf.getInt(Const.LOG_DEBUG_NUM_IN_EVENTS_REPORTED_CONF_PATH) == 0)
                loggerService.debug(Utils.getDateNowString() + " message arrived with topic: " + ((MqttMessage) mqttMessage).getTopic());

        }catch (Exception e){
            loggerService.warn("Error while loading configuration, doing the action from hardcoded values");
            if(debugCount%20== 0)
                loggerService.debug(Utils.getDateNowString() + " message arrived with topic: " + ((MqttMessage) mqttMessage).getTopic());

        }

        mangeEvent(((MqttMessage) mqttMessage).getTopic(), ((MqttMessage) mqttMessage).getPayload());

    }

    protected abstract void mangeEvent(String topic, byte[] payload) ;

    protected void publishFeedback(GeneralRequestResponse requestResponse){
        try {
            brokerService.publish(requestResponse.getTopic(), requestResponse.getMessage());
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
    }
    protected void publishFeedback(TraceableException e){
        try {
            brokerService.publish(buildTopic(DynamicConst.getId(),e.getErrorProducerType(),"error",e.getErrorProducerId()), e.getMessage());
        } catch (Exception ex) {

            loggerService.error(e.getMessage(), e);
            loggerService.error(ex.getMessage(), ex);
        }
    }
    protected void publishFeedback(UntraceableException e){
        try {
            brokerService.publish(buildTopic(DynamicConst.getId(),"error", e.getMessage()), e.getMessage());
        } catch (Exception ex) {

            loggerService.error(e.getMessage(), e);
            loggerService.error(ex.getMessage(), ex);
        }
    }
    protected String buildTopic(String... topicParts){
        String result="";
        if(topicParts!=null)
            for(String part:topicParts)
                result+=part+"/";

        return result;
    }
}
