package eu.linksmart.services.event.intern;




import eu.linksmart.services.event.handler.HandlerConst;


/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const, eu.linksmart.services.event.feeders.Const, HandlerConst {
	int EMAIL_DISPATCHER_QUEUE_CAPACITY = 256; 
	long EMAIL_DISPATCHER_JOIN_INTERVAL = 5000;

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

    String DEFAULT_CONFIGURATION_FILE = "__def__agent__conf__.cfg";

    String DEFAULT_DEBUG_CONFIGURATION_FILE = "log4j2.properties";

    String APPLICATION_CONFIGURATION_FILE = "conf.cfg";
    String ID_CONF_PATH = "agent_id";

    String FeederPayloadClass ="connector_observers_payload_type_class";
    String FeederPayloadAlias ="connector_observers_type_aliases";


    String AdditionalImportPackage ="cep_init_additionalImportPackage";
    // legacy equivalent before feeders
    String ADDITIONAL_CLASS_TO_BOOTSTRAPPING = "agent_init_extensions";

    String PRE_CEP_EXTENSIONS = "agent_init_after_conf_before_CEP";
    String PRE_TYPES_EXTENSIONS = "agent_init_after_CEP_before_types";
    String PRE_FEEDERS_EXTENSIONS = "agent_init_after_types_before_feeders";
    String PRE_BOOTSTRAP_EXTENSIONS = "agent_init_after_feeders_before_bootstrapping";
    String PRE_END_EXTENSIONS = "agent_init_end";

    String START_MQTT_STATEMENT_API = "api_statements_mqtt_enable";
    String MONITOR_TOPICS = "connector_monitoring_mqtt_events_report_topics";
    String MONITOR_EVENTS = "connector_monitoring_mqtt_events_report_everySeconds";
    String ENVIRONMENTAL_VARIABLES_ENABLED = "env_var_enabled";
    String ENABLE_REST_API = "api_rest_enabled";
    String SPRING_MANAGED_FEATURES = "spring_managed_configuration_features";

    String REST_API_EXTENSION = "rest_api_extensions";
    String AGENT_DESCRIPTION = "agent_description";
    String OGC_REGISTRATION_TOPIC = "ogc_registration_topic";
    String OGC_REGISTRATION_TOPIC_WILL = "ogc_topic_will";

    String LINKSMART_SERVICE_CATALOG_ENDPOINT = "linksmart_service_catalog_endpoint";
    String LINKSMART_SERVICE_WILL_TOPIC = "linksmart_service_will_topic";
    String LINKSMART_REGISTRATION_TOPIC = "linksmart_service_registration_topic";
    String FAIL_IF_PERSISTENCE_FAILS = "fails_if_persistence_fails";
    String CONNECTOR_PERSISTENT_FILE = "connector_persist_in";
    String PERSISTENT_ENABLED = "persistent_enabled";
    String PERSISTENT_STORAGE_PERIOD = "persistent_storage_period";
    String STATEMENT_IN_TOPIC_GET_CONF_PATH = "api_statements_mqtt_topic_base_get";
    String LINKSMART_SERVICE_TTL = "linksmart_ttl";
    String STATEMENT_DEFAULT_OUTPUT_TYPE = "api_statements_default_output_type";
    String PROMISCUOUS_EVENT_PARSING = "api_events_promiscuous";
    String TRANSLATOR_MODE = "api_events_translator";
    String PYTHON_PATH = "python_path";
    String LINKSMART_SERVICE_CATALOG_IN_REGISTRATION_FAIL_STOP = "linksmart_service_catalog_in_registration_fail_stop";
}
