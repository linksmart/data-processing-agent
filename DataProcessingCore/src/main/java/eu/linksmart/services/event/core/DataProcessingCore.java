package eu.linksmart.services.event.core;

import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.connectors.FileConnector;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.connectors.PersistenceService;
import eu.linksmart.services.event.connectors.RestInit;
import eu.linksmart.services.event.connectors.file.BigFileConnector;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.types.BootstrappingBean;
import eu.linksmart.services.event.types.PersistentRequestInstance;
import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J. Angel Carvajal on 06.10.2014.
 */
public class DataProcessingCore {

    //   protected static List<Feeder> feeders = new ArrayList<>();

    private static Map<String, Pair<String, String>> aliasTopicClass = new HashMap<>();

    private DataProcessingCore() {
    }

    public static boolean isActive() {
        return active;
    }

    protected static boolean active = false;
    protected static boolean started = false;
    protected transient static Configurator conf;
    protected transient static Logger loggerService;

    /**
     * Run will initialize the Agent and then run the status loop in this thread
     *
     * @param args the parameters provided in the console
     */
    public static void run(String args) {
        if (!started) {
            init(args);
            statusLoop();
        }
    }

    /**
     * Start will initialize the Agent in this thread and then start the status loop in a new thread. Finally, it will return the result of the initialization process.
     *
     * @param args the parameters provided in the console
     * @return is the agent successfully initialized
     */
    public static void start(String args) {
        if (!started) {
            init(args);
            try {
                ThingsRegistrationService.getReference().startTimer();
            } catch (TraceableException | UntraceableException e) {
                loggerService.error(e.getMessage(), e);
            }
            new Thread(() -> statusLoop()).start();
        }
    }

    /**
     * Initialization process of the Agent
     *
     * @param args the parameters provided in the console
     * @return is the agent successfully initialized
     */
    private static synchronized void init(String args) {

        started = true;
        System.out.println("\n" +
                "╦   ╦ ╔╗╔ ╦╔═  ╔═╗ ╔╦╗ ╔═╗ ╦═╗ ╔╦╗ R\t╦ ╔═╗ ╔╦╗ \t╔═╗ ╦══ ╔══ ╔╗╔ ╔╦╗ \n" +
                "║   ║ ║║║ ╠╩╗  ╚═╗ ║║║ ╠═╣ ╠╦╝  ║   \t║ ║ ║  ║ \t╠═╣ ║ ╗ ╠══ ║║║  ║ \n" +
                "╩═╝ ╩ ╝╚╝ ╩ ╩  ╚═╝ ╩ ╩ ╩ ╩ ╩╚═  ╩   \t╩ ╚═╝  ╩ \t╩ ╩ ╩═╝ ╚══ ╝╚╝  ╩ \n" +
                ":: LinkSmart ::        (v" + Utils.getVersion("linksmart.version") + ")\n" +
                ":: IoT Agent ::        (v" + Utils.getVersion() + ")\n");
        initConf(args);

        loggerService.info("The Agent streaming core version " + AgentUtils.getVersion() + " is " + (SharedSettings.isFirstLoad() ? "" : "re") + "starting with ID: " + SharedSettings.getId());

        initForceLoading(Const.PRE_CEP_EXTENSIONS);
        initCEPEngines();
        initForceLoading(Const.PRE_TYPES_EXTENSIONS);
        intoCEPTypes();
        initForceLoading(Const.PRE_FEEDERS_EXTENSIONS);
        // legacy equivalent before feeders
        initForceLoading(Const.ADDITIONAL_CLASS_TO_BOOTSTRAPPING);
        initFeeders();
        initForceLoading(Const.PRE_CONNECTORS_EXTENSIONS);
        initConnectors();
        initForceLoading(Const.PRE_BOOTSTRAP_EXTENSIONS);
        bootstrapping();
        initForceLoading(Const.PRE_END_EXTENSIONS);
        // force the loading of the RegistrationService
        // ThingsRegistrationService.getReference();

        // ServiceRegistratorService.getRegistrator();
    }

    /**
     * Tracks the status of the initialized agent
     */
    // TODO: this status must be improved
    static private void statusLoop() {
        active = true;
        while (active) {
            if (active) {
                loggerService.debug("The Agent with ID " + SharedSettings.getId() + " is alive");
                int hb = 5000;
                try {
                    hb = conf.getInt(Const.LOG_HEARTBEAT_TIME_CONF_PATH);
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
     * initConf will initialize the configuration service of the agent. If this the agent will crash.
     *
     * @param args the parameters provided in the console
     */
    private static void initConf(String args) {
        if (args != null)
            Configurator.addConfFile(args);
        else if (AgentUtils.fileExists(Const.APPLICATION_CONFIGURATION_FILE))
            Configurator.addConfFile(Const.APPLICATION_CONFIGURATION_FILE);
        if (System.getenv().containsKey(Const.ENVIRONMENTAL_VARIABLES_ENABLED))
            Configurator.getDefaultConfig().enableEnvironmentalVariables();
        conf = Configurator.getDefaultConfig();

        SharedSettings.setWill(ThingsRegistrationService.getReference().getThingString());
        SharedSettings.setWillTopic(conf.getString(Const.OGC_REGISTRATION_TOPIC_WILL));
        loggerService = LogManager.getLogger(DataProcessingCore.class);
        if (args != null) {
            loggerService.info("Loading configuration form file " + args);
        } else if (AgentUtils.fileExists(Const.APPLICATION_CONFIGURATION_FILE)) {
            loggerService.info("Loading configuration form file " + Const.APPLICATION_CONFIGURATION_FILE);
        } else
            loggerService.info("No configuration conf.cfg file in the class path or as argument at start. Only the defaults values are used");
        if (conf.isEnvironmentalVariablesEnabled())
            loggerService.info("The environmental variables are loaded!");
        String idPath = conf.getString(Const.ID_CONF_PATH);
        if ("*".equals(idPath))
            if (conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED) && conf.getBoolean(Const.PERSISTENT_ENABLED)) {
                loggerService.error("A persistent agent must have a persistent id (defined in the property " + Const.ID_CONF_PATH + " either in the configuration file or as environmental variable)");
                System.exit(-1);
            } else
                SharedSettings.setIsSet(true);
        else
            SharedSettings.setId(conf.getString(Const.ID_CONF_PATH));

        conf.setProperty(Const.ID_CONF_PATH, SharedSettings.getId());
        // set if this is my first start
        SharedSettings.isIsFirstLoad(!AgentUtils.isFile(PersistentRequestInstance.getPersistentFile()));
    }

    /**
     * This will bootstrap the agent with data and statements such that the agent has already data or statements pre-loaded.
     */
    private static void bootstrapping() {
        if (conf.getList(Const.PERSISTENT_DATA_FILE) != null && SharedSettings.isFirstLoad()) {
            FileConnector fileFeeder = new FileConnector((String[]) conf.getList(Const.PERSISTENT_DATA_FILE).toArray(new String[conf.getList(Const.PERSISTENT_DATA_FILE).size()]));

            fileFeeder.loadFiles();
        }
        if (conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED) && conf.getBoolean(Const.PERSISTENT_ENABLED) && !SharedSettings.isFirstLoad()) {
            PersistenceService fileFeeder = new PersistenceService(PersistentRequestInstance.getPersistentFile());

            fileFeeder.loadFiles();
            List requests = fileFeeder.consumeRequests(StatementInstance.class.getCanonicalName());
            if (requests != null && !requests.isEmpty())
                StatementFeeder.feedStatements(requests);
        }
        if (conf.getStringArray(Const.FeederPayloadAlias) != null) {
            for (String alias : conf.getStringArray(Const.FeederPayloadAlias)) {

                //if(conf.getList(Const.PERSISTENT_EVENTS_FILE+"_"+alias) != null )
                BigFileConnector fileFeeder = null;
                try {
                    if (conf.getStringArray(Const.PERSISTENT_EVENTS_FILE + "_" + alias) != null) {
                        fileFeeder = new BigFileConnector((Class<? extends EventEnvelope>) Class.forName(aliasTopicClass.get(alias).getRight()), (String[]) conf.getList(Const.PERSISTENT_EVENTS_FILE + "_" + alias).toArray(new String[conf.getList(Const.PERSISTENT_EVENTS_FILE + "_" + alias).size()]));
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
     */
    private static void initForceLoading(String toLoad) {
        if (conf.containsKeyAnywhere(toLoad) && conf.getStringArray(toLoad).length > 0) {
            String[] modules = conf.getStringArray(toLoad);
            loggerService.info("Loading following extensions " + Arrays.toString(modules));
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
     */
    private static void initFeeders() {

        // loading of feeders
        //IncomingConnector mqtt = null;
        try {

            Class.forName(EventFeeder.class.getCanonicalName());
            Class.forName(StatementFeeder.class.getCanonicalName());
            Class.forName(BootstrappingBean.class.getCanonicalName());

            if (conf.getBoolean(Const.TRANSLATOR_MODE))
                StatementFeeder.addNewStatement("{\"name\": \"translator" + "\" ,\"statement\": \"select cast(EventEnvelope.builders.get('default'), eu.linksmart.api.event.types.EventBuilder).refactory(event) as vector from GenericEvent as event\"}", null, null);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    private static void initConnectors() {

        try {

            Class.forName(MqttIncomingConnectorService.class.getCanonicalName());
            Class.forName(RestInit.class.getCanonicalName());

            if (conf.getBoolean(Const.TRANSLATOR_MODE))
                StatementFeeder.addNewStatement("{\"name\": \"translator" + "\" ,\"statement\": \"select cast(EventEnvelope.builders.get('default'), eu.linksmart.api.event.types.EventBuilder).refactory(event) as vector from GenericEvent as event\"}", null, null);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
    }

    /**
     * This initialize the CEP engines and their utilities.
     */
    private static void initCEPEngines() {
        // loading the CEP engines
        try {
            Class.forName(conf.getString(Const.CEP_ENGINE).toString());
        } catch (ClassNotFoundException e) {
            loggerService.error(e.getMessage(), e);
            System.exit(-1);
        }
        //initializing engines
        List pkgList = conf.getList(Const.AdditionalImportPackage);
        for (Object pkgName : pkgList) {

            try {
                CEPEngineAdvanced dfwExtensions = CEPEngine.instancedEngine.getValue().getAdvancedFeatures();
                if (dfwExtensions != null)
                    dfwExtensions.loadAdditionalPackages(pkgName.toString());
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }
    }

    /**
     * This initialize the datatypes inside the CEP engines.
     */
    protected static void intoCEPTypes() {
        // initialize mapping of alias->class set by the configuration
        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> aliasTopicClass.put(alias, new ImmutablePair<>(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), conf.getString(Const.FeederPayloadClass + "_" + alias))));

        // in case no alias is found the configuration the system has nothing to do and terminates all.
        if (aliasTopicClass.isEmpty()) {

            InstantiationException e = new InstantiationException(
                    "The configuration parameters of incoming events "
                            + Const.FeederPayloadAlias + " do not have proper topic or class configurations for properties: "
                            + Const.FeederPayloadClass + "_<alias> " + Const.EVENT_IN_TOPIC_CONF_PATH + "_<alias> "
            );
            loggerService.error(e.getMessage(), e);
            System.exit(-1);
        }

        // initialize the types in the engines
        aliasTopicClass.forEach((alias, topicClass) -> {
            try {
                Class<EventEnvelope> aClass = (Class<EventEnvelope>) Class.forName(topicClass.getRight());
                aClass.newInstance().setClassTopic(topicClass.getLeft());

                CEPEngine.instancedEngine.getValue().addEventType(alias, aClass);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                loggerService.error(e.getMessage(), e);
            }
        });
        CEPEngine.instancedEngine.getValue().addEventType("GenericEvent", EventEnvelope.class);

        // load types as libraries in esper
        aliasTopicClass.values().forEach(pkgName ->
                {
                    if (!"".equals(pkgName.getValue())) {
                        try {
                            CEPEngineAdvanced dfwExtensions = CEPEngine.instancedEngine.getValue().getAdvancedFeatures();
                            if (dfwExtensions != null) {
                                dfwExtensions.loadAdditionalPackages(pkgName.getValue());
                                loggerService.info("Library : " + pkgName.getValue() + " loaded in " + CEPEngine.instancedEngine.getValue().getName());
                            }
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(), e);
                        }
                    }
                }
        );
    }
}
