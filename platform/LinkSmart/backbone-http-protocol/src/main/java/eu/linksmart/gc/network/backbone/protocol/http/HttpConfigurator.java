package eu.linksmart.gc.network.backbone.protocol.http;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.utils.Configurator;

import org.osgi.service.cm.ConfigurationAdmin;

public class HttpConfigurator extends Configurator {

	/* Configuration PID & file path. */
	public static String BACKBONE_PID = "eu.linksmart.gc.network.backbone.protocol.http";
	public static String CONFIGURATION_FILE = "/http.properties";

	public static final String BACKBONE_DESCRIPTION = "BackboneHTTP.description";
	
	//see enum SecurityProperty in API, file SecurityProperty.java
	//and configuration section in /zmq.properties
	public static final String SECURITY_PARAMETERS = "BackboneHTTP.SecurityParameters";

	private HttpImpl bbHttpImpl;

	/**
	 * Log4j logger of this class
	 */
	private static Logger LOGGER = Logger.getLogger(HttpConfigurator.class.getName());


    /**
     * Initializes the HTTP backbone configurator.
     *
     * @param bbHttpImpl
     *            instantiation of HTTP backbone
     * @param context
     *            A bundle context
     * @param configurationAdmin configAdmin reference for proper setup*
     */
    public HttpConfigurator(HttpImpl bbHttpImpl,
                                    BundleContext context, ConfigurationAdmin configurationAdmin) {
        super(context, LOGGER, BACKBONE_PID, CONFIGURATION_FILE, configurationAdmin);
        super.init();
        this.bbHttpImpl = bbHttpImpl;
    }

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public void applyConfigurations(Hashtable updates) {
		this.bbHttpImpl.applyConfigurations(updates);
	}

}
