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
package it.ismb.pertlab.pwal.smartbindriver.data;

import metrics.smartbin.api.StandardSensorMetric;

/**
 * A class for wrapping the updated information regarding a SmartBin waste bin,
 * to be handled by the PWAL drivers.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class IESmartBinData
{
	// the bin sensor battery level
	private double battery;
	
	// the nDays...???
	private int days;
	
	// ??? check with the api definition
	private int distanceToTarget;
	
	// the current fill level in percentage
	private int fillLevel;
	
	// the timestamp in which the bin was last serviced
	private String lastServiced;
	
	// the strenght of the mobile signal
	private double mobileSignal;
	
	// the current temperature value
	private double temperature;
	
	/**
	 * 
	 */
	public IESmartBinData()
	{
		// empty constructor
	}
	
	/**
	 * Creates a new IESmartBinData instance by extracting relevant values from
	 * the given {@link StandardSensorMetric} instance representing the bin.
	 * 
	 * @param binMetric
	 *            The bin metric object from which extracting the relevant data.
	 */
	public IESmartBinData(StandardSensorMetric binMetric)
	{
		// initialize the relevant instance varaiables
		this.battery = binMetric.getBattery();
		this.days = binMetric.getDays();
		this.distanceToTarget = binMetric.getDistanceToTarget();
		this.fillLevel = binMetric.getPercent();
		this.lastServiced = binMetric.getLastServiced();
		this.mobileSignal = binMetric.getMobileSignal();
		this.temperature = binMetric.getTemperature();
	}
	
	/**
	 * Provides back the current battery level for the bin represented by this
	 * "update" object.
	 * 
	 * @return the battery level as a double.
	 */
	public double getBattery()
	{
		return battery;
	}
	
	/**
	 * Sets the battery level associated to the bin represented by this "update"
	 * data. Typically adopted in conjunction with bean-pattern instantiation.
	 * 
	 * @param battery
	 *            the battery level to set.
	 */
	public void setBattery(double battery)
	{
		this.battery = battery;
	}
	
	/**
	 * Returns the amount of days since last servicing (this is a guess and must
	 * be verified)
	 * 
	 * @return the amount of days since last servicing.
	 */
	public int getDays()
	{
		return days;
	}
	
	/**
	 * Sets the amount of days since last servicing. Typically adopted in
	 * conjunction with bean-pattern instantiation.
	 * 
	 * @param days
	 *            the amount days to set.
	 */
	public void setDays(int days)
	{
		this.days = days;
	}
	
	/**
	 * @return the distanceToTarget
	 */
	public int getDistanceToTarget()
	{
		return distanceToTarget;
	}
	
	/**
	 * @param distanceToTarget
	 *            the distanceToTarget to set
	 */
	public void setDistanceToTarget(int distanceToTarget)
	{
		this.distanceToTarget = distanceToTarget;
	}
	
	/**
	 * Provides the current fill level of the bin associated to this "update"
	 * data object.
	 * 
	 * @return the current fillLevel as an integer between 0 and 100.
	 */
	public int getFillLevel()
	{
		return fillLevel;
	}
	
	/**
	 * Sets the current fill level of the bin associated to this "update" data
	 * object. Typically adopted in conjunction with bean-pattern instantiation.
	 * 
	 * @param fillLevel
	 *            the fillLevel to set.
	 */
	public void setFillLevel(int fillLevel)
	{
		this.fillLevel = fillLevel;
	}
	
	/**
	 * Return the timestamp at which the bin was last serviced (TODO: check the
	 * timestamp format)
	 * 
	 * @return the lastServiced date, as a String.
	 */
	public String getLastServiced()
	{
		return lastServiced;
	}
	
	/**
	 * Sets the timestamp at which the bin was last serviced (TODO: check the
	 * timestamp format). Typically adopted in conjunction with bean-pattern
	 * instantiation.
	 * 
	 * @param lastServiced
	 *            the lastServiced to set
	 */
	public void setLastServiced(String lastServiced)
	{
		this.lastServiced = lastServiced;
	}
	
	/**
	 * Get the level of the mobile signal as detected by the bin instance
	 * represented by this update object.
	 * 
	 * @return the mobileSignal as a double value.
	 */
	public double getMobileSignal()
	{
		return mobileSignal;
	}
	
	/**
	 * Sets the level of the mobile signal as detected by the bin instance
	 * represented by this update object. Typically adopted in conjunction with
	 * bean-pattern instantiation.
	 * 
	 * @param mobileSignal
	 *            the mobileSignal to set
	 */
	public void setMobileSignal(double mobileSignal)
	{
		this.mobileSignal = mobileSignal;
	}
	
	/**
	 * Get the temperature sensed by the bin represented by this "update"
	 * object.
	 * 
	 * @return the temperature in celsius degrees.
	 */
	public double getTemperature()
	{
		return temperature;
	}
	
	/**
	 * Sets the temperature sensed by the bin represented by this "update"
	 * object. Typically adopted in conjunction with bean-pattern instantiation.
	 * 
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(double temperature)
	{
		this.temperature = temperature;
	}
}
