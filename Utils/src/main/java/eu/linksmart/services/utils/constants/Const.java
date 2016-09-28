package eu.linksmart.services.utils.constants;

import eu.linksmart.services.utils.logging.LoggerServiceConst;
import eu.linksmart.services.utils.mqtt.broker.BrokerServiceConst;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends   BrokerServiceConst,LoggerServiceConst {
    public static String TIME_FORMAT_CONF_PATH = "general.time.timestamp.format";
    public static String TIME_TIMEZONE_CONF_PATH = "general.time.zone";
    public static String TIME_EPOCH_CONF_PATH = "general.time.epoch";
    public static final String TIME_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ;

    public static final  String CONFIGURATION_CLASS_FILE = "eu.linksmart.services.Application";


}
