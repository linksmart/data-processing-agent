package eu.linksmart.gc.utils.constants;

import eu.linksmart.gc.utils.configuration.ConfigurationConst;
import eu.linksmart.gc.utils.logging.LoggerServiceConst;
import eu.linksmart.gc.utils.mqtt.broker.BrokerServiceConst;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends   BrokerServiceConst,LoggerServiceConst {
    public static String TIME_FORMAT_CONF_PATH = "TIME_ISO";
    public static String TIME_TIMEZONE_CONF_PATH = "TIME_TIMEZONE";
    public static final String TIME_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ;

}
