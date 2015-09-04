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

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.smartbindriver.data.IESmartBinData;
import it.ismb.pertlab.pwal.smartbindriver.devices.IESmartBin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import metrics.smartbin.api.MetricServices;
import metrics.smartbin.api.MetricsAPIv24Locator;

/**
 * The pWAL driver for handling waste bins published by
 * SmartBin(http://www.smartbin.com/) through their private, web service API.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class IESmartBinDriver extends PollingDevicesManager<IESmartBinData>
{
	// the default polling time for sensors handled by this manager in
	// milliseconds
	public static final int DEFAULT_POLLING_TIME = 60000; // 3minutes
	
	// the minimum polling time safely supported by the network in milliseconds
	public static final int MINIMUM_POLLING_TIME = 60000; // 3 minutes
	
	// the percent tolerance on data delivery times
	public static final int TIME_TOLERANCE_PERCENT = 20; // does not count as
															// minimum = default
	
	public static final String PORT_NAME = "MetricServicesPort";
	
	// the SmartBin web service endpoint
	private String soapEnpoint;
	
	// the required polling time in milliseconds
	private int requiredPollingTimeMillis;
	
	// the smart bin web service client
	private MetricServices smartBinService;
	
	// the api key
	private String apiKey;
	
	// the api name
	private String apiName;
	
	/**
	 * Class constructor, initializes a new PWAL IESmartBindDriver with the
	 * needed information to support operation.
	 * 
	 * @param soapEndpoint
	 *            The endpoint of the SmartBin web service.
	 * @param pollingTimeMillis
	 *            The required polling period in milliseconds.
	 * @param apiName
	 *            The SmartBin api name.
	 * @param apiKey
	 *            The SmartBin api key.
	 */
	public IESmartBinDriver(String soapEndpoint, int pollingTimeMillis, String apiName, String apiKey)
	{
		// store the soap service enpoint
		this.soapEnpoint = soapEndpoint;
		
		// the polling time required for the application
		this.requiredPollingTimeMillis = pollingTimeMillis;
		
		// force the given polling time
		this.basePollingTimeMillis = this.requiredPollingTimeMillis;
		this.minimumPollingTimeMillis = this.requiredPollingTimeMillis;
		
		// store the api key and password
		this.apiKey = apiKey;
		this.apiName = apiName;
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// initialize the service staubs
		this.initializeServiceStubs();
		
		// build the polling task
		this.pollingTask = new IESmartBinPollingTask(this, DevicesManager.log);
	}
	
	/**
	 * Main execution cycle, only needed to "respect" the lifetime control.
	 */
	public void run()
	{
		// start polling at the default / required frequency (might be updated
		// when devices are discovered)
		this.pollingTimeMillis = this.requiredPollingTimeMillis;
		
		// trigger polling task start
		this.updatePollingTime();
		
		// just wait for interruptions
		while (!t.isInterrupted())
		{
			try
			{
				Thread.sleep(60000);
			}
			catch (InterruptedException e)
			{
				log.error("Exception: ", e);
				t.interrupt();
			}
		}
	}
	
	@Override
	protected void setBasePollingTimeMillis()
	{
		// set the base polling time in milliseconds
		this.basePollingTimeMillis = IESmartBinDriver.DEFAULT_POLLING_TIME;
		
	}
	
	@Override
	protected void setMinimumPollingTimeMillis()
	{
		// set the minimum polling time in milliseconds
		this.minimumPollingTimeMillis = IESmartBinDriver.MINIMUM_POLLING_TIME;
		
	}
	
	@Override
	protected void setTimeTolerancePercentage()
	{
		// set the time tolerance as percentage
		this.timeTolerancePercentage = IESmartBinDriver.TIME_TOLERANCE_PERCENT;
		
	}
	
	@Override
	protected void updatePollingTime()
	{
		// check if the polling task is running
		if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
		{
			// stop the current polling task
			this.futureRun.cancel(false);
			
		}
		
		log.debug("Updating polling time to: " + this.pollingTimeMillis);
		log.debug("Active subscriptions:" + this.nActiveSubscriptions);
		
		// starts the poller only if at least one subscription is available
		this.futureRun = this.poller.scheduleAtFixedRate(this.pollingTask, 0, this.pollingTimeMillis,
				TimeUnit.MILLISECONDS);
		
	}
	
	@Override
	public String getNetworkType()
	{
		return DeviceNetworkType.IESMARTBIN;
	}
	
	/**
	 * Add a subscription to updates coming from the "real" network, for the
	 * given waste bin representation.
	 * 
	 * @param bin
	 *            The IESmartBin to update.
	 */
	public void addDeviceSubscription(IESmartBin bin)
	{
		log.debug("Adding subscription for:" + bin.getNetworkLevelId());
		
		// add the bin subscription
		addSubscription(new DataUpdateSubscription<IESmartBinData>(Math.max(this.requiredPollingTimeMillis,
				IESmartBinDriver.MINIMUM_POLLING_TIME), bin, bin.getNetworkLevelId()));
		
		// trace the discovered (and connected) devices
		synchronized (devicesDiscovered)
		{
			if (!devicesDiscovered.containsKey(bin.getNetworkLevelId()))
			{
				List<Device> ld = new ArrayList<Device>();
				devicesDiscovered.put(bin.getNetworkLevelId(), ld);
			}
			devicesDiscovered.get(bin.getNetworkLevelId()).add(bin);
			// notify the addition
		}
		
		for (DeviceListener l : deviceListener)
		{
			// notify listeners for device addition
			l.notifyDeviceAdded(bin);
		}
		
	}
	
	/**
	 * Provides access to the SmartBin WebService stub.
	 * 
	 * @return the smartBinService stub.
	 */
	public MetricServices getSmartBinService()
	{
		return smartBinService;
	}
	
	/**
	 * Provides the api key currently used by this driver.
	 * 
	 * @return the apiKey
	 */
	public String getApiKey()
	{
		return apiKey;
	}
	
	/**
	 * Provides the api password currently used by this driver.
	 * 
	 * @return the apiPwd
	 */
	public String getApiName()
	{
		return apiName;
	}
	
	/************************************************************
	 * 
	 * PRIVATE METHODS
	 * 
	 ***********************************************************/
	
	/**
	 * Initializes the WebService stub used by this class, returns true if the
	 * initialization process is successful and false otherwise.
	 * 
	 * @return True is initialization completed successfully, false otherwise.
	 */
	private boolean initializeServiceStubs()
	{
		boolean success = false;
		try
		{
			// build the web service locator
			MetricsAPIv24Locator serviceLocator = new MetricsAPIv24Locator();
			serviceLocator.setEndpointAddress(IESmartBinDriver.PORT_NAME, this.soapEnpoint);
			
			// get the web service proxy
			this.smartBinService = serviceLocator.getMetricServicesPort();
			
			// set the success flag
			success = true;
		}
		catch (ServiceException e)
		{
			DevicesManager.log.error("Unable to generate the web service stub", e);
		}
		
		return success;
	}
}
