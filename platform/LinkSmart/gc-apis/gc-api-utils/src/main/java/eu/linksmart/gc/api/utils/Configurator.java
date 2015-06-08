/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Implementation of a Managed Service, that is a service that needs 
 * configuration data
 */

package eu.linksmart.gc.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public abstract class Configurator implements ManagedService {

	public Dictionary configuration = null;
	private ConfigurationAdmin cm = null;
	private ServiceRegistration managedServiceReg;
	public Hashtable unsavedConfigs = new Hashtable();

	public BundleContext context;
	public Logger logger;
	/** Configuration PID */
	public String pid;
	/** Default Configuration Location */
	public String configurationFilePath;

	/**
	 * Constructor
	 * @param context the context
	 * @param logger the logger
	 * @param pid the PID
	 * @param configurationFilePath the configuration file path
	 */
	public Configurator(BundleContext context, Logger logger, 
			String pid, String configurationFilePath) {
		this.context = context;
		this.pid = pid;
		this.configurationFilePath = configurationFilePath;
		this.logger = logger;

//		ServiceReference ref = context.getServiceReference(
//				ConfigurationAdmin.class.getName());
//		if (ref != null) {
//			this.cm = (ConfigurationAdmin) context.getService(ref);
//		}

//		init();
//		initiated = true;
	}
	
	public Configurator(BundleContext context, Logger logger, 
			String pid, String configurationFilePath, ConfigurationAdmin cm) {
		this.context = context;
		this.pid = pid;
		this.configurationFilePath = configurationFilePath;
		this.logger = logger;
		this.cm = cm;
	}
	
	/**
	 * Sets the Configuration Admin, thats is a service for administer 
	 * configuration data
	 * 
	 * @param cm the configuration admin
	 */
	public final void setConfigurationAdmin(ConfigurationAdmin cm) {
		this.cm = cm;
//		if (initiated) {
//			setConfiguration(null, null);
//		}
	}

	/**
	 * Unsets the Configuration Admin.
	 * 
	 * @param admin not used
	 */
	public final void unsetConfigurationAdmin(ConfigurationAdmin admin) {
		if (managedServiceReg!=null) {
			managedServiceReg.unregister();
		}
		this.cm = null;
	}

	/**
	 * Initializes the configuration of the framework
	 */
	public void init() {
		try {
			if (cm != null) {
				// We check if there is already a persistent configuration
				// in the framework.
				Configuration config = cm.getConfiguration(pid);
				configuration = config.getProperties();

				// If there is no configuration, we will use the properties 
				// file in the jar
				if (configuration == null) {
					logger.info("No persistent configuration for PID " + pid + " available");
					configuration = loadDefaults();
					config.update(configuration);
				}
			}
			else {
				// If the configuration admin is not yet available, just use the 
				// default configuration from the Jar
				logger.warn("Configuration=" + pid + " - ConfigurationAdmin not found... loading defaults...");
				configuration = loadDefaults();
			}
		} catch (IOException e) {
			logger.error("Error during configuration load!", e);
			// TODO: What should we do here? Can we also load the defaults?
		}
	}

	/**
	 * Registers a configuration
	 */
	public final void registerConfiguration() {
		if (cm != null) {
			Dictionary d  = new Hashtable();
			d.put(Constants.SERVICE_PID, pid);
			managedServiceReg = context.registerService(ManagedService.class.getName(), this, d);
			logger.info("registering configuration for PID [" + pid + "]");
		}
	}

	/**
	 * Stop method 
	 */
	public void stop() {
		managedServiceReg.unregister();
//		initiated = false;
		configuration = null;
		unsavedConfigs = null;
	}

	/**
	 * Updates the configuration with the properties parameter
	 * 
	 * @param properties a dictionary with configuration pairs (key, value)
	 */
	public synchronized final void updated(Dictionary properties) 
			throws ConfigurationException {

		if (properties == null) {
			// The configuration has been deleted, load the default one 
			// (internally from the JAR)
			try {
				Configuration config = cm.getConfiguration(pid);

				configuration = config.getProperties();
				if (configuration == null) {
					configuration = loadDefaults();
					config.update(configuration);
					return;
				} else {
					//ignore the update as configuration is still available
					return;
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				return;
			}
		} else {
			Hashtable updates = ConfigurationTools.getConfigurationChanges(
					this.configuration, properties);

			// We update now the configuration with the new configuration dictionary
			this.configuration = properties;
			if (!updates.isEmpty()) {
				// If there is an update in properties, we update our bundle's 
				// configuration
				applyConfigurations(updates);
			}	
		}
	}

	/**
	 * Gets the configuration
	 * 
	 * @return the configuration
	 */
	public Dictionary getConfiguration() {
		return configuration;
	}

	/**
	 * Apply the configuration changes
	 * 
	 * @param updates the configuration changes
	 */
	public abstract void applyConfigurations(Hashtable updates);
	
	/**
	 * Sets a new configuration with a given configuration in the configuration
	 * 
	 * @param Properties props new configuration 
	 */
	public synchronized final void setConfiguration(Properties props) {
		Configuration config;

		if (cm == null) {
			if (configuration == null) {
				configuration = loadDefaults();
			}

			for(Object key : props.keySet()) {
				configuration.put(key, props.get(key));
				unsavedConfigs.put(key, props.get(key));
				logger.error("setConfiguration: Could not save configuration "
						+ "properties into CM. CM not available");
				return;
			}
		}

		if (props != null) {
			for(Object key : props.keySet()) {
				configuration.put(key, props.get(key));
				unsavedConfigs.put(key, props.get(key));
			}
		}

		try {
			config = cm.getConfiguration(pid);
			Dictionary storedConfig = null;
			if (config != null) {
				config.getProperties();
			}

			if (storedConfig == null) {
				/*
				 * Only when there is still no configuration, take the current 
				 * one (will have already the unsaved properties)
				 */
				storedConfig = configuration;
			}
			else {
				Enumeration unsaved = unsavedConfigs.keys();
				while (unsaved.hasMoreElements()) {
					String k = (String) unsaved.nextElement();
					storedConfig.put(k, unsavedConfigs.get(k));
				}
			}

			if (config != null) {
				config.update(storedConfig);
			}

			unsavedConfigs.clear();
			logger.info("setConfiguration: Saved configuration into CM");
		} catch (IOException e) {
			logger.error("setConfiguration: Could not save configuration "
					+ "properties into CM");
			e.printStackTrace();
		}
	}

	/**
	 * Sets a new configuration pair (key, value) in the configuration
	 * 
	 * @param key key value
	 * @param value value value
	 */
	public synchronized final void setConfiguration(String key, String value) {
		Configuration config;

		if (cm == null) {
			if (configuration == null) {
				configuration = loadDefaults();
			}
			configuration.put(key, value);
			unsavedConfigs.put(key, value);
			logger.error("setConfiguration: Could not save configuration "
					+ "properties into CM. CM not available");
			return;
		}

		if (key != null) {
			unsavedConfigs.put(key, value);
			configuration.put(key, value);
		}

		try {
			config = cm.getConfiguration(pid);
			if (config != null) {
				config.getProperties();
			}

			Dictionary storedConfig = null;
			if (storedConfig == null) {
				/*
				 * Only when there is still no configuration, take the current 
				 * one (will have already the unsaved properties)
				 */
				storedConfig = configuration;
			}
//			else {				
//				Enumeration unsaved = unsavedConfigs.keys();
//				while (unsaved.hasMoreElements()) {
//					String k = (String) unsaved.nextElement();
//					storedConfig.put(k, value);
//				}
//			}

			if (config != null) {
				config.update(storedConfig);
			}

			unsavedConfigs.clear();
			logger.info("setConfiguration: Saved configuration into CM");
		} catch (IOException e) {
			logger.error("setConfiguration: Could not save configuration "
					+ "properties into CM");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the value associated to the key "key"
	 * 
	 * @param key the key to search a value
	 * @return the value associated to the key "key"
	 */
	public String get(String key) {
		if (configuration == null) {
			return null;
		}
		return (String) configuration.get(key);
	}

	/**
	 * Loads the default configuration from the configuration file
	 * 
	 * @return the configuration loaded from the configuration file
	 */
	public Dictionary loadDefaults() {
		Properties properties = new Properties();
		InputStream f = null;
		logger.info("Loading default configuration for PID [" + pid + "] from " + configurationFilePath);
		f = this.getClass().getResourceAsStream(configurationFilePath);

		try {
			properties.load(f);
		} catch(IOException e) {
			logger.error(e.getMessage(), e);
		}	

		return properties;
	}

}	
