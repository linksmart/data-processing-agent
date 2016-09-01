package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import org.slf4j.Logger;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public abstract class IncomingMqttObserver implements Observer {

    protected long debugCount=0;
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();

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
}
