package eu.almanac.event.datafusion.intern;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.logging.MqttLogger;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class Utils {
    static public Logger createMqttLogger(Class lass, Configurator conf) throws MalformedURLException, MqttException {
        StaticBrokerService brokerService=  StaticBrokerService.getBrokerService(
                lass.getCanonicalName(),
                conf.getString(Const.LOG_OUT_BROKER_CONF_PATH),
                conf.getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
        );

        return MqttLogger.getLogger(lass, brokerService);
    }
    static public Logger createMqttLogger(Class lass) throws MalformedURLException, MqttException {
        StaticBrokerService brokerService=  StaticBrokerService.getBrokerService(
                lass.getCanonicalName(),
                Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_CONF_PATH),
                Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
        );

        return MqttLogger.getLogger(lass, brokerService);
    }
    static public LoggerService initDefaultLoggerService(Class lass){
       LoggerService loggerService = new LoggerService(LoggerFactory.getLogger(lass));
        if (Configurator.getDefaultConfig().getBool(Const.LOG_ONLINE_ENABLED_CONF_PATH)) {
            try {

                loggerService.addLoggers(Utils.createMqttLogger(lass));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }
        return loggerService;

    }
}
