package eu.linksmart.services.event.intern;




import eu.linksmart.services.event.handler.HandlerConst;


/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const, eu.linksmart.services.event.feeders.Const, HandlerConst {


    String STATEMENT_INOUT_BROKER_CONF_PATH ="api_statements_mqtt_broker";
    String STATEMENT_INOUT_BASE_TOPIC_CONF_PATH ="api_statements_mqtt_topic_base";
    String STATEMENT_IN_TOPIC_ADD_CONF_PATH ="api_statements_mqtt_topic_base_add";
    String STATEMENT_IN_TOPIC_CREATE_CONF_PATH ="api_statements_mqtt_topic_base_create";
    String STATEMENT_IN_TOPIC_DELETE_CONF_PATH ="api_statements_mqtt_topic_base_remove";
    String STATEMENT_IN_TOPIC_UPDATE_CONF_PATH ="api_statements_mqtt_topic_base_update";
    String STATEMENT_OUT_TOPIC_ERROR_CONF_PATH ="api_statements_mqtt_topic_base_errors";
    String EVENT_IN_TOPIC_CONF_PATH = "api_events_mqtt_topic_incoming";
    String EVENTS_IN_BROKER_CONF_PATH ="api_events_mqtt_broker_incoming";
    String LOG_HEARTBEAT_TIME_CONF_PATH = "core_monitoring_heartbeat_every_milliseconds";

    String PERSISTENT_DATA_FILE = "cep_init_bootstrapping_files";

    String PERSISTENT_EVENTS_FILE = "cep_init_bootstrapping_events_files";

    String CEP_ENGINES_PATH = "cep_init_engines";

    String DEFAULT_CONFIGURATION_FILE = "__def__conf__.cfg";

    String APPLICATION_CONFIGURATION_FILE = "conf.cfg";
    String ID_CONF_PATH = "agent_id";

    String FeederPayloadClass ="connector_observers_payload_type_class";
    String FeederPayloadAlias ="connector_observers_type_aliases";


    String AdditionalImportPackage ="cep_init_additionalImportPackage";

    String ADDITIONAL_CLASS_TO_BOOTSTRAPPING = "agent_init_extensions";

    String START_MQTT_STATEMENT_API = "api_statements_mqtt_enable";
    String MONITOR_TOPICS = "connector_monitoring_mqtt_events_report_topics";
    String MONITOR_EVENTS = "connector_monitoring_mqtt_events_report_everySeconds";
    String ENVIRONMENTAL_VARIABLES_ENABLED = "env_var_enabled";
    String ENABLE_REST_API = "api_rest_enabled";
    String SPRING_MANAGED_FEATURES = "spring_managed_configuration_features";

    String REST_API_EXTENSION = "rest_api_extensions";
    String AGENT_DESCRIPTION = "agent_description";
    String REGISTRATION_TOPIC = "registration_topic";
    String REGISTRATION_TOPIC_WILL = "topic_will";
}
