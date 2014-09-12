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

import java.util.HashSet;
import java.util.Set;

import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;
import it.ismb.pertlab.pwal.wastebinsimulator.devices.WasteBin;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteBinSimulatorManager extends PollingDevicesManager<WasteBinSensorData>
{
	// Turin's bounding box, used to randomly generate latitude and longitude
	// information
	// TODO: drop this constants....
	public static final double MAX_LAT = 45.133267;
	public static final double MIN_LAT = 45.004271;
	public static final double MAX_LONG = 7.709400;
	public static final double MIN_LONG = 7.569667;
	
	// the default numbe of beans
	private int nBins = 25000;
	private Set<WasteBin> activeBins;
	
	/**
	 * 
	 */
	public WasteBinSimulatorManager()
	{
		//create inner data structures
		this.activeBins = new HashSet<WasteBin>();
	}

	public void run()
	{
		//profiling
		long time = System.currentTimeMillis();
		
		//set up the sensors
		for(int i=0; i<nBins; i++)
		{
			double currentLongitude = WasteBinSimulatorManager.MIN_LONG+Math.random()*(WasteBinSimulatorManager.MAX_LONG-WasteBinSimulatorManager.MIN_LONG);
			double currentLatitude = WasteBinSimulatorManager.MIN_LAT+Math.random()*(WasteBinSimulatorManager.MAX_LAT-WasteBinSimulatorManager.MIN_LAT);
			WasteBin currentBin = new WasteBin(currentLongitude, currentLatitude, ""+i);
			
			//store the bin
			this.activeBins.add(currentBin);
			
			//info
			log.info("Created virtual WasteBin at: "+currentLatitude+"N, "+currentLongitude+"E");
			
			//notify the addition
			/*for (DeviceListener l : deviceListener)
			{
				//l.notifyDeviceAdded(currentBin);
			}*/
		}
		
		//profiling
		log.info("Created "+nBins+", elapsed time: "+(System.currentTimeMillis()-time)+"ms");
		
		//just wait for interrumptions
		while(!t.isInterrupted())
		{
			try
			{
				//TODO add better behavior handling here: e.g. trigger waste bin emptying at random
				//just keep the manager alive
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				log.error("Exception: ", e);
			}
		}
		
		//remove the bins
		/*for(WasteBin bin : this.activeBins)
		{
			//notify the addition
			for (DeviceListener l : deviceListener)
			{
				//l.notifyDeviceRemoved(currentBin);
			}
		}*/
		this.activeBins.clear();
		
	}
	
	@Override
	protected void setBasePollingTimeMillis()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void setMinimumPollingTimeMillis()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void setTimeTolerancePercentage()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void updatePollingTime()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getNetworkType()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
