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
package it.ismb.pertlab.pwal.wastebinsimulator;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;
import it.ismb.pertlab.pwal.wastebinsimulator.devices.SimulatedWasteBin;
import it.ismb.pertlab.pwal.wastebinsimulator.network.WasteBinNetwork;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A device manager which simulates a number of fictitious waste bins equipped
 * with a temperature and a fill level sensor, it manages realistic temperature
 * update exploiting real time data from Open Weather Map and supports for fake,
 * yet realistic waste collection cycles triggered by full bins.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteBinSimulatorManager extends PollingDevicesManager<WasteBinSensorData>
{
	// the default number of simulated bins
	public static final int DEFAULT_N_BINS = 10;
	
	// the minimum polling time safely supported by the network in milliseconds
	public static final int MINIMUM_POLLING_TIME = 180000; // 3 min
	
	// the default polling time for sensors handled by this manager in
	// milliseconds
	public static final int DEFAULT_POLLING_TIME = 180000; // 3 min
	
	// the percent tolerance on data delivery times
	public static final int TIME_TOLERANCE_PERCENT = 20; // does not count as
															// minimum = default
	
	// the probability of a bin to be emptied
	public static final double EMPTY_FULL_BIN_PROBABILITY = 0.75;
	
	// the threshold on the fill level upon which a bin participates in waste
	// collection
	public static final double FULL_BIN_THRESHOLD = 75;
	
	// the waste collection cycle in hours
	public static final int DEFAULT_WASTE_COLLECTION_CYCLE = 1; //should be 24;
	
	// the poller service
	private ScheduledExecutorService poller;
	
	// the polling task
	private WasteBinSimulatorPollingTask pollingTask;
	
	// the future task execution promise that allows handling the polling
	// process
	private ScheduledFuture<?> futureRun;
	
	// the waste bin network layer
	private WasteBinNetwork networkLayer;
	
	/**
	 * Empty constructor respecting the bean instantiation pattern
	 */
	public WasteBinSimulatorManager()
	{
		// build the network layer
		this.networkLayer = new WasteBinNetwork(WasteBinSimulatorManager.DEFAULT_N_BINS);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WasteBinSimulatorPollingTask(this, log);
	}
	
	/**
	 * Empty constructor respecting the bean instantiation pattern
	 */
	public WasteBinSimulatorManager(String cityModel, String cityModelFile, String cityModelPrefix, String ontologyDir)
	{
		String cityModelPath = WasteBinSimulatorManager.class.getClassLoader().getResource(cityModelFile).getPath();
		String ontologyDirPath = WasteBinSimulatorManager.class.getClassLoader().getResource(ontologyDir).getPath();
		
		// build the network layer
		this.networkLayer = new WasteBinNetwork(cityModel,cityModelPath,cityModelPrefix,ontologyDirPath);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WasteBinSimulatorPollingTask(this, log);
	}
	
	/**
	 * Creates a new waste bin simulator manager handling the given number of
	 * fake bins
	 * 
	 * @param nBins
	 *            The number of fake bins to handle
	 * 
	 *            TODO: allow for boundary box specification here (for instance,
	 *            to change city)
	 */
	public WasteBinSimulatorManager(int nBins)
	{
		// build the network layer
		this.networkLayer = new WasteBinNetwork(nBins);
		
		// build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		// build the polling task
		this.pollingTask = new WasteBinSimulatorPollingTask(this, log);
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
		Collection<SimulatedWasteBin> allBins = this.networkLayer.getAllBins();
		
		// disable polling time update
		this.setAutoPollingUpdate(false);
		
		// Parallel bin generation using 4 threads
		ExecutorService initializationService = Executors.newFixedThreadPool(4);
		CompletionService<Boolean> compService = new ExecutorCompletionService<Boolean>(initializationService);
		
		// register the bins as devices
		for (final SimulatedWasteBin currentBin : allBins)
		{
			Callable<Boolean> task = new Callable<Boolean>() {
				
				@Override
				public Boolean call()
				{
					log.debug("Adding subscription for:" + currentBin.getNetworkLevelId());
					
					// add device polling subscription
					// TODO: define sampling time at the device level
					addSubscription(new DataUpdateSubscription<WasteBinSensorData>(
							WasteBinSimulatorManager.DEFAULT_POLLING_TIME, currentBin, currentBin.getNetworkLevelId()));
					
					// notify the addition
					
					for (DeviceListener l : deviceListener)
					{
						l.notifyDeviceAdded(currentBin);
					}
					
					return true;
				}
				
			};
			compService.submit(task);
		}
		
		for (int i = 0; i < allBins.size(); i++)
		{
			try
			{
				compService.take();
			}
			catch (InterruptedException e)
			{
				log.warn("Error while generating bins", e);
			}
		}
		initializationService.shutdown(); // always reclaim resources
		
		this.setAutoPollingUpdate(true);
		
		// profiling
		log.debug("Created " + allBins.size() + ", elapsed time: " + (System.currentTimeMillis() - time) + "ms");
		
		// --------------- END of SET-UP ----------------
		
		// --------------- KEEP ALIVE -------------------
		
		int nCycles = 0;
		// just wait for interrumptions
		while (!t.isInterrupted())
		{
			try
			{
				// trigger waste
				// bin emptying at random, check once at DEFAULT_WASTE_COLLECTION_CYCLE
				if (nCycles == 60*WasteBinSimulatorManager.DEFAULT_WASTE_COLLECTION_CYCLE)
				{
					for (SimulatedWasteBin bin : this.networkLayer.getAllBins())
					{
						if (bin.getFillLevel() >= FULL_BIN_THRESHOLD)
						{
							if (Math.random() >= EMPTY_FULL_BIN_PROBABILITY)
							{
								bin.setFillLevel(0);
							}
						}
					}
					
					nCycles = 0;
				}
				
				nCycles++;
				
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
		
		// ---------------- TEAR DOWN --------------------
		// check if the polling task is running
		if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
		{
			// cancel the polling task
			this.futureRun.cancel(false);
		}
		
		// TODO: check if devices must be removed here...
	}
	
	@Override
	protected void setBasePollingTimeMillis()
	{
		// set the minimum allowed polling time
		// TODO: set this value in the configuration...
		this.basePollingTimeMillis = WasteBinSimulatorManager.DEFAULT_POLLING_TIME;
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
		this.minimumPollingTimeMillis = WasteBinSimulatorManager.MINIMUM_POLLING_TIME;
		
	}
	
	@Override
	protected void setTimeTolerancePercentage()
	{
		this.timeTolerancePercentage = WasteBinSimulatorManager.TIME_TOLERANCE_PERCENT;
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
		return DeviceNetworkType.WASTEBINSIMULATOR;
	}
	
	/**
	 * Returns a reference to the network layer managed / accessed by this
	 * device manager implementation
	 * 
	 * @return the networkLayer the {@link WasteBinNetwork} layer used by this
	 *         manager
	 */
	@JsonIgnore
	public WasteBinNetwork getNetworkLayer()
	{
		return networkLayer;
	}
	
}
