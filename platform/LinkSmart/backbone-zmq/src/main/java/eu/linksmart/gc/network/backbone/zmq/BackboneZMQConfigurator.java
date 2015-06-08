package eu.linksmart.gc.network.backbone.zmq;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.utils.Configurator;

import org.osgi.service.cm.ConfigurationAdmin;

public class BackboneZMQConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BACKBONE_PID = "eu.linksmart.gc.network.backbone.zmq";
	public static String CONFIGURATION_FILE = "/zmq.properties";

	public static final String BACKBONE_DESCRIPTION = "backbone.description";
	public static final String BACKBONE_ZMQ_XPUB_URI = "backbone.zmq.xpub.uri";
	public static final String BACKBONE_ZMQ_XSUB_URI = "backbone.zmq.xsub.uri";
	public static final String BACKBONE_ZMQ_HEARTBEAT_INTERVAL = "backbone.zmq.heartbeat.interval";
	
	//see enum SecurityProperty in API, file SecurityProperty.java
	//and configuration section in /zmq.properties
	public static final String SECURITY_PARAMETERS = "BackboneJXTA.SecurityParameters";

	private BackboneZMQImpl bbZmqAImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger LOGGER = Logger.getLogger(BackboneZMQConfigurator.class.getName());


    /**
     * Initializes the ZMQ backbone configurator.
     *
     * @param bbZmqAImpl
     *            instantiation of ZMQ backbone
     * @param context
     *            A bundle context
     * @param configurationAdmin configAdmin reference for proper setup*
     */
    public BackboneZMQConfigurator(BackboneZMQImpl bbZmqAImpl,
                                    BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, LOGGER, BACKBONE_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.bbZmqAImpl = bbZmqAImpl;
    }

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		this.bbZmqAImpl.applyConfigurations(updates);
	}

}
