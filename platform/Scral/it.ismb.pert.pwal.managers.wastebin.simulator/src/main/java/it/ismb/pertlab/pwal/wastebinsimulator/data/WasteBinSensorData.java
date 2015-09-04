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
package it.ismb.pertlab.pwal.wastebinsimulator.data;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;

/**
 * A class for representing the sensor data for a given waste bin, used as
 * payload for network-level events.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteBinSensorData
{
	// the temperature data
	private DecimalMeasure<Temperature> temperature;
	
	// the waste bin fill level (percentage number between 0 and 100)
	private double fillLevel;
	
	// the waste bin network id
	private String lUID;
	
	/**
	 * Builds a new waste bin data, holding the relevant sensor measurements for
	 * smart waste bins. It does not account for the bin location as it is
	 * property of the bin itself and not a measure.
	 * 
	 * @param temperature
	 *            The temperature value.
	 * @param temperatureUOM
	 *            The unit of measure for temperature values
	 * @param fillLevel
	 *            The fill level
	 * @param lUID
	 *            The low level (network) id of the device generating this data
	 */
	public WasteBinSensorData(DecimalMeasure<Temperature> temperature, double fillLevel, String lUID)
	{
		this.temperature = temperature;
		this.fillLevel = fillLevel;
		this.lUID = lUID;
	}
	
	/**
	 * Gets the temperature value as a number having a unit of measure
	 * 
	 * @return the temperature
	 */
	public DecimalMeasure<Temperature> getTemperature()
	{
		return temperature;
	}
	
	/**
	 * Sets the temperature as a number having a unit of measure
	 * 
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(DecimalMeasure<Temperature> temperature)
	{
		this.temperature = temperature;
	}
	
	/**
	 * Gets the current fill level of the waste bin as a percentual value
	 * between 0 and 100
	 * 
	 * @return the fillLevel
	 */
	public double getFillLevel()
	{
		return fillLevel;
	}
	
	/**
	 * Sets the current fill level of the waste bin as a percentual value
	 * between 0 and 100
	 * 
	 * @param fillLevel
	 *            the fillLevel to set
	 */
	public void setFillLevel(int fillLevel)
	{
		// handles value setting saturating to 0 and 100, respectively
		if (fillLevel > 0)
			this.fillLevel = (fillLevel > 100) ? 100 : fillLevel;
		else
			this.fillLevel = 0;
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
