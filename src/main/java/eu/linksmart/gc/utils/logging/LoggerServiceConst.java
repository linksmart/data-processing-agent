package eu.linksmart.gc.utils.logging;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public interface LoggerServiceConst {
    static final public String SERVICE_NAME_CONF_PATH = "LOGGER_SERVICE_NAME";
    static final public String SERVICE_MQTT_QOS_CONF_PATH = "LOGGER_SERVICE_MQTT_QOS";
    static final public String SERVICE_MQTT_RETAIN_POLICY_CONF_PATH = "LOGGER_SERVICE_MQTT_RETAIN_POLICY";
    static final public String DEBUG_LOG_CONF_PATH = "LOGGER_DEBUG_LOG";
    static final public String INFO_LOG_CONF_PATH = "LOGGER_INFO_LOG";
    static final public String TRACE_LOG_CONF_PATH = "LOGGER_TRACE_LOG";
    static final public String ERROR_LOG_CONF_PATH = "LOGGER_ERROR_LOG";

    public static final String LOG_ONLINE_ENABLED_CONF_PATH = "LOGGER_LOG_ONLINE_ENABLED";
    public static final String LOG_OUT_BROKER_CONF_PATH ="LOGGER_LOG_OUT_BROKER";
    public static final String LOG_OUT_BROKER_PORT_CONF_PATH ="LOGGER_LOG_OUT_BROKER_PORT";
}
