package it.ismb.pertlab.pwal.watermetersimulator.network;

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

import it.ismb.pertlab.pwal.watermetersimulator.data.DayProfileGenerator;
import it.ismb.pertlab.pwal.watermetersimulator.data.WaterMeterSensorData;
import it.ismb.pertlab.pwal.watermetersimulator.devices.SimulatedWaterMeter;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Volume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The waste bin network class which emulates the existence of a physical
 * network of waste bins, each with its own sensors and location.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WaterMeterNetwork
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
	private int nMeters = 10000;
	
	// the inner id
	private int id;
	
	// the currently active waste bins
	private Vector<SimulatedWaterMeter> activeWaterMeters;
	
	// the fill level generator
	private DayProfileGenerator fGenerator;
	
	// the logger
	protected static final Logger logger = LoggerFactory.getLogger(WaterMeterNetwork.class);
	
	/**
	 * Empty constructor, uses default initialization values, e.g., the default
	 * number of bins to generate.
	 */
	public WaterMeterNetwork()
	{
		// initialize the id counter
		this.id = 0;
		
		this.initCommon();
	}
	
	/**
	 * Creates a network layer handling the given number of simulated bins
	 * 
	 * @param nMeters
	 */
	public WaterMeterNetwork(int nMeters)
	{
		// initialize the id counter
		this.id = 0;
		
		// set the number of bins
		this.nMeters = nMeters;
		
		this.initCommon();
		
		// initialize the waste bins
		this.initMeters();
	}
	
	/**
	 * Common initialization tasks
	 */
	private void initCommon()
	{
		// create inner data structures
		this.activeWaterMeters = new Vector<SimulatedWaterMeter>();
		
		// initialize the fill level generator
		this.fGenerator = new DayProfileGenerator();
	}
	
	/**
	 * Parallel bin creation using 4 threads
	 */
	private void initMeters()
	{
		ExecutorService initializationService = Executors.newFixedThreadPool(4);
		CompletionService<Boolean> compService = new ExecutorCompletionService<Boolean>(initializationService);
		for (int i = 0; i < nMeters; i++)
		{
			Callable<Boolean> task = new Callable<Boolean>() {
				
				@Override
				public Boolean call()
				{
					// compute the fake longitude for the current bin
					double currentLongitude = WaterMeterNetwork.MIN_LONG + Math.random()
							* (WaterMeterNetwork.MAX_LONG - WaterMeterNetwork.MIN_LONG);
					
					// compute the fake latitude for the
					double currentLatitude = WaterMeterNetwork.MIN_LAT + Math.random()
							* (WaterMeterNetwork.MAX_LAT - WaterMeterNetwork.MIN_LAT);
					
					// build the current bin
					SimulatedWaterMeter currentMeter = new SimulatedWaterMeter((float) currentLatitude, (float) currentLongitude, "" + getId());
					
					// store the bin
					activeWaterMeters.add(currentMeter);
					
					// info
					logger.info("Created virtual Water Meter at: " + currentLatitude + "N, " + currentLongitude + "E");
					
					// return true
					return true;
				}
				
			};
			compService.submit(task);
		}
		
		// wait for all threads to complete
		for (int i = 0; i < nMeters; i++)
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
	public Collection<SimulatedWaterMeter> getAllWaterMeters()
	{
		return (Collection<SimulatedWaterMeter>) this.activeWaterMeters.clone();
	}
	
	/**
	 * Get the latest updates from all managed bins
	 * 
	 * @return
	 */
	public Vector<WaterMeterSensorData> getLatestUpdates()
	{
		// the set of updated bins
		Vector<WaterMeterSensorData> updates = new Vector<WaterMeterSensorData>();
		
		// debug
		int i = 0;
		// profiling
		long time = System.currentTimeMillis();
		
		// generate the updates in the same order for all requests
		for (int j = 0; j < this.activeWaterMeters.size(); j++)
		{
			SimulatedWaterMeter currentBin = this.activeWaterMeters.elementAt(j);
			
			DecimalMeasure<Volume> currentFlowRate = this.fGenerator.getCurrentFlowRate(
					currentBin.getFlowAsMeasure(), currentBin.getLatestUpdate());
			
			// build the updated sensor data
			WaterMeterSensorData cData = new WaterMeterSensorData(currentFlowRate, currentBin.getNetworkLevelId());
			
			// store the update
			updates.add(cData);
			
			// inc i
			i++;
			
			// log
			WaterMeterNetwork.logger.debug("Updated [" + i + "]: Flow rate =" + currentFlowRate);
			
		}
		
		WaterMeterNetwork.logger.debug("Generated updates, Elapsed time: "
				+ ((System.currentTimeMillis() - time) / 1000.0) + "s");
		
		return updates;
	}
	
}
