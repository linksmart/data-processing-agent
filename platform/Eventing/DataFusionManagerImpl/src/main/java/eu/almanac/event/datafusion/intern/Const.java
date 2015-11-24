package eu.almanac.event.datafusion.intern;




import eu.almanac.event.datafusion.feeder.FeederConst;
import eu.almanac.event.datafusion.handler.HandlerConst;

import java.util.UUID;



/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface Const extends eu.linksmart.gc.utils.constants.Const, FeederConst, HandlerConst {

    public static final String STATEMENT_INOUT_BASE_TOPIC_CONF_PATH ="STATEMENT_INOUT_BASE_TOPIC";
    public static final String STATEMENT_IN_TOPIC_CONF_PATH ="STATEMENT_IN_TOPIC";
    public static final String STATEMENT_INOUT_BROKER_CONF_PATH ="STATEMENT_INOUT_BROKER";
    public static final String STATEMENT_INOUT_BROKER_PORT_CONF_PATH ="STATEMENT_INOUT_BROKER_PORT";

    public static final String EVENT_BASE_IN_TOPIC_CONF_PATH ="EVENT_BASE_IN_TOPIC";
    public static final String EVENT_IN_TOPIC_CONF_PATH = "EVENT_IN_TOPIC";
    public static final String EVENTS_IN_BROKER_CONF_PATH ="EVENTS_IN_BROKER";
    public static final String EVENTS_IN_BROKER_PORT_CONF_PATH ="EVENTS_IN_BROKER_PORT";

    public static final String LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH = "LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH";

    public static final String PERSISTENT_DATA_FILE = "PERSISTENT_FILES";

    public static final String CEP_ENGINES_PATH = "CEP_ENGINES";

    public static final String DEFAULT_CONFIGURATION_FILE ="dfm.cfg";
    public static final UUID DFM_ID = UUID.randomUUID();

    public static final String FeederPayloadTopic ="Feeder.Payload.Type.Topic";
    public static final String FeederPayloadClass ="Feeder.Payload.Type.Class";
    public static final String FeederPayloadAlias ="Feeder.Payload.Type.Alias";


    public static final String AdditionalImportPackage ="DataFusionWrapper.AdditionalImportPackage";

}
