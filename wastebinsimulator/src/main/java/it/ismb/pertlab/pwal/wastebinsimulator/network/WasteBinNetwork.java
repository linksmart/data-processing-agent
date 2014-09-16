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
package it.ismb.pertlab.pwal.wastebinsimulator.network;

import it.ismb.pertlab.pwal.wastebinsimulator.data.LinearFillLevelGenerator;
import it.ismb.pertlab.pwal.wastebinsimulator.data.OWMTemperatureGenerator;
import it.ismb.pertlab.pwal.wastebinsimulator.data.TemperatureGenerator;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;
import it.ismb.pertlab.pwal.wastebinsimulator.devices.SimulatedWasteBin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The waste bin network class which emulates the existence of a physical
 * network of waste bins, each with its own sensors and location.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteBinNetwork
{
	// Hardcoded Turin's bounding box, used to randomly generate latitude and
	// longitude
	// information
	// TODO: drop this constants and make location configurable
	public static final float MAX_LAT = 45.133267f;
	public static final float MIN_LAT = 45.004271f;
	public static final float MAX_LONG = 7.709400f;
	public static final float MIN_LONG = 7.569667f;
	
	// the default number of beans
	private int nBins = 10000;
	
	// the number of completely fake bins who actually mirror the temperature
	// data from a real location (bin) with respect to the number of "realistic"
	// bins.
	private int fakeToRealRatio = 10;
	
	// the inner id
	private int id;
	
	// the currently active waste bins
	private Vector<SimulatedWasteBin> activeBins;
	
	// the temperature generator
	private TemperatureGenerator tGenerator;
	
	// the fill level generator
	private LinearFillLevelGenerator fGenerator;
	
	// the logger
	protected static final Logger logger = LoggerFactory.getLogger(WasteBinNetwork.class);
	
	/**
	 * Empty constructor, uses default initialization values, e.g., the default
	 * number of bins to generate.
	 */
	public WasteBinNetwork()
	{
		// initialize the id counter
		this.id = 0;
		
		this.initCommon();
	}
	
	/**
	 * Creates a network layer handling the given number of simulated bins
	 * 
	 * @param nBins
	 */
	public WasteBinNetwork(int nBins)
	{
		// initialize the id counter
		this.id = 0;
		
		// set the number of bins
		this.nBins = nBins;
		
		this.initCommon();
	}
	
	/**
	 * Common initialization tasks
	 */
	private void initCommon()
	{
		// create inner data structures
		this.activeBins = new Vector<SimulatedWasteBin>();
		
		// initialize the waste bins
		this.initBins();
		
		// initialize the temperature generator
		this.tGenerator = new OWMTemperatureGenerator();
		
		// initialize the fill level generator
		this.fGenerator = new LinearFillLevelGenerator();
	}
	
	/**
	 * Parallel bin creation using 4 threads
	 */
	private void initBins()
	{
		ExecutorService initializationService = Executors.newFixedThreadPool(4);
		CompletionService<Boolean> compService = new ExecutorCompletionService<Boolean>(initializationService);
		for (int i = 0; i < nBins; i++)
		{
			Callable<Boolean> task = new Callable<Boolean>() {
				
				@Override
				public Boolean call()
				{
					// compute the fake longitude for the current bin
					double currentLongitude = WasteBinNetwork.MIN_LONG + Math.random()
							* (WasteBinNetwork.MAX_LONG - WasteBinNetwork.MIN_LONG);
					
					// compute the fake latitude for the
					double currentLatitude = WasteBinNetwork.MIN_LAT + Math.random()
							* (WasteBinNetwork.MAX_LAT - WasteBinNetwork.MIN_LAT);
					
					// build the current bin
					SimulatedWasteBin currentBin = new SimulatedWasteBin((float) currentLongitude,
							(float) currentLatitude, "" + getId());
					
					// set a random fill level
					currentBin.setFillLevel((int)Math.round(Math.random()*100));
					
					// store the bin
					activeBins.add(currentBin);
					
					// info
					logger.info("Created virtual WasteBin at: " + currentLatitude + "N, " + currentLongitude + "E");
					
					// return true
					return true;
				}
				
			};
			compService.submit(task);
		}
		
		// wait for all threads to complete
		for (int i = 0; i < nBins; i++)
		{
			try
			{
				compService.take();
			}
			catch (InterruptedException e)
			{
				logger.warn("Error while generating bins", e);
			}
		}
		
		// shutdown the execution service to free resources
		initializationService.shutdown(); // always reclaim resources
		
	}
	
	/**
	 * get the auto-increment id for generated fake bins (does not survive
	 * between different runs)
	 * 
	 * @return
	 */
	private synchronized String getId()
	{
		return "" + (this.id++);
	}
	
	/**
	 * Returns a snapshot of the current bin set (not linked)
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<SimulatedWasteBin> getAllBins()
	{
		return (Collection<SimulatedWasteBin>) this.activeBins.clone();
	}
	
	/**
	 * Get the latest updates from all managed bins
	 * 
	 * @return
	 */
	public Collection<WasteBinSensorData> getLatestUpdates()
	{
		// the set of updated bins
		Set<WasteBinSensorData> updates = new HashSet<WasteBinSensorData>();
		
		// debug
		int i = 0;
		// profiling
		long time = System.currentTimeMillis();
		
		DecimalMeasure<Temperature> currentT = null;
		
		// generate the updates
		for (SimulatedWasteBin currentBin : this.activeBins)
		{
			if (i % this.fakeToRealRatio == 0)
				// get the current temperature...
				currentT = this.tGenerator.getCurrentTemperature((float) currentBin.getLatitude(),
						(float) currentBin.getLongitude());
			
			int currentFillLevel = this.fGenerator.getCurrentFillLevel(currentBin.getFillLevel(), currentBin.getLatestUpdate(), currentBin.getnDaysToFull());
			
			// if null try using the previous value
			if (currentT == null)
			{
				currentT = currentBin.getTemperatureAsMeasure();
			}
			
			// if not null (Tin Pants handling)
			if (currentT != null)
			{
				// build the updated sensor data
				WasteBinSensorData cData = new WasteBinSensorData(currentT, currentFillLevel, currentBin.getNetworkLevelId());
				
				// store the update
				updates.add(cData);
				
				// inc i
				i++;
				
				// log
				WasteBinNetwork.logger.info("Updated [" + i + "]: T="+currentT+" F="+currentFillLevel);
			}
			
		}
		
		WasteBinNetwork.logger.info("Generated updates, Elapsed time: "
				+ ((System.currentTimeMillis() - time) / 1000.0) + "s");
		
		return updates;
	}
	
}
