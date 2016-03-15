package de.fraunhofer.fit.event.ceml.intern;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.gc.utils.constants.Const {
    static final public String DefaultEvaluatorConfPath = "CEML.Evaluation.Default";
    public static final String COMPLEX_LEARNING_HANDLER ="COMPLEX_LEARNING_HANDLER";

    public static final String CEML_GenerateReports =   "CEML.Evaluation.GenerateReports";

    public static final String CEML_EngineTimeProveded =   "CEML.Evaluation.TimeProvedBy";
    public static final String CEML_MQTT_OUTPUT_TOPIC =   "CEML.API.MQTT.Topic.Output";
    public static final String CEML_MQTT_INPUT_TOPIC =   "CEML.API.MQTT.Topic.Input";
    public static final String CEML_MQTT_ERROR_TOPIC =   "CEML.API.MQTT.Topic.Error";
    public static final String CEML_MQTT_BROKER_HOST =   "CEML.API.MQTT.Broker.Host";
    public static final String CEML_MQTT_BROKER_PORT =   "CEML.API.MQTT.Broker.Port";
    public static final String CEML_DEFAULT_CONFIGURATION_FILE = "conf.cfg";
}
