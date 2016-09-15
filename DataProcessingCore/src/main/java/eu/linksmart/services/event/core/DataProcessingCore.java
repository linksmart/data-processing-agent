package eu.linksmart.services.event.core;

import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;


import eu.linksmart.services.event.connectors.FileConnector;
import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.connectors.Observers.StatementMqttObserver;
import eu.linksmart.services.event.feeder.EventFeeder;
import eu.linksmart.services.event.feeder.StatementFeeder;
import eu.linksmart.api.event.types.impl.BootstrappingBean;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.event.intern.Const;
import org.slf4j.Logger;

import java.util.List;

/**
 * Created by J. Angel Caravajal on 06.10.2014.
 *
 */
public class DataProcessingCore {

 //   protected static List<Feeder> feeders = new ArrayList<>();

    protected static MqttIncomingConnectorService mqtt =null;
    private DataProcessingCore() {
    }

    public static boolean isActive() {
        return active;
    }

    protected static boolean active =false;
    protected transient static Configurator conf;
    protected transient static  Logger loggerService;

    public static void run(String args){

         init(args);
        statusLoop();
    }

    public static boolean start(String args){
        Boolean ret =init(args);
        new Thread(() ->statusLoop()).start();
        return ret;

    }
    static protected void statusLoop(){
        active = true;
        while (active){

            active = mqtt.isUp();

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
    private static void initConf(String args){
        if(args != null) {
            Configurator.addConfFile(args);

        }else
            Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();

        loggerService = Utils.initLoggingConf(DataProcessingCore.class);

        String idPath= conf.getString(Const.ID_CONF_PATH);
        if("*".equals(idPath))
            DynamicConst.setIsSet(true);
        else
            DynamicConst.setId(conf.getString(Const.ID_CONF_PATH));
    }
    protected static boolean init(String args){
        
        initConf(args);
       

        loggerService.info(
                "The Data-Fusion Manager is starting with ID: " + DynamicConst.getId().toString() + ";\n" +
                        " with incoming events in broker tcp://" + conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH) + ":" + conf.getString(Const.EVENTS_IN_BROKER_PORT_CONF_PATH) +
                        " waiting for events from the topic: " + conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH) + ";\n" +
                        " waiting for queries from topic: " + conf.getString(Const.STATEMENT_IN_TOPIC_CONF_PATH) +
                        " generating event in: " + conf.getString(Const.STATEMENT_IN_TOPIC_CONF_PATH)
        );

        initCEPEngines();
        initFeeders();
        
        initForceLoading();
        return initFeeders();
    }

    private static void initForceLoading() {
        if(conf.containsKey(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING))
            conf.getList(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING).forEach(cls -> {
                try {
                    Class c =Class.forName(cls.toString());
                    loggerService.info("Loading extension: "+c.getSimpleName());
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(),e);
                }
            });
    }


    private static boolean initFeeders() {

        // loading of feeders
        //IncomingConnector mqtt = null;
        try {
            Class.forName(EventFeeder.class.getCanonicalName());
            Class.forName(StatementFeeder.class.getCanonicalName());
            Class.forName(BootstrappingBean.class.getCanonicalName());
            mqtt = MqttIncomingConnectorService.getReference();

            mqtt.addAddListener(conf.getString(Const.STATEMENT_INOUT_BROKER_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BROKER_PORT_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH), new StatementMqttObserver());
            mqtt.addAddListener(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH),conf.getString(Const.EVENTS_IN_BROKER_PORT_CONF_PATH),conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH), new EventMqttObserver());

            FileConnector persistentFeeder = new FileConnector((String[]) conf.getList(Const.PERSISTENT_DATA_FILE).toArray(new String[conf.getList(Const.PERSISTENT_DATA_FILE).size()]));

            persistentFeeder.loadFiles();


        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

        if(mqtt!=null&& !mqtt.isUp()){
            loggerService.error("The feeders couldn't start! Agent now is stopping");
            return false;
        }


        return true;
    }

    private static void initCEPEngines() {
        // loading the CEP engines
        for (Object engines: conf.getList(Const.CEP_ENGINES_PATH))
            try {
                Class.forName(engines.toString());
            } catch (ClassNotFoundException e) {
                loggerService.error(e.getMessage(),e);
            }
        //initializing engines
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()  ) {
            List pkgList= conf.getList(Const.AdditionalImportPackage);
            for (Object pkgName : pkgList    ) {

                try {
                    CEPEngineAdvanced dfwExtensions =dfw.getAdvancedFeatures();
                    if(dfwExtensions != null)
                        dfwExtensions.loadAdditionalPackages(pkgName.toString());
                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                }
            }

        }

    }

}
