/*
 * PWAL - SCPD helper
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package scpdhelper;

import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.event.format.ogc.sensorthings.api.OGCSensorThingsAPIPayloadFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An helper class for converting PWAL devices {@link Device} into SCPD device
 * descriptions respecting the ALMANAC format (Absolutely not standard,...)
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class ScpdHelper
{
	// the velocity engine for generating scpd device templates
	private VelocityEngine vtEngine;
	
	// the class logger
	private Logger logger;
	
	//the fully qualified domain name
	private String platformFQDN;
	
	/**
	 * Constructor, takes a fully qualified domain for supporting OGC SensorThings API description piggybaking
	 * @param platformFQDN
	 */
	public ScpdHelper(String platformFQDN)
	{
		// initialize the class logger
		this.logger = LoggerFactory.getLogger(ScpdHelper.class);
		
		// store the platform FQDN
		this.platformFQDN = platformFQDN;
		
		// prepare the velocity generator to work by using a classpath resource
		// loader
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "class");
		p.put("class.resource.loader.class", ClasspathResourceLoader.class.getName());
		// /p.put("cls.resource.loader.root", templateFolder.toString());
		p.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
		
		// create the engine
		this.vtEngine = new VelocityEngine(p);
		
		// initialize the engine
		this.vtEngine.init();
	}
	
	/**
	 * Given a PWAL device ({@link Device}) instance, a presentation URL for the
	 * device and a gateway at which the device description can be reached,
	 * provides back the device description as SCPD.
	 * 
	 * @param device
	 *            The device to represent as SCPD.
	 * @param presentationUrl
	 *            The device presentation url.
	 * @param gateway
	 *            The gateway serving the device
	 * @return The device SCPD as a {@link String}.
	 */
	public String getSCPD(Device device, String presentationUrl, String gateway)
	{
		// the scpd device description
		String scpd = null;
		
		// get the velocity template
		Template template = this.vtEngine.getTemplate("templates/device-scpd.vm");
		
		// check not null
		if (template != null)
		{
			// create the context
			VelocityContext context = new VelocityContext();
			
			// build the device data
			context.put("type", device.getType().toLowerCase());
			context.put("url", presentationUrl);
			context.put("name", device.getId());
			context.put("manufacturer", device.getNetworkType());
			context.put("manufacturerURL", "http://www.ismb.it");
			context.put("description",
					"A SCRAL" + device.getType() + " device connected to a " + device.getNetworkType() + " network.");
			context.put("modelName", device.getType().toLowerCase());
			context.put("modelNumber", "1");
			context.put("uid", "uuid:" + device.getPwalId());
			context.put("resourceId", device.getPwalId());
			context.put("gateway", gateway);
			context.put("lastUpdate", device.getUpdatedAt());
			context.put("service", device.getType().toLowerCase() + "service");
			context.put("serviceId", device.getType().toLowerCase() + "service_" + device.getPwalId());
			
			// the OGC payload factory
			OGCSensorThingsAPIPayloadFactory payloadFactory = OGCSensorThingsAPIPayloadFactory.getInstance();
			
			// give the event extract the list of observations to post
			Thing thing = payloadFactory.getSensorMetadataPayload(device, this.platformFQDN);
			
			//set the Thing JSON rendering as content for the $thing parameter in the template
			try
			{
				context.put("thing", PWALJsonMapper.obj2json(thing));
			}
			catch (IOException e)
			{
				// just log the error;
				this.logger.error("Unable to Jsonify the thing representation. ",e);
			}
						
			// prepare the set of device parameters
			Set<ScpdParameter> deviceParameters = new HashSet<ScpdParameter>();
			
			// get the interfaces implemented by the given device class
			Class<?> deviceInterfaces[] = device.getClass().getInterfaces();
			
			// get only the methods defined by interfaces extending device
			for (int i = 0; i < deviceInterfaces.length; i++)
			{
				if (Device.class.isAssignableFrom(deviceInterfaces[i]) && (!deviceInterfaces[i].equals(Device.class)))
				{				
					// get the interface methods
					Method methods[] = deviceInterfaces[i].getMethods();
					
					// handle methods
					for (int j = 0; j < methods.length; j++)
					{
						// only handle getters
						if (methods[j].getName().startsWith("get"))
						{
							// get the parameter name
							String methodName = methods[j].getName();
							String name = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
							
							//this.logger.info("************************* "+name);
							
							// invoke the getter to retrieve the parameter value
							String value = "";
							try
							{
								Object valueObject = methods[j].invoke(device, new Object[0]);
								if (valueObject != null)
									value += valueObject.toString();
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								this.logger.warn("Non void method call...");
							}
							
							// if both name and value are not null, create the
							// device parameter
							if ((name != null) && (!name.isEmpty()) && (value != null) && (!value.isEmpty()))
							{
								deviceParameters.add(new ScpdParameter(name, value));
							}
						}
					}
					
					// add the device parameters
					context.put("parameters", deviceParameters);
					
					// prepare a string writer on which compiling the template
					StringWriter sw = new StringWriter();
					
					// compile the template
					template.merge(context, sw);
					
					// set the generated scpd description
					scpd = sw.toString();
				}
			}
			
		}
		// return the device description
		return scpd;
	}
}
