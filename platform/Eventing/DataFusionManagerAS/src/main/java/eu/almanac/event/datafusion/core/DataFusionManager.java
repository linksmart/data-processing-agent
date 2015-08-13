package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.feeder.EventFeederImpl;

import eu.almanac.event.datafusion.feeder.FeederImpl;
import eu.almanac.event.datafusion.feeder.StatementFeederImpl;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.gc.utils.logging.LoggerService;

/**
 * Created by J. Angel Caravajal on 06.10.2014.
 *
 */
public class DataFusionManager {

    public static void main(String[] args) {
        Configurator conf;



        if(args.length>=1) {


            Const.DEFAULT_CONFIGURATION_FILE= args[1];

        }else
            Const.DEFAULT_CONFIGURATION_FILE = "dfm.cfg";
        conf = Configurator.getDefaultConfig();
        LoggerService loggerService = Utils.initDefaultLoggerService(DataFusionManager.class);


        loggerService.info(
                "The Data-Fusion Manager is starting with ID: " + Const.DFM_ID.toString() + ";\n" +
                        " with incoming events in broker tcp://" + conf.get(Const.EVENTS_IN_BROKER_CONF_PATH) + ":" + conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH) +
                        " waiting for events from the topic: " + conf.get(Const.EVENT_IN_TOPIC_CONF_PATH) + ";\n" +
                        " waiting for queries from topic: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH) +
                        " generating event in: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH)
        );
        FeederImpl feederImplEvents = null,  feederImplQuery = null;
        try {
            feederImplEvents = new EventFeederImpl(conf.get(Const.EVENTS_IN_BROKER_CONF_PATH).toString(), conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.EVENT_IN_TOPIC_CONF_PATH).toString());

            EsperEngine esper = new EsperEngine();

            feederImplEvents.dataFusionWrapperSignIn(esper);


            feederImplQuery = new StatementFeederImpl(conf.get(Const.STATEMENT_INOUT_BROKER_CONF_PATH).toString(), conf.get(Const.STATEMENT_INOUT_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH).toString());


            feederImplQuery.dataFusionWrapperSignIn(esper);

        } catch (Exception e) {
           loggerService.error(e.getMessage(),e);
        }

        if(feederImplQuery == null  || feederImplEvents ==null){
            loggerService.error("The feeders couldn't start! MDF now is stopping");
            return;
        }


        while (!feederImplEvents.isDown() || !feederImplQuery.isDown()){


            loggerService.info("Data Fusion Manager is alive");


            try {
                Thread.sleep(conf.getInt(Const.LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH));
            } catch (InterruptedException e) {
               loggerService.error(e.getMessage(),e);
            }
        }
    }

}
