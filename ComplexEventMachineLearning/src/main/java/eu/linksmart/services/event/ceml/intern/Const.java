package eu.linksmart.services.event.ceml.intern;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const {
    public static final String CEML_GenerateReports =   "ceml.monitoring.evaluation.generateReports";

    public static final String CEML_MQTT_OUTPUT_TOPIC =   "ceml.api.mqtt.topic.output";
    public static final String CEML_MQTT_INPUT_TOPIC =   "ceml.api.mqtt.topic.input";
    public static final String CEML_MQTT_ERROR_TOPIC =   "ceml.api.mqtt.topic.error";
    public static final String CEML_MQTT_BROKER_HOST =   "ceml.api.mqtt.broker";
    public static final String CEML_DEFAULT_CONFIGURATION_FILE = "conf.cfg";
}
