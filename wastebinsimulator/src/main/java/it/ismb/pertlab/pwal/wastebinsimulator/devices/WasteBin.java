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
package it.ismb.pertlab.pwal.wastebinsimulator.devices;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class WasteBin implements DataUpdateSubscriber<WasteBinSensorData>
{
	// the sensor latitude
	private double longitude;
	
	// the sensor longitude
	private double latitude;
	
	// the temperature value as a measure
	private DecimalMeasure<Temperature> temperature;
	
	// the current fill level
	private int fillLevel;
	
	// the waste bin identifier
	private String id;
	
	/**
	 * Creates a new {@link WasteBin} instance specifying the bin location, current fill
	 * level and temperature and the low-level unique id identifying the bin.
	 * 
	 * @param longitude The bin longitude
	 * @param latitude The bin latitude
	 * @param temperature The bin temperature
	 * @param fillLevel The bin fill level
	 * @param id The bin id
	 */
	public WasteBin(double longitude, double latitude, DecimalMeasure<Temperature> temperature, int fillLevel, String id)
	{
		this.longitude = longitude;
		this.latitude = latitude;
		this.temperature = temperature;
		this.fillLevel = fillLevel;
		this.id = id;
	}
	
	/**
	 * Creates a new {@link WasteBin} instance specifying the current bin location and the low-level unique id identifying the bin.
	 * @param longitude The bin longitude
	 * @param latitude The bin latitude
	 * @param id The bin id
	 */
	public WasteBin(double longitude, double latitude, String id)
	{
		this.longitude = longitude;
		this.latitude = latitude;
		this.id = id;
	}
	
	/**
	 * Provides the longitude of the bin location.
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}
	
	/**
	 * Sets the longitude of the bin location.
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	/**
	 * Provide the latitude of the bin location.
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}
	
	/**
	 * Sets the latitude of the bin location.
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	
	/**
	 * Gets the current bin temperature as a value with a unit of measure.
	 * @return the temperature
	 */
	public DecimalMeasure<Temperature> getTemperature()
	{
		return temperature;
	}
	
	/**
	 * Sets the current bin temperature as a value with a unit of measure.
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(DecimalMeasure<Temperature> temperature)
	{
		this.temperature = temperature;
	}
	
	/**
	 * Provides the current fill level of the bin, as a percent value.
	 * @return the fillLevel
	 */
	public int getFillLevel()
	{
		return fillLevel;
	}
	
	/**
	 * Sets the current fill level of the bin, as a percent value.
	 * @param fillLevel
	 *            the fillLevel to set
	 */
	public void setFillLevel(int fillLevel)
	{
		this.fillLevel = fillLevel;
	}
	
	public void handleUpdate(WasteBinSensorData updatedData)
	{
		// extract updated values and store them in the current instance
		// variables.
		this.fillLevel = updatedData.getFillLevel();
		this.temperature = updatedData.getTemperature();
		
	}
	
	public String getNetworkLevelId()
	{
		return this.id;
	}
	
}
