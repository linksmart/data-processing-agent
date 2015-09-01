package eu.almanac.event.datafusion.intern;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.MqttLogger;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class Utils extends eu.linksmart.gc.utils.function.Utils {
    static public Logger createMqttLogger(Class lass, Configurator conf) throws MalformedURLException, MqttException {
        StaticBrokerService brokerService=  StaticBrokerService.getBrokerService(
                lass.getCanonicalName(),
                conf.getString(Const.LOG_OUT_BROKER_CONF_PATH),
                conf.getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
        );

        return MqttLogger.getLogger(lass, brokerService);
    }

}
