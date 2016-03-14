package eu.almanac.event.datafusion.core;

import eu.almanac.event.datafusion.feeder.EventMqttFeederImpl;


import eu.almanac.event.datafusion.feeder.PersistenceFeeder;
import eu.almanac.event.datafusion.feeder.StatementMqttFeederImpl;
import eu.almanac.event.datafusion.feeder.TestFeeder;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.CEPEngineAdvanced;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.almanac.event.cep.esper.EsperEngine;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by J. Angel Caravajal on 06.10.2014.
 *
 */
public class DataFusionManagerCore {

    protected static ArrayList<Feeder> feeders = new ArrayList<>();

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

            for(Feeder f:feeders)
                if (f.isDown())
                    active =false;

            if(active) {
                loggerService.info("Data Fusion Manager is alive");
                int hb = 5000;
                try {
                     hb=conf.getInt(Const.LOG_DEBUG_HEARTBEAT_TIME_CONF_PATH);

                } catch (Exception ignored) {

                }
                try {

                    Thread.sleep(hb);
                } catch (Exception e) {
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
        try {
            //System.setProperty("log4j.configuration",(new File(".", "resources"+File.separatorChar+ Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile))).toURL().toString());
            //System.setProperty("log4j.configuration",(new File(".", "resources"+File.separatorChar+ Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile))).toURL().toString());
            System.setProperty("log4j.configuration",(new File(  Configurator.getDefaultConfig().getString(eu.linksmart.gc.utils.constants.Const.LoggingDefaultLoggingFile))).toURL().toString());
            System.setProperty("log4j.configurationFile",(new File(  Configurator.getDefaultConfig().getString(eu.linksmart.gc.utils.constants.Const.LoggingDefaultLoggingFile))).toURL().toString());
            //DOMConfigurator.configure(Configurator.getDefaultConfig().getString(eu.linksmart.gc.utils.constants.Const.LoggingDefaultLoggingFile));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        loggerService = Utils.initDefaultLoggerService(DataFusionManagerCore.class);


        loggerService.info(
                "The Data-Fusion Manager is starting with ID: " + Const.DFM_ID.toString() + ";\n" +
                        " with incoming events in broker tcp://" + conf.get(Const.EVENTS_IN_BROKER_CONF_PATH) + ":" + conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH) +
                        " waiting for events from the topic: " + conf.get(Const.EVENT_IN_TOPIC_CONF_PATH) + ";\n" +
                        " waiting for queries from topic: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH) +
                        " generating event in: " + conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH)
        );

        // loading the CEP engines
        for (String engines: conf.getList(Const.CEP_ENGINES_PATH))
            try {
                Class.forName(engines);
            } catch (ClassNotFoundException e) {
                loggerService.error(e.getMessage(),e);
            }
        //initializing engines
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()  ) {
            List<String> pkgList= conf.getList(Const.AdditionalImportPackage);
            for (String pkgName : pkgList    ) {

                try {
                    CEPEngineAdvanced dfwExtensions =dfw.getAdvancedFeatures();
                    if(dfwExtensions != null)
                        dfwExtensions.loadAdditionalPackages(pkgName);
                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                }
            }

        }

        // TODO: change the loading of feeder the same way as the CEP engines are loaded
        // loading of feeders
        Feeder feederImplEvents = null,  feederImplQuery = null, persistentFeeder=null,testFeeder = null;
        try {

            feederImplEvents = new EventMqttFeederImpl(conf.get(Const.EVENTS_IN_BROKER_CONF_PATH).toString(), conf.get(Const.EVENTS_IN_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.EVENT_IN_TOPIC_CONF_PATH).toString());

            feederImplQuery = new StatementMqttFeederImpl(conf.get(Const.STATEMENT_INOUT_BROKER_CONF_PATH).toString(), conf.get(Const.STATEMENT_INOUT_BROKER_PORT_CONF_PATH).toString(), conf.get(Const.STATEMENT_IN_TOPIC_CONF_PATH).toString());


            persistentFeeder = new PersistenceFeeder(conf.getList(Const.PERSISTENT_DATA_FILE).toArray(new String[conf.getList(Const.PERSISTENT_DATA_FILE).size()]));


            for (CEPEngine wrapper: CEPEngine.instancedEngines.values()) {
                feederImplEvents.dataFusionWrapperSignIn(wrapper);
                feederImplQuery.dataFusionWrapperSignIn(wrapper);
                persistentFeeder.dataFusionWrapperSignIn(wrapper);
                if(conf.getBool(Const.TEST_FEEDER)) {
                    testFeeder = new TestFeeder();
                    testFeeder.dataFusionWrapperSignIn(wrapper);
                }
            }

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
