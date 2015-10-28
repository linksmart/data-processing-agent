/*
 * PWAL - Smart Bin Driver
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
package it.ismb.pertlab.pwal.smartbindriver;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.api.devices.polling.PWALPollingTask;
import it.ismb.pertlab.pwal.smartbindriver.data.IESmartBinData;
import it.ismb.pertlab.pwal.smartbindriver.devices.IESmartBin;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import metrics.smartbin.api.MetricServices;
import metrics.smartbin.api.MetricsWrapper;
import metrics.smartbin.api.StandardSensorMetric;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

/**
 * The SmartBin (http://www.smartbin.com/) data poller. It queries the SmartBin
 * private webservice, with the required frequency and takes care of either
 * updating existing wastebins (IESmartBin instances) or creating the newly
 * discovered ones.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class IESmartBinPollingTask extends PWALPollingTask<IESmartBinDriver, IESmartBinData> implements Runnable
{
	/**
	 * Class constructor, basically calls the superclass constructor and, if
	 * needed, initializes the instance variables.
	 * 
	 * @param manager
	 *            The manager controlling this polling task.
	 * @param logger
	 *            The logger to use for tracing runtime information and errors.
	 */
	public IESmartBinPollingTask(PollingDevicesManager<IESmartBinData> manager, Logger logger)
	{
		super(manager, logger);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		// get the bins
		Set<StandardSensorMetric> allBins = getLatestBinInformation();
		
		// iterate over the bins
		for (StandardSensorMetric bin : allBins)
		{
			// handle updates, if the bin does not exists add it...
			Set<DataUpdateSubscription<IESmartBinData>> subscriptionBucket = this.manager.getSubscriptions(""
					+ bin.getId());
			
			if (subscriptionBucket != null)
			{
				// -------- handle updates ---------------
				
				// the data to use as update
				IESmartBinData currentData = new IESmartBinData(bin);
				
				// iterate over device subscriptions
				for (DataUpdateSubscription<IESmartBinData> subscription : subscriptionBucket)
				{
					// if the current subscription is not null
					if (subscription != null)
					{
						// check how much time passed since last update
						long currentTime = System.currentTimeMillis();
						
						// if needed generate a new update event (low-level)
						if (currentTime - subscription.getTimestamp() >= (subscription.getDeliveryTimeMillis()))
						{
							// update the device
							DateTime updateAt = DateTime.now(DateTimeZone.UTC);
							String expiresAt = updateAt.plusMillis(subscription.getDeliveryTimeMillis()).toString();
							((Device) subscription.getSubscriber()).setUpdatedAt(updateAt.toString());
							((Device) subscription.getSubscriber()).setExpiresAt(expiresAt);
							subscription.getSubscriber().handleUpdate(currentData);
							
							// log
							this.logger.debug("Updating device: {} type: {}.",
									((Device) subscription.getSubscriber()).getPwalId(),
									((Device) subscription.getSubscriber()).getType());
						}
					}
				}
			}
			else
			{
				// handle bin creation
				
				// create the new bin
				IESmartBin smartBin = new IESmartBin(bin);
				
				// add and register subscription...
				((IESmartBinDriver) this.manager).addDeviceSubscription(smartBin);
			}
		}
	}
	
	/**
	 * Gets the latest updates by querying the SmartBin webservice
	 */
	private Set<StandardSensorMetric> getLatestBinInformation()
	{
		// the set of found bins
		HashSet<StandardSensorMetric> allBins = new HashSet<StandardSensorMetric>();
		
		// the metrics service stub
		MetricServices service = ((IESmartBinDriver) this.manager).getSmartBinService();
		
		// the call result holder
		MetricsWrapper smartBinRawData = null;
		
		// try to get the SmartBins data
		try
		{
			smartBinRawData = service.metrics(((IESmartBinDriver) this.manager).getApiName(),
					((IESmartBinDriver) this.manager).getApiKey());
		}
		catch (RemoteException e)
		{
			this.logger.error("Error while gettin SmartBin data", e);
		}
		
		// if some data is available
		if (smartBinRawData != null)
		{
			Object[] bins = smartBinRawData.getPayload().getData();
			
			for (int i = 0; i < bins.length; i++)
			{
				if (bins[i] instanceof StandardSensorMetric)
				{
					// cast to sensor metric
					StandardSensorMetric binMetric = (StandardSensorMetric) bins[i];
					
					// debug
					this.logger.info("Found bin with id: " + binMetric.getId());
					
					// add to the set of bins
					allBins.add(binMetric);
				}
			}
		}
		// if no bin has been found, return null
		return allBins.isEmpty() ? null : allBins;
	}
	
}
