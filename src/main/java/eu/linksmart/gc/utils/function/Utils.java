package eu.linksmart.gc.utils.function;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.logging.MqttLogger;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class  Utils {
    static private DateFormat dateFormat = getDateFormat();

    static private DateFormat isoDateFormat = new SimpleDateFormat(Const.TIME_ISO_FORMAT);
    static public DateFormat getDateFormat(){
        DateFormat dateFormat;
        String tzs = Configurator.getDefaultConfig().getString(Const.TIME_TIMEZONE_CONF_PATH);
        if(tzs == null || tzs.equals(""))
            tzs = "UTC";
        TimeZone tz = TimeZone.getTimeZone(tzs);
        if(Configurator.getDefaultConfig().getString(Const.TIME_FORMAT_CONF_PATH) == null)

            dateFormat= new SimpleDateFormat(Const.TIME_ISO_FORMAT);

        else
             dateFormat =new SimpleDateFormat(Const.TIME_ISO_FORMAT);

        dateFormat.setTimeZone(tz);

        return dateFormat;

    }
    static public String getTimestamp(Date date){
        return dateFormat.format(date);
    }
    static public String getIsoTimestamp(Date date){
        return isoDateFormat.format(date);
    }
    static public String getDateNowString(){
        return getDateFormat().format(new Date());
    }
    static public LoggerService initDefaultLoggerService(Class lass){


        LoggerService loggerService = new LoggerService(LoggerFactory.getLogger(lass));
        try {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile)));
                loggerService.info("Loading from configuration from jar default file");
            }catch(FileNotFoundException ex){
                try {

                    InputStream in = lass.getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    p.load(in);
                    loggerService.info("Loading from configuration from jar default file");
                }catch (Exception exx){
                    try {
                        InputStream in = Utils.class.getClassLoader().getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                        p.load(in);
                        loggerService.info("Loading from configuration from jar default file");
                    }catch (Exception exxx){

                        exxx.printStackTrace();
                    }
                }
            }
            PropertyConfigurator.configure(p);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Configurator.getDefaultConfig().getBool(Const.LOG_ONLINE_ENABLED_CONF_PATH)) {
            try {

                loggerService.addLoggers(Utils.createMqttLogger(lass));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }

        loggerService.info("Configuration file loaded");
        return loggerService;

    }
    static public Logger createMqttLogger(Class lass) throws MalformedURLException, MqttException {

        return MqttLogger.getLogger(
                lass,
                new StaticBroker(
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_CONF_PATH),
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
                )
        );
    }
}
