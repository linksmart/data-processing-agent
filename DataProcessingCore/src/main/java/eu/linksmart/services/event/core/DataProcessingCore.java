package eu.linksmart.services.event.core;

import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.connectors.BigFileConnector;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;


import eu.linksmart.services.event.connectors.FileConnector;
import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.connectors.Observers.StatementMqttObserver;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.connectors.RestInit;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.api.event.types.impl.BootstrappingBean;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.utils.serialization.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

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

    public static void run(String args){
        if(!started) {
            init(args);
            statusLoop();
        }
    }

    public static boolean start(String args){
        if(!started) {
            Boolean ret = init(args);
            new Thread(() -> statusLoop()).start();
            return ret;
        }
        return false;

    }
    static protected void statusLoop(){
        active = true;
        while (active){

            active = mqtt.isUp();
            RegistrationService.getReference().startTimer();
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
    private static void initConf(String args){
        if(args != null)
            Configurator.addConfFile(args);
        else if(Utils.fileExists(Const.APPLICATION_CONFIGURATION_FILE))
            Configurator.addConfFile(Const.APPLICATION_CONFIGURATION_FILE);
        if(System.getenv().containsKey(Const.ENVIRONMENTAL_VARIABLES_ENABLED))
            Configurator.getDefaultConfig().enableEnvironmentalVariables();
        conf = Configurator.getDefaultConfig();

        SharedSettings.setWill(RegistrationService.getReference().getThingString());
        SharedSettings.setWillTopic(conf.getString(Const.REGISTRATION_TOPIC_WILL).replace("<id>", SharedSettings.getId()));
        loggerService = Utils.initLoggingConf(DataProcessingCore.class);
        if(args != null) {
            loggerService.info("Loading configuration form file " + args);

        }else if(Utils.fileExists(Const.APPLICATION_CONFIGURATION_FILE)) {
            loggerService.info("Loading configuration form file " + Const.APPLICATION_CONFIGURATION_FILE);
        }else
            loggerService.info("No configuration conf.cfg file in the class path or as argument at start. Only the defaults values are used");
        if(conf.isEnvironmentalVariablesEnabled())
            loggerService.info("The environmental variables are loaded!");
        String idPath= conf.getString(Const.ID_CONF_PATH);
        if("*".equals(idPath))
            SharedSettings.setIsSet(true);
        else
            SharedSettings.setId(conf.getString(Const.ID_CONF_PATH));

        conf.setProperty(Const.ID_CONF_PATH, SharedSettings.getId());
    }

    protected static synchronized boolean init(String args){
        started =true;
        initConf(args);


        loggerService.info("The Agent streaming core version "+Utils.getVersion()+" is starting with ID: " + SharedSettings.getId());

        initCEPEngines();
        initSharedSettings();
        intoCEPTypes();
        initForceLoading();
        boolean success = initFeeders();
        bootstrapping();
        // force the loading of the RegistrationService
        RegistrationService.getReference();
        return success;
    }

    private static void initSharedSettings() {
        try {
            if (conf.containsKeyAnywhere(Const.SERIALIZER_PROVIDER))
                // in case the serialization provider is given, exists, and known we load it; else we load default.
                if (conf.getString(Const.SERIALIZER_PROVIDER).equals("JWS")) {
                    SharedSettings.setSerializer(new JWSSerializer(new DefaultSerializer()));

                    loggerService.info("Loading JWS serializer");
                } else
                    loggerService.info("Loading default serializer"); // default serialized is not replaced
            if (conf.containsKeyAnywhere(Const.DESERIALIZER_PROVIDER))
                if (conf.getString(Const.DESERIALIZER_PROVIDER).equals("JWS")) {
                    SharedSettings.setDeserializer(new JWSDeserializer(Base64.getEncoder().encodeToString(((JWSSerializer) SharedSettings.getSerializer()).getPublicKey().getEncoded())));

                    loggerService.info("Loading JWS deserializer");
                } else
                    loggerService.info("Loading default deserializer");// default serialized is not replaced
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            loggerService.info("Loading default (de)serializers");// default serialized is not replaced
        }

    }

    private static void bootstrapping() {
        if(conf.getList(Const.PERSISTENT_DATA_FILE) != null ) {
            FileConnector fileFeeder = new FileConnector((String[]) conf.getList(Const.PERSISTENT_DATA_FILE).toArray(new String[conf.getList(Const.PERSISTENT_DATA_FILE).size()]));

            fileFeeder.loadFiles();
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

    private static void initForceLoading() {
        if(conf.containsKeyAnywhere(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING)) {
            String[] modules = conf.getStringArray(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING);
            loggerService.info("Loading following extensions "+ Arrays.toString(modules));
            Arrays.stream(modules).forEach(cls -> {
                try {
                    if (!"".equals(cls)) {
                        Class c = Class.forName(cls);
                        loggerService.info("Extension: " + c.getSimpleName() + " loaded");
                    }
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(), e);
                }
            });
        }
    }


    private static boolean initFeeders() {

        // loading of feeders
        //IncomingConnector mqtt = null;
        try {

            Class.forName(EventFeeder.class.getCanonicalName());
            Class.forName(StatementFeeder.class.getCanonicalName());
            Class.forName(BootstrappingBean.class.getCanonicalName());
            mqtt = MqttIncomingConnectorService.getReference();


            if(conf.getBoolean(Const.START_MQTT_STATEMENT_API))
                mqtt.addAddListener(conf.getString(Const.STATEMENT_INOUT_BROKER_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#", new StatementMqttObserver(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#"));
            //

            Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                    .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                    .forEach(alias -> Arrays.asList(conf.getStringArray(Const.EVENTS_IN_BROKER_CONF_PATH)).forEach(broker->{
                        try {
                            mqtt.addAddListener(broker, conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), new EventMqttObserver(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias)));
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(),e);
                        }
                    }));



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
    protected static void intoCEPTypes() {

        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> aliasTopicClass.put(alias, new ImmutablePair<>(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), conf.getString(Const.FeederPayloadClass + "_" + alias))));

        if(aliasTopicClass.isEmpty())
            try {
                throw new InstantiationException(
                        "The configuration parameters of incoming events "
                                +Const.FeederPayloadAlias+" do not have proper topic or class configurations for properties: "
                                +Const.FeederPayloadClass+"_<alias> "+Const.EVENT_IN_TOPIC_CONF_PATH+"_<alias> "
                );
            } catch (InstantiationException e) {
                loggerService.error(e.getMessage(),e);
            }

        CEPEngine.instancedEngines.values().stream().forEach(engine->aliasTopicClass.forEach((alias,topicClass)-> {
                    try {
                        Class aClass = Class.forName(topicClass.getRight());

                        engine.addEventType(alias,aClass);
                    } catch (ClassNotFoundException e) {
                        loggerService.error(e.getMessage(), e);
                    }
                })

        );
    }
}
