package eu.linksmart.services.event.core;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.connectors.*;


import eu.linksmart.services.event.connectors.file.BigFileConnector;
import eu.linksmart.services.event.connectors.mqtt.EventMqttObserver;
import eu.linksmart.services.event.connectors.mqtt.StatementMqttObserver;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.services.event.types.BootstrappingBean;
import eu.linksmart.services.event.types.PersistentRequestInstance;
import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by J. Angel Carvajal on 06.10.2014.
 *
 */
public class DataProcessingCore {

 //   protected static List<Feeder> feeders = new ArrayList<>();

    protected static MqttIncomingConnectorService mqtt =null;
    private static Map<String,Pair<String,String>> aliasTopicClass= new HashMap<>();
    private DataProcessingCore() {
    }

    public static boolean isActive() {
        return active;
    }

    protected static boolean active =false;
    protected static boolean started =false;
    protected transient static Configurator conf;
    protected transient static  Logger loggerService;
    /**
     * Run will initialize the Agent and then run the status loop in this thread
     *
     * @param args the parameters provided in the console
     *
     * */
    public static void run(String args){
        if(!started) {
            init(args);
            statusLoop();
        }
    }
    /**
     *  Start will initialize the Agent in this thread and then start the status loop in a new thread. Finally, it will return the result of the initialization process.
     *
     * @param args the parameters provided in the console
     *
     * @return is the agent successfully initialized
     *
     * */
    public static boolean start(String args){
        if(!started) {
            Boolean ret = init(args);
            try {
                ThingsRegistrationService.getReference().startTimer();
            } catch (TraceableException | UntraceableException e) {
                loggerService.error(e.getMessage(),e);
            }
            new Thread(() -> statusLoop()).start();
            return ret;
        }
        return false;

    }
    /**
     * Initialization process of the Agent
     *
     * @param args the parameters provided in the console
     *
     * @return is the agent successfully initialized
     * */
    private static synchronized boolean init(String args){

        started =true;
        System.out.println("\n" +
                "╦   ╦ ╔╗╔ ╦╔═  ╔═╗ ╔╦╗ ╔═╗ ╦═╗ ╔╦╗ R\t╦ ╔═╗ ╔╦╗ \t╔═╗ ╦══ ╔══ ╔╗╔ ╔╦╗ \n" +
                "║   ║ ║║║ ╠╩╗  ╚═╗ ║║║ ╠═╣ ╠╦╝  ║   \t║ ║ ║  ║ \t╠═╣ ║ ╗ ╠══ ║║║  ║ \n" +
                "╩═╝ ╩ ╝╚╝ ╩ ╩  ╚═╝ ╩ ╩ ╩ ╩ ╩╚═  ╩   \t╩ ╚═╝  ╩ \t╩ ╩ ╩═╝ ╚══ ╝╚╝  ╩ \n"+
                ":: LinkSmart ::        (v"+ Utils.getVersion("linksmart.version")+")\n"+
                ":: IoT Agent ::        (v"+ Utils.getVersion()+")\n"    );
        initConf(args);


        loggerService.info("The Agent streaming core version "+ AgentUtils.getVersion()+" is "+(SharedSettings.isFirstLoad()?"":"re")+"starting with ID: " + SharedSettings.getId());

        initCEPEngines();
        intoCEPTypes();
        initForceLoading();
        boolean success = initFeeders();
        bootstrapping();
        // force the loading of the RegistrationService
        ThingsRegistrationService.getReference();

        ServiceRegistratorService.getRegistrator();
        return success;
    }
    /**
     * Tracks the status of the initialized agent
     *
     * */
    // TODO: this status must be improved
    static private void statusLoop(){
        active = true;
        while (active){

            active = mqtt.isUp();
            if(active) {
                loggerService.info("The Agent with ID "+ SharedSettings.getId()+" is alive");
                int hb = 5000;
                try {
                     hb=conf.getInt(Const.LOG_HEARTBEAT_TIME_CONF_PATH);

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
    /**
     *  initConf will initialize the configuration service of the agent. If this the agent will crash.
     *
     * @param args the parameters provided in the console
     *
     * */
    private static void initConf(String args){
        if(args != null)
            Configurator.addConfFile(args);
        else if(AgentUtils.fileExists(Const.APPLICATION_CONFIGURATION_FILE))
            Configurator.addConfFile(Const.APPLICATION_CONFIGURATION_FILE);
        if(System.getenv().containsKey(Const.ENVIRONMENTAL_VARIABLES_ENABLED))
            Configurator.getDefaultConfig().enableEnvironmentalVariables();
        conf = Configurator.getDefaultConfig();

        SharedSettings.setWill(ThingsRegistrationService.getReference().getThingString());
        SharedSettings.setWillTopic(conf.getString(Const.OGC_REGISTRATION_TOPIC_WILL));
        loggerService = LogManager.getLogger(DataProcessingCore.class);
        if(args != null) {
            loggerService.info("Loading configuration form file " + args);

        }else if(AgentUtils.fileExists(Const.APPLICATION_CONFIGURATION_FILE)) {
            loggerService.info("Loading configuration form file " + Const.APPLICATION_CONFIGURATION_FILE);
        }else
            loggerService.info("No configuration conf.cfg file in the class path or as argument at start. Only the defaults values are used");
        if(conf.isEnvironmentalVariablesEnabled())
            loggerService.info("The environmental variables are loaded!");
        String idPath= conf.getString(Const.ID_CONF_PATH);
        if("*".equals(idPath))
            if( conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED) && conf.getBoolean(Const.PERSISTENT_ENABLED)){
                loggerService.error("A persistent agent must have a persistent id (defined in the property "+Const.ID_CONF_PATH+" either in the configuration file or as environmental variable)");
                System.exit(-1);
            }else
                SharedSettings.setIsSet(true);
        else
            SharedSettings.setId(conf.getString(Const.ID_CONF_PATH));


        conf.setProperty(Const.ID_CONF_PATH, SharedSettings.getId());
        // set if this is my first start
        SharedSettings.isIsFirstLoad(!AgentUtils.isFile(PersistentRequestInstance.getPersistentFile()));
    }
    /**
     *  This will bootstrap the agent with data and statements such that the agent has already data or statements pre-loaded.
     *
     * */
    private static void bootstrapping() {
        if(conf.getList(Const.PERSISTENT_DATA_FILE) != null && SharedSettings.isFirstLoad()) {
            FileConnector fileFeeder = new FileConnector((String[]) conf.getList(Const.PERSISTENT_DATA_FILE).toArray(new String[conf.getList(Const.PERSISTENT_DATA_FILE).size()]));

            fileFeeder.loadFiles();
        }
        if(conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED)&& conf.getBoolean(Const.PERSISTENT_ENABLED) && !SharedSettings.isFirstLoad()) {
            PersistenceService fileFeeder = new  PersistenceService(PersistentRequestInstance.getPersistentFile());

            fileFeeder.loadFiles();
            List requests = fileFeeder.getRequests(StatementInstance.class.getCanonicalName());
            if(requests!= null && !requests.isEmpty())
                StatementFeeder.feedStatements(requests);
        }
        if( conf.getStringArray(Const.FeederPayloadAlias) != null) {
            for (String alias: conf.getStringArray(Const.FeederPayloadAlias)) {

                //if(conf.getList(Const.PERSISTENT_EVENTS_FILE+"_"+alias) != null )
                BigFileConnector fileFeeder = null;
                try {
                    if ( conf.getStringArray(Const.PERSISTENT_EVENTS_FILE + "_" + alias) != null) {
                        fileFeeder = new BigFileConnector((Class<? extends EventEnvelope>) Class.forName(aliasTopicClass.get(alias).getRight()), (String[]) conf.getList(Const.PERSISTENT_EVENTS_FILE+"_"+alias).toArray(new String[conf.getList(Const.PERSISTENT_EVENTS_FILE+"_"+alias).size()]));
                        fileFeeder.loadFiles();
                    }
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                }

            }
        }

    }
    /**
     * This function force to load packages by loading the classloader
     *
     * */
    private static void initForceLoading() {
        if(conf.containsKeyAnywhere(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING)) {
            String[] modules = conf.getStringArray(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING);
            loggerService.info("Loading following extensions "+ Arrays.toString(modules));
            Arrays.stream(modules).forEach(cls -> {
                try {
                    if (!"".equals(cls)) {
                        Class c = Class.forName(cls.trim());
                        loggerService.info("Extension: " + c.getSimpleName() + " loaded");
                    }
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(), e);
                    System.exit(-1);
                }
            });
        }
    }

    /**
     * This function initialize the feeders and connectors. By doing this the Network APIs are being set up
     *
     * */
    private static boolean initFeeders() {

        // loading of feeders
        //IncomingConnector mqtt = null;
        try {

            Class.forName(EventFeeder.class.getCanonicalName());
            Class.forName(StatementFeeder.class.getCanonicalName());
            Class.forName(BootstrappingBean.class.getCanonicalName());
            mqtt = MqttIncomingConnectorService.getReference();
            if(conf.getBoolean(Const.TRANSLATOR_MODE))
                StatementFeeder.addNewStatement("{\"name\": \"translator"+"\" ,\"statement\": \"select cast(EventEnvelope.builders.get('default'), eu.linksmart.api.event.types.EventBuilder).refactory(event) as vector from GenericEvent as event\"}",null,null);


            if(conf.getBoolean(Const.START_MQTT_STATEMENT_API))
                mqtt.addListener(conf.getString(Const.STATEMENT_INOUT_BROKER_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#", new StatementMqttObserver(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#"));
            //

           addEventConnection(Arrays.asList(conf.getStringArray(Const.EVENTS_IN_BROKER_CONF_PATH)));


          if (conf.containsKeyAnywhere(Const.ENABLE_REST_API)&&  conf.getBoolean(Const.ENABLE_REST_API))
                    RestInit.init(conf);

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

        if(mqtt!=null&& !mqtt.isUp()){
            loggerService.error("The feeders couldn't start! Agent now is stopping");
            return false;
        }


        return true;
    }
    private static void addEventConnection(String alias, List<String> brokers){
         brokers.forEach(broker->{
                    try {
                       // if(! SharedSettings.existSharedObject(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias))
                            mqtt.addListener(broker, conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), new EventMqttObserver(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias)));
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(),e);
                    }
                });
    }
    public static void addEventConnection( List<String> brokers){
        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> brokers.forEach(broker->addEventConnection(alias,brokers)));
    }
    /**
     * This initialize the CEP engines and their utilities.
     *
     * */
    private static void initCEPEngines() {
        // loading the CEP engines
        for (Object engines: conf.getList(Const.CEP_ENGINES_PATH))
            try {
                Class.forName(engines.toString());
            } catch (ClassNotFoundException e) {
                loggerService.error(e.getMessage(),e);
                System.exit(-1);
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
    /**
     * This initialize the datatypes inside the CEP engines.
     *
     * */
    protected static void intoCEPTypes() {
        // initialize mapping of alias->class set by the configuration
        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> aliasTopicClass.put(alias, new ImmutablePair<>(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), conf.getString(Const.FeederPayloadClass + "_" + alias))));

        // in case no alias is found the configuration the system has nothing to do and terminates all.
        if(aliasTopicClass.isEmpty()){

                InstantiationException e=new InstantiationException(
                        "The configuration parameters of incoming events "
                                +Const.FeederPayloadAlias+" do not have proper topic or class configurations for properties: "
                                +Const.FeederPayloadClass+"_<alias> "+Const.EVENT_IN_TOPIC_CONF_PATH+"_<alias> "
                );
                loggerService.error(e.getMessage(),e);
                System.exit(-1);
            }

        // initialize the types in the engines
        CEPEngine.instancedEngines.values().stream().forEach(engine->
                {
                    aliasTopicClass.forEach((alias, topicClass) -> {
                        try {
                            Class<EventEnvelope> aClass = (Class<EventEnvelope>) Class.forName(topicClass.getRight());
                            aClass.newInstance().setClassTopic(topicClass.getLeft());

                            engine.addEventType(alias, aClass);
                        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                            loggerService.error(e.getMessage(), e);
                        }
                    });
                    engine.addEventType("GenericEvent", EventEnvelope.class);
                }

        );
        // load types as libraries in esper
        aliasTopicClass.values().forEach(pkgName-> CEPEngine.instancedEngines.values().forEach(dfw->
                {
                    if (!"".equals(pkgName.getValue())) {
                        try {
                            CEPEngineAdvanced dfwExtensions = dfw.getAdvancedFeatures();
                            if (dfwExtensions != null) {
                                dfwExtensions.loadAdditionalPackages(pkgName.getValue());
                                loggerService.info("Library : " + pkgName.getValue() + " loaded in "+dfw.getName());
                            }
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(), e);
                        }
                    }

                }
                )

        );
    }
}
