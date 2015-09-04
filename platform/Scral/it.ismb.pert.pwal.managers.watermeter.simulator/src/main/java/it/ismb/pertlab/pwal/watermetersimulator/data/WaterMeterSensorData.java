package it.ismb.pertlab.pwal.watermetersimulator.data;

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

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Volume;

/**
 * A class for representing the sensor data for a given waste bin, used as
 * payload for network-level events.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WaterMeterSensorData
{
	// the temperature data
	private DecimalMeasure<Volume> flowRate;
	
	// the waste bin network id
	private String lUID;
	
	public WaterMeterSensorData(DecimalMeasure<Volume> flowRate, String lUID)
	{
		this.flowRate = flowRate;
		this.lUID = lUID;
	}
	
	public DecimalMeasure<Volume> getFlowRate()
	{
		return this.flowRate;
	}
	
	/**
	 * @param flowRate
	 *            the flowRate to set
	 */
	public void setFlowRate(DecimalMeasure<Volume> flowRate)
	{
		this.flowRate = flowRate;
	}
	
	/**
	 * Sets the low level (network) id of the device generating this data
	 * 
	 * @return the lUID
	 */
	public String getlUID()
	{
		return lUID;
	}
	
	/**
	 * Gets the low level (network) id of the device generating this data
	 * 
	 * @param lUID
	 *            the lUID to set
	 */
	public void setlUID(String lUID)
	{
		this.lUID = lUID;
	}
	
}
