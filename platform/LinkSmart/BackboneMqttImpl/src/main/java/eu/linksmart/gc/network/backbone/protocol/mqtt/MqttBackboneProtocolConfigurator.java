package eu.linksmart.gc.network.backbone.protocol.mqtt;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.utils.Configurator;

import org.osgi.service.cm.ConfigurationAdmin;

public class MqttBackboneProtocolConfigurator extends Configurator {


	public static String BACKBONE_PID = "eu.linksmart.gc.network.backbone.protocol.mqtt";
	public static String CONFIGURATION_FILE = "/mqttprotocol.properties";

	public static final String BACKBONE_DESCRIPTION = "BackboneMQTT.description";

    public static final String SECURITY_PARAMETERS = "BackboneMQTT.SecurityParameters";



    public static String QoS = "BackboneMQTT.MQTT.QoS";

    public static String PERSISTENCE = "BackboneMQTT.MQTT.Persistence";

    public static String BROADCAST = "BackboneMQTT.Broadcast";

    public static String ALLOWED_LOCAL_MESSAGING_LOOP = "BackboneMQTT.AllowingLocalLoop";

    public static String SUBSCRIBE_TO = "BackboneMQTT.mapping.SubscribeTo";
    public static String PUBLISH_TO = "BackboneMQTT.mapping.PublishTo";
    public static String UNSUBSCRIBE_TO = "BackboneMQTT.mapping.UnsubscribeTo";
    public static String RESUBSCRIBE_TO = "BackboneMQTT.mapping.ReubscribeTo";

    public static String MESSAGE_CONTROL_CLEANER_TIMEOUT = "BackboneMQTT.Timeout";

    public static String MESSAGE_REPETITION_CONTROL = "BackboneMQTT.advance.MessageRepetitionControl";

    public static String BROKER_NAME = "BackboneMQTT.MQTT.BrokerName";

    public static String BROKER_PORT = "BackboneMQTT.MQTT.BrokerPort";


	private MqttBackboneProtocolImpl bbMqttImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger LOGGER = Logger.getLogger(MqttBackboneProtocolConfigurator.class.getName());


    /**
     * Initializes the HTTP backbone configurator.
     *
     * @param bbMqttImpl
     *            instantiation of MQTT backbone
     * @param context
     *            A bundle context
     * @param configurationAdmin configAdmin reference for proper setup*
     */
    public MqttBackboneProtocolConfigurator(MqttBackboneProtocolImpl bbMqttImpl,
                                            BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, LOGGER, BACKBONE_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.bbMqttImpl = bbMqttImpl;
    }

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {

        this.bbMqttImpl.applyConfigurations(updates);
        // add update capabilities
	}

}
