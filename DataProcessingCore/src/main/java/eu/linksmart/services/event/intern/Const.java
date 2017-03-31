package eu.linksmart.services.event.intern;




import eu.linksmart.services.event.feeder.FeederConst;
import eu.linksmart.services.event.handler.HandlerConst;


/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.services.utils.constants.Const, FeederConst, HandlerConst {


    public static final String STATEMENT_INOUT_BROKER_CONF_PATH ="api.statements.mqtt.broker";
    public static final String STATEMENT_INOUT_BASE_TOPIC_CONF_PATH ="api.statements.mqtt.topic.base";
    public static final String STATEMENT_IN_TOPIC_ADD_CONF_PATH ="api.statements.mqtt.topic.base.add";
    public static final String STATEMENT_IN_TOPIC_CREATE_CONF_PATH ="api.statements.mqtt.topic.base.create";
    public static final String STATEMENT_IN_TOPIC_DELETE_CONF_PATH ="api.statements.mqtt.topic.base.remove";
    public static final String STATEMENT_IN_TOPIC_UPDATE_CONF_PATH ="api.statements.mqtt.topic.base.update";
    public static final String STATEMENT_OUT_TOPIC_ERROR_CONF_PATH ="api.statements.mqtt.topic.base.errors";

    public static final String EVENT_IN_TOPIC_CONF_PATH = "api.events.mqtt.topics.incoming";
    public static final String EVENTS_IN_BROKER_CONF_PATH ="api.events.mqtt.broker.incoming";

    public static final String LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH = "core.monitoring.heartbeat.every.milliseconds";

    public static final String PERSISTENT_DATA_FILE = "cep.init.bootstrapping.file";
    public static final String PERSISTENT_DATA_DIRECTORY = "cep.init.bootstrapping.file";

    public static final String CEP_ENGINES_PATH = "cep.init.engines";

    public static final String DEFAULT_CONFIGURATION_FILE = "__def__conf__.cfg";

    public static final String APPLICATION_CONFIGURATION_FILE = "conf.cfg";
    public static final String ID_CONF_PATH = "agent.id";

   // public static final String FeederPayloadTopic ="feeder.payload.type.topic";
    public static final String FeederPayloadClass ="connector.observers.payload.type.classes";
    public static final String FeederPayloadAlias ="connector.observers.type.aliases";


    public static final String AdditionalImportPackage ="cep.init.additionalImportPackage";

    public static final String ADDITIONAL_CLASS_TO_BOOTSTRAPPING = "agent.init.extensions";

    public static final String START_MQTT_STATEMENT_API = "api.statements.mqtt.enable";
    public static final String WILL_TOPIC = "api.statements.mqtt.topic.base.will";
    public static final String WILL_MESSAGE = "api.statements.mqtt.message.base.will";
    public static final String MONITOR_TOPICS = "connector.monitoring.mqtt.events.report.topics";
    public static final String MONITOR_EVENTS = "connector.monitoring.mqtt.events.report.everySeconds";
    public static final String ENVIRONMENTAL_VARIABLES_ENABLED = "env.var.enabled";
}
