package eu.almanac.event.datafusion.intern;




import java.util.UUID;



/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Const extends eu.linksmart.gc.utils.constants.Const {

    public static final String STATEMENT_INOUT_BASE_TOPIC_CONF_PATH ="STATEMENT_INOUT_BASE_TOPIC";
    public static final String STATEMENT_IN_TOPIC_CONF_PATH ="STATEMENT_IN_TOPIC";
    public static final String STATEMENT_INOUT_BROKER_CONF_PATH ="STATEMENT_INOUT_BROKER";
    public static final String STATEMENT_INOUT_BROKER_PORT_CONF_PATH ="STATEMENT_INOUT_BROKER_PORT";

    public static final String EVENT_BASE_IN_TOPIC_CONF_PATH ="EVENT_BASE_IN_TOPIC";
    public static final String EVENT_IN_TOPIC_CONF_PATH = "EVENT_IN_TOPIC";
    public static final String EVENTS_IN_BROKER_CONF_PATH ="EVENTS_IN_BROKER";
    public static final String EVENTS_IN_BROKER_PORT_CONF_PATH ="EVENTS_IN_BROKER_PORT";

    public static final String EVENT_OUT_TOPIC_CONF_PATH = "EVENT_OUT_TOPIC";

    public static final String EVENTS_OUT_BROKER_ALIASES_CONF_PATH ="EVENTS_OUT_BROKERS_ALIASES";
    public static final String EVENTS_OUT_BROKER_CONF_PATH ="EVENTS_OUT_BROKERS";
    public static final String EVENTS_OUT_BROKER_PORT_CONF_PATH ="EVENTS_OUT_BROKER_PORTS";



    public static final String LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH = "LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH";
    public static final String LOG_DEBUG_NUM_IN_EVENTS_REPORTED_CONF_PATH = "LOG_DEBUG_NUM_IN_EVENTS_REPORTED";

    public static final UUID DFM_ID = UUID.randomUUID();
    //public static final String DEFAULT_CONFIGURATION_FILE = "dfm.cfg";
    static {
        eu.linksmart.gc.utils.configuration.ConfigurationConst.DEFAULT_CONFIGURATION_FILE = "dfm.cfg";


    }


}
