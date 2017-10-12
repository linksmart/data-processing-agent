package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.subscription.MqttMessageObserver;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.testing.tooling.MessageValidator;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public abstract class IncomingMqttObserver implements MqttMessageObserver {

    protected transient long debugCount=0;
    protected transient Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected transient Configurator conf =  Configurator.getDefaultConfig();

    protected StaticBroker brokerService;
    protected ArrayList<String> topics = new ArrayList<>();

    //Start of code made for testing performance
    protected final boolean VALIDATION_MODE;
    private final MessageValidator validator;
    //End of code made for testing performance

    public IncomingMqttObserver(List<String> topics)  {
        this.topics.addAll( topics.stream().map(s -> s.replace("<id>", SharedSettings.getId())).collect(Collectors.toList()));

        /// Code for validation and test proposes
        if(VALIDATION_MODE = Configurator.getDefaultConfig().containsKeyAnywhere(eu.linksmart.services.utils.constants.Const.VALIDATION_LOT_SIZE)) {

            validator = new MessageValidator(this.getClass(),"0",Configurator.getDefaultConfig().getLong(eu.linksmart.services.utils.constants.Const.VALIDATION_LOT_SIZE));
        }else{
            validator = null;
        }
    }
    public IncomingMqttObserver(String topic)  {
        topics.add(topic.replace("<id>", SharedSettings.getId()));

        /// Code for validation and test proposes
        if(VALIDATION_MODE = Configurator.getDefaultConfig().containsKeyAnywhere(eu.linksmart.services.utils.constants.Const.VALIDATION_OBSERVERS   )) {
            validator = new MessageValidator(this.getClass(),"0",Configurator.getDefaultConfig().getLong(eu.linksmart.services.utils.constants.Const.VALIDATION_LOT_SIZE));
        }else{
            validator = null;
        }
    }
    public StaticBroker getBrokerService() {
        return brokerService;
    }

    public void setBrokerService(StaticBroker brokerService) {
        this.brokerService = brokerService;
    }
    public void addTopic(String topic){
        topics.add(topic);
    }
    public List<String> getTopics(){
        return topics;
    }
    @Override
    public void update(String topic, MqttMessage mqttMessage)  {

        debugCount=(debugCount+1)%Long.MAX_VALUE;
        try {
            if(debugCount%conf.getInt(Const.LOG_DEBUG_NUM_IN_EVENTS_REPORTED_CONF_PATH) == 0)
                loggerService.debug(Utils.getDateNowString() + " message arrived with topic: " +  mqttMessage.getTopic());

        }catch (Exception e){
            loggerService.warn("Error while loading configuration, doing the action from hardcoded values");
            if(debugCount%20== 0)
                loggerService.debug(Utils.getDateNowString() + " message arrived with topic: " +  mqttMessage.getTopic());

        }

        mangeEvent( mqttMessage.getTopic(), mqttMessage.getPayload());

        if(VALIDATION_MODE) toValidation(mqttMessage.getTopic(),  mqttMessage.getPayload());
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
            brokerService.publish(buildTopic(SharedSettings.getId(),e.getErrorProducerType(),"error",e.getErrorProducerId()), e.getMessage());
        } catch (Exception ex) {

            loggerService.error(e.getMessage(), e);
            loggerService.error(ex.getMessage(), ex);
        }
    }
    protected void publishFeedback(UntraceableException e){
        try {
            brokerService.publish(buildTopic(SharedSettings.getId(),"error", e.getMessage()), e.getMessage());
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


    /// for validation and evaluation propose
    private void toValidation(String topic, byte[] payload){
        if (VALIDATION_MODE)
            try {
                validator.addMessage(topic,(int)SharedSettings.getDeserializer().deserialize(payload, Hashtable.class).get("ResultValue"));
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
}
