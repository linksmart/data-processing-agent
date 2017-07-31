package eu.linksmart.services.event.intern;




import eu.linksmart.services.event.handler.HandlerConst;


/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const, eu.linksmart.services.event.feeders.Const, HandlerConst {


    public static final String STATEMENT_INOUT_BROKER_CONF_PATH ="api_statements_mqtt_broker";
    public static final String STATEMENT_INOUT_BASE_TOPIC_CONF_PATH ="api_statements_mqtt_topic_base";
    public static final String STATEMENT_IN_TOPIC_ADD_CONF_PATH ="api_statements_mqtt_topic_base_add";
    public static final String STATEMENT_IN_TOPIC_CREATE_CONF_PATH ="api_statements_mqtt_topic_base_create";
    public static final String STATEMENT_IN_TOPIC_DELETE_CONF_PATH ="api_statements_mqtt_topic_base_remove";
    public static final String STATEMENT_IN_TOPIC_UPDATE_CONF_PATH ="api_statements_mqtt_topic_base_update";
    public static final String STATEMENT_OUT_TOPIC_ERROR_CONF_PATH ="api_statements_mqtt_topic_base_errors";

    public static final String EVENT_IN_TOPIC_CONF_PATH = "api_events_mqtt_topic_incoming";
    public static final String EVENTS_IN_BROKER_CONF_PATH ="api_events_mqtt_broker_incoming";

    public static final String LOG_HEARTBEAT_TIME_CONF_PATH = "core_monitoring_heartbeat_every_milliseconds";

    public static final String PERSISTENT_DATA_FILE = "cep_init_bootstrapping_files";

    public static final String PERSISTENT_EVENTS_FILE = "cep_init_bootstrapping_events_files";
    public static final String PERSISTENT_DATA_DIRECTORY = "cep_init_bootstrapping_directories";

    public static final String CEP_ENGINES_PATH = "cep_init_engines";

    public static final String DEFAULT_CONFIGURATION_FILE = "__def__conf__.cfg";

    public static final String APPLICATION_CONFIGURATION_FILE = "conf.cfg";
    public static final String ID_CONF_PATH = "agent_id";

    public static final String FeederPayloadClass ="connector_observers_payload_type_class";
    public static final String FeederPayloadAlias ="connector_observers_type_aliases";


    public static final String AdditionalImportPackage ="cep_init_additionalImportPackage";

    public static final String ADDITIONAL_CLASS_TO_BOOTSTRAPPING = "agent_init_extensions";

    public static final String START_MQTT_STATEMENT_API = "api_statements_mqtt_enable";
    public static final String WILL_TOPIC = "api_statements_mqtt_topic_base_will";
    public static final String WILL_MESSAGE = "api_statements_mqtt_message_base_will";
    public static final String MONITOR_TOPICS = "connector_monitoring_mqtt_events_report_topics";
    public static final String MONITOR_EVENTS = "connector_monitoring_mqtt_events_report_everySeconds";
    public static final String ENVIRONMENTAL_VARIABLES_ENABLED = "env_var_enabled";
    public static final String ENABLE_REST_API = "api_rest_enabled";
    public static final String SPRING_MANAGED_FEATURES = "spring_managed_configuration_features";

    public static final String REST_API_EXTENSION = "rest_api_extensions";
    String AGENT_DESCRIPTION = "agent_description";
    String REGISTRATION_TOPIC = "registration_topic";
}
