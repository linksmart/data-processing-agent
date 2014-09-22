/*
 * PWAL -Network-level Data Publisher
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
package it.ismb.pertlab.pwal.smartsantander.manager;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

/**
 * The polling task handling actual polling for Smart Santander sensors. It
 * takes care of querying the network-level end point and to deliver updates to
 * the subscribing devices, with the required timing properties.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class SmartSantanderPollingTask implements Runnable
{
	
	// the device manager running this polling task
	private SmartSantanderManager manager;
	private Logger log;
	
	public SmartSantanderPollingTask(SmartSantanderManager manager, Logger log)
	{
		// store a reference to the device manager
		this.manager = manager;
		
		// store the reference to the logger
		this.log = log;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		log.debug("Polling " + this.manager.getActiveSubscriptionsSize() + "subscription...");
		
		// if there is at least one subscription
		if (this.manager.getActiveSubscriptionsSize() > 0)
		{
			// ask for updated data
			List<SmartSantanderTrafficIntensityJson> measureUpdates = manager.restClient.getMeasures();
			
			// debug
			log.debug("Measures:" + measureUpdates);
			
			// iterate over the list and dispatch updates
			for (SmartSantanderTrafficIntensityJson currentMeasure : measureUpdates)
			{
				// get the low-level UID for the device
				String lUID = currentMeasure.getNodeId();
				
				// get all active subscriptions for the give lUID
				// debug
				log.debug("lUID: " + lUID);
				
				// dispatch the new measure if a subscription is registered for
				// the given lUID
				Set<DataUpdateSubscription<SmartSantanderTrafficIntensityJson>> subscriptionBucket = this.manager.getSubscriptions(lUID);
				
				if (subscriptionBucket != null)
				{
					for (DataUpdateSubscription<SmartSantanderTrafficIntensityJson> subscription : subscriptionBucket)
					{
						if (subscription != null)
						{
							long currentTime = System.currentTimeMillis();
							
							if (currentTime - subscription.getTimestamp() >= (subscription.getDeliveryTimeMillis()))
							{
								subscription.setTimestamp(currentTime);
								subscription.getSubscriber().handleUpdate(currentMeasure);
								log.info("Updating device: {} of type: {}.",((Device) subscription.getSubscriber()).getPwalId(),((Device) subscription.getSubscriber()).getType());
							}
						}
					}
				}
			}
		}
		
	}
}
