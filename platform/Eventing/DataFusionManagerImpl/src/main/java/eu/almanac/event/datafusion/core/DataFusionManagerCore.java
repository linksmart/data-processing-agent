package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.esper.EsperEngine;
import eu.almanac.event.datafusion.feeder.EventMqttFeederImpl;

import eu.almanac.event.datafusion.feeder.MqttFeederImpl;
import eu.almanac.event.datafusion.feeder.StatementMqttFeederImpl;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.ArrayList;

/**
 * Created by J. Angel Caravajal on 06.10.2014.
 *
 */
public class DataFusionManagerCore {

    protected static ArrayList<MqttFeederImpl> feeders = new ArrayList<>();

    public static boolean isActive() {
        return active;
    }

    protected static boolean active =false;
    protected static Configurator conf;
    protected static  LoggerService loggerService;

    public static void run(String[] args){

         init(args);
        statusLoop();
    }

    public static boolean start(String[] args){
        Boolean ret =init(args);
        new Thread(new Runnable(){

            @Override
            public void run() {
                statusLoop();
            }
        }
        ).start();
        return ret;

    }
    static protected void statusLoop(){
        active = true;
        while (active){

            for(MqttFeederImpl f:feeders)
                if (f.isDown())
                    active =false;

            if(active) {
                loggerService.info("Data Fusion Manager is alive");


                try {
                    Thread.sleep(conf.getInt(Const.LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH));
                } catch (InterruptedException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }
        }
    }
    protected static boolean init(String args[]){




        if(args.length>0) {
            for (String arg: args)
                Configurator.addConfFile(arg);

        }else
            Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();
        loggerService = Utils.initDefaultLoggerService(DataFusionManagerCore.class);


        loggerService.info(
                "The Data-Fusion Manager is starting with ID: " + Const.DFM_ID.toString() + ";\n" +
                        " with incoming events in broker tcp://" + conf.get(Const.EVENTS_IN_BROKER_CONF_PATH) + ":" + conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH) +
                        " waiting for events from the topic: " + conf.get(Const.EVENT_IN_TOPIC_CONF_PATH) + ";\n" +
                        " waiting for queries from topic: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH) +
                        " generating event in: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH)
        );
        MqttFeederImpl feederImplEvents = null,  feederImplQuery = null;
        try {
            EsperEngine esper = EsperEngine.getEngine();

            feederImplEvents = new EventMqttFeederImpl(conf.get(Const.EVENTS_IN_BROKER_CONF_PATH).toString(), conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.EVENT_IN_TOPIC_CONF_PATH).toString());
            feederImplEvents.dataFusionWrapperSignIn(esper);


            feederImplQuery = new StatementMqttFeederImpl(conf.get(Const.STATEMENT_INOUT_BROKER_CONF_PATH).toString(), conf.get(Const.STATEMENT_INOUT_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH).toString());


            feederImplQuery.dataFusionWrapperSignIn(esper);



        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

        if(feederImplQuery == null  || feederImplEvents ==null || feederImplEvents.isDown() || feederImplQuery.isDown()){
            loggerService.error("The feeders couldn't start! MDF now is stopping");
            return false;
        }

        feeders.add(feederImplEvents);
        feeders.add(feederImplQuery);

        return true;

    }

}
