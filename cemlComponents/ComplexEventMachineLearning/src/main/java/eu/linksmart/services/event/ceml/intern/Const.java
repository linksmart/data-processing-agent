package eu.linksmart.services.event.ceml.intern;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const,eu.linksmart.services.event.intern.Const {
    public static final String CEML_GenerateReports =   "ceml_monitoring_evaluation_generateReports";

    public static final String CEML_MQTT_OUTPUT_TOPIC =   "ceml_api_mqtt_topic_output";
    public static final String CEML_MQTT_INPUT_TOPIC =   "ceml_api_mqtt_topic_input";
    public static final String CEML_MQTT_ERROR_TOPIC =   "ceml_api_mqtt_topic_error";
    public static final String CEML_MQTT_BROKER_HOST =   "ceml_api_mqtt_broker";
    public static final String CEML_DEFAULT_CONFIGURATION_FILE = "__def__conf__.cfg";
    public static final String CEML_INIT_BOOTSTRAPPING = "ceml_init_bootstrapping";
}
