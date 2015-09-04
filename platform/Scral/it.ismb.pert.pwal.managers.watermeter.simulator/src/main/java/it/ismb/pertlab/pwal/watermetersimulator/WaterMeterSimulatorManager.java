/*
 * PWAL -Waste Bin Data Simulator
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
package it.ismb.pertlab.pwal.watermetersimulator;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.watermetersimulator.data.WaterMeterSensorData;
import it.ismb.pertlab.pwal.watermetersimulator.devices.SimulatedWaterMeter;
import it.ismb.pertlab.pwal.watermetersimulator.network.WaterMeterNetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A device manager which simulates a number of fictitious water meyters
 * equipped with a flow sensor.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WaterMeterSimulatorManager extends PollingDevicesManager<WaterMeterSensorData>
{
	// the default number of simulated bins
	public static final int DEFAULT_N_WATER_METERS = 10;
	
	// the minimum polling time safely supported by the network in milliseconds
	public static final int MINIMUM_POLLING_TIME = 60000; // 3 min
	
	// the default polling time for sensors handled by this manager in
	// milliseconds
	public static int DEFAULT_POLLING_TIME = 60000; // 3 min
	
	// the percent tolerance on data delivery times
	public static final int TIME_TOLERANCE_PERCENT = 20; // does not count as
															// minimum = default
	
	// the probability of a bin to be emptied
	public static final double EMPTY_FULL_BIN_PROBABILITY = 0.75;
	
	// the threshold on the fill level upon which a bin participates in waste
	// collection
	public static final double FULL_BIN_THRESHOLD = 75;
	
	// the waste collection cycle in hours
	public static final int DEFAULT_WASTE_COLLECTION_CYCLE = 1; // should be 24;
	
	// the poller service
	// private ScheduledExecutorService poller;
	
	// the polling task
	// private WasteBinSimulatorPollingTask pollingTask;
	
	// the future task execution promise that allows handling the polling
	// process
	private ScheduledFuture<?> futureRun;
	
	// the waste bin network layer
	private WaterMeterNetwork networkLayer;
	
	// the required sensor polling time
	private int requiredPollingTime = 0;
	
	/**
	 * Empty constructor respecting the bean instantiation pattern
	 */
	public WaterMeterSimulatorManager()
	{
		super();
		
		// build the network layer
		this.networkLayer = new WaterMeterNetwork(WaterMeterSimulatorManager.DEFAULT_N_WATER_METERS);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WaterMeterSimulatorPollingTask(this, log);
	}
	
	public WaterMeterSimulatorManager(int nMeters)
	{
		// build the network layer
		this.networkLayer = new WaterMeterNetwork(nMeters);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WaterMeterSimulatorPollingTask(this, log);
	}
	
	public WaterMeterSimulatorManager(int nMeters, int pollingTimeMillis)
	{
		// set the polling time
		this.basePollingTimeMillis = pollingTimeMillis;
		this.requiredPollingTime = pollingTimeMillis;
		this.minimumPollingTimeMillis = pollingTimeMillis;
		
		// build the network layer
		this.networkLayer = new WaterMeterNetwork(nMeters);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WaterMeterSimulatorPollingTask(this, log);
	}
	
	/**
	 * Executed during the ACTIVE phase of the manager. It is basically divided
	 * in three parts: set-up in which waste bins are created and registered as
	 * listeners for network subscriptions, keep alive, in which nothing is done
	 * apart generating fake bin collections, tear-down, in which house keeping
	 * is performed before shutting down.
	 */
	public void run()
	{
		// ------------ SET-UP -----------------------------------------------
		// profiling
		long time = System.currentTimeMillis();
		
		// list all bins
		Collection<SimulatedWaterMeter> allMeters = this.networkLayer.getAllWaterMeters();
		
		// disable polling time update
		this.setAutoPollingUpdate(false);
		
		// Parallel bin generation using 4 threads
		ExecutorService initializationService = Executors.newFixedThreadPool(Math.min(4, allMeters.size()));
		CompletionService<Boolean> compService = new ExecutorCompletionService<Boolean>(initializationService);
		
		// register the bins as devices
		for (final SimulatedWaterMeter currentMeter : allMeters)
		{
			Callable<Boolean> task = new Callable<Boolean>() {
				
				@Override
				public Boolean call()
				{
					log.debug("Adding subscription for:" + currentMeter.getNetworkLevelId());
					
					// add device polling subscription
					// TODO: define sampling time at the device level
					addSubscription(new DataUpdateSubscription<WaterMeterSensorData>(Math.max(requiredPollingTime,
							WaterMeterSimulatorManager.MINIMUM_POLLING_TIME), currentMeter,
							currentMeter.getNetworkLevelId()));
					synchronized (devicesDiscovered)
					{
						if (!devicesDiscovered.containsKey(currentMeter.getNetworkLevelId()))
						{
							List<Device> ld = new ArrayList<>();
							devicesDiscovered.put(currentMeter.getNetworkLevelId(), ld);
						}
						devicesDiscovered.get(currentMeter.getNetworkLevelId()).add(currentMeter);
						// notify the addition
					}
					
					for (DeviceListener l : deviceListener)
					{
						l.notifyDeviceAdded(currentMeter);
					}
					
					return true;
				}
				
			};
			compService.submit(task);
		}
		
		for (int i = 0; i < allMeters.size(); i++)
		{
			try
			{
				compService.take();
			}
			catch (InterruptedException e)
			{
				log.warn("Error while generating water meters", e);
			}
		}
		initializationService.shutdown(); // always reclaim resources
		
		this.setAutoPollingUpdate(true);
		
		// profiling
		log.debug("Created " + allMeters.size() + ", elapsed time: " + (System.currentTimeMillis() - time) + "ms");
		
		// --------------- END of SET-UP ----------------
		
		// --------------- KEEP ALIVE -------------------
		// just wait for interruptions
		while (!t.isInterrupted())
		{
			try
			{
				// just keep the manager alive
				Thread.sleep(60000);
			}
			catch (InterruptedException e)
			{
				log.error("Exception: ", e);
				t.interrupt();
			}
		}
		
		// ---------------- END of KEEP ALIVE ------------
	}
	
	@Override
	protected void setBasePollingTimeMillis()
	{
		// set the minimum allowed polling time
		// TODO: set this value in the configuration...
		this.basePollingTimeMillis = WaterMeterSimulatorManager.DEFAULT_POLLING_TIME;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager#
	 * setMinimumPollingTimeMillis()
	 */
	@Override
	protected void setMinimumPollingTimeMillis()
	{
		this.minimumPollingTimeMillis = WaterMeterSimulatorManager.MINIMUM_POLLING_TIME;
		
	}
	
	@Override
	protected void setTimeTolerancePercentage()
	{
		this.timeTolerancePercentage = WaterMeterSimulatorManager.TIME_TOLERANCE_PERCENT;
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
		// TODO Auto-generated method stub
		return DeviceNetworkType.WATERMETERSIMULATOR;
	}
	
	/**
	 * Returns a reference to the network layer managed / accessed by this
	 * device manager implementation
	 * 
	 * @return the networkLayer the {@link WaterMeterNetwork} layer used by this
	 *         manager
	 */
	@JsonIgnore
	public WaterMeterNetwork getNetworkLayer()
	{
		return networkLayer;
	}
	
}
