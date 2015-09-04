/*
 * PWAL - Resource Catalog connector
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
package it.ismb.pertlab.pwal.resourcecatalog.connector;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;
import com.mycila.event.Topic;

/**
 * The ResourceCatalog connector, listens for new device added events on the
 * inner pub-sub system and registers the new devices into the catalog. Uses an
 * unbounded queue to handle big bursts of device registrations in a time
 * compatible with the http requests needed for registering the devices with the
 * catalog.
 * 
 * Might unefficiently use memory, in such a case different policies might be
 * applied.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class ResourceCatalogConnector extends PWALEventSubsciber<PWALNewDeviceAddedEvent>
{
	// the resource catalog endpoint
	private String resourceCatalogEndpoint;
	
	// the scral endpoint
	private String scralEndpoint;
	
	// the class logger
	private Logger logger;
	
	// the registration executor service
	private ExecutorService registrationExecutor;
	
	/**
	 * Builds a ResourceCatalog connector instance given the endpoint of the
	 * catalog and the rest endpoint serving the device scpd.
	 * 
	 * @param resourceCatalogEndpoint
	 *            The resource catalog endpoint.
	 * @param scralEndpoint
	 *            The rest endpoint serving the device SCPD.
	 */
	public ResourceCatalogConnector(String resourceCatalogEndpoint, String scralEndpoint)
	{
		// initialize the class logger
		this.logger = LoggerFactory.getLogger(ResourceCatalogConnector.class);
		
		// attach to the pwal event manager
		PWALEventDispatcher
				.getInstance()
				.getDispatcher()
				.subscribe(Topic.match(PWALTopicsUtility.createNewDeviceAddedTopic("**")), PWALNewDeviceAddedEvent.class,
						this);
		
		this.resourceCatalogEndpoint = resourceCatalogEndpoint;
		this.scralEndpoint = scralEndpoint;
		
		//note: to reduce memory usage, multiple threads can be used here
		this.registrationExecutor = Executors.newSingleThreadExecutor();

		
	}
	
	/**
	 * Listens for new device added events and delivers them to the catalog by
	 * exploiting an executor service and an unbounded queue.
	 */
	public void onEvent(Event<PWALNewDeviceAddedEvent> deviceAddedEvent) throws Exception
	{
		// get a reference to the newly added device
		final Device newDevice = deviceAddedEvent.getSource().getSender();
		
		// build the scral url
		String scral = (this.scralEndpoint.endsWith("/") ? this.scralEndpoint : this.scralEndpoint + "/");
		
		// build the resource catalog url
		String resourceCatalog = (this.resourceCatalogEndpoint.endsWith("/") ? this.resourceCatalogEndpoint
				: this.resourceCatalogEndpoint + "/");
		
		// build the device registration url
		final String enpointUrl = resourceCatalog + "services/applicationdevicemanager/actions/RegisterResource?scpd="
				+ scral + "devices/" + newDevice.getPwalId() + "/scpd&restendpoint=" + scral + "devices/"
				+ newDevice.getPwalId() + "/scpd" + newDevice.getPwalId();
		
		this.registrationExecutor.submit(new Runnable() {
			
			public void run()
			{
				try
				{
					//build the enpoint url
					URL registrationUrl = new URL(enpointUrl);
					
					//perform a get on the url
					HttpURLConnection connection = (HttpURLConnection) registrationUrl.openConnection();
					
					//check the response code
					if (connection.getResponseCode() == 200)
					{
						logger.info("Registered device: " + newDevice.getId() + " (" + newDevice.getPwalId() + ")");
					}
					else
					{
						logger.error("Unable to register to the resource catalog");
					}
					
					connection.disconnect();
				}
				catch (Exception e)
				{
					logger.error("Unable to register to the resource catalog", e);
				}
				
			}
		});
		
	}
	
}
