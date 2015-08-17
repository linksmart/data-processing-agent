package eu.linksmart.gc.network.backbone.protocol.mqtt.conf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import eu.linksmart.gc.api.types.Configurable;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.utils.Configurator;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
/**/

public class MqttBackboneProtocolConfigurator extends Configurator{


    private Logger LOG = Logger.getLogger(MqttBackboneProtocolConfigurator.class.getName());

	public static String BACKBONE_PID = "eu.linksmart.gc.network.backbone.protocol.mqtt";
	public static String CONFIGURATION_FILE = /*"./etc/MQTTBackboneProtocol.cfg";//*/"/mqttprotocol.properties";

	public static final String BACKBONE_DESCRIPTION = "BackboneMQTT.description";

    public static final String SECURITY_PARAMETERS = "BackboneMQTT.SecurityParameters";

    public static final String BROKER_AS_SERVICE = "BackboneMQTT.BrokerAsService";

    public static final String QoS = "BackboneMQTT.MQTT.QoS";

    public static final String PERSISTENCE = "BackboneMQTT.MQTT.Persistence";

    public static final String BROADCAST_TOPIC = "BackboneMQTT.Broadcast.Topic";

    public static final String ALLOWED_LOCAL_MESSAGING_LOOP = "BackboneMQTT.AllowingLocalLoop";

    public static final String SUBSCRIBE_TO = "BackboneMQTT.mapping.SubscribeTo";
    public static String PUBLISH_TO = "BackboneMQTT.mapping.PublishTo";
    public static String UNSUBSCRIBE_TO = "BackboneMQTT.mapping.UnsubscribeTo";
    public static String RESUBSCRIBE_TO = "BackboneMQTT.mapping.ReubscribeTo";

    public static String MESSAGE_CONTROL_CLEANER_TIMEOUT = "BackboneMQTT.Timeout";

    public static String MESSAGE_REPETITION_CONTROL = "BackboneMQTT.advance.MessageRepetitionControl";

    public static String BROKER_NAME = "BackboneMQTT.MQTT.BrokerName";

    public static String BROKER_PORT = "BackboneMQTT.MQTT.BrokerPort";

    public static final String BROADCAST_PROPAGATION ="BackboneMQTT.Broadcast.PropagationFeature";

    private static final String FELIX_CONST_FILENAME = "felix.fileinstall.filename";

	private Configurable bbMqttImpl;
    private ServiceRegistration myService;

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
    public MqttBackboneProtocolConfigurator(Configurable bbMqttImpl,
                                            BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, LOGGER, BACKBONE_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.bbMqttImpl = bbMqttImpl;
        Hashtable <String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, "MQTTBackboneProtocol");
        myService = context.registerService (ManagedService.class.getName(),this , properties);

    }

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 *
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {

        if(updates.containsKey(FELIX_CONST_FILENAME))
            CONFIGURATION_FILE = updates.get(FELIX_CONST_FILENAME).toString();

        this.bbMqttImpl.applyConfigurations(updates);
        // add update capabilitie/*s
	}
    public void deregister(){
        if(myService!=null)
            myService.unregister();
        myService = null;
    }

}
