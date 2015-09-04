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
package it.ismb.pertlab.pwal.smartbindriver.devices;

import java.util.Date;
import java.util.HashMap;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.UnitFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import metrics.smartbin.api.StandardSensorMetric;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WasteBin;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;
import it.ismb.pertlab.pwal.smartbindriver.data.IESmartBinData;

/**
 * A class representing a WasteBin exposed by the SmartBin service
 * (http://www.smartbin.com/) through their "private" web service API.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class IESmartBin implements WasteBin, DataUpdateSubscriber<IESmartBinData>
{
	// the real bin represented by this object
	private StandardSensorMetric realBin;
	
	// the logger
	protected static final Logger logger = LoggerFactory.getLogger(IESmartBin.class);
	
	// the pwalId
	private String pwalId;
	
	// the sensor type
	private String type = DeviceType.WASTE_BIN;
	
	// the network type
	private String networkType = DeviceNetworkType.IESMARTBIN;
	
	// the latest update time stamp
	private Date latestUpdate;
	
	// the temperature value as a measure
	@JsonIgnore
	private DecimalMeasure<Temperature> temperatureAsMeasure;
	
	// the current fill level
	private double fillLevel;
	
	// data timestamp
	private String updatedAt;
	
	// data expiration
	private String expiresAt;
	
	// pwal event publisher
	private PWALEventPublisher eventPublisher;
	
	/**
	 * @param binMetric
	 * 
	 */
	public IESmartBin(StandardSensorMetric binMetric)
	{
		// store the corresponding low-level sensor metric
		this.realBin = binMetric;
		
		// initialize the current instance values
		
		// now updated for the first time
		this.latestUpdate = new Date();
		
		// the current fill level
		this.fillLevel = binMetric.getPercent();
		
		// the temperature as a measure (Absolute 0 as invalid value)
		this.temperatureAsMeasure = DecimalMeasure.valueOf(binMetric.getTemperature() + " " + SI.CELSIUS.toString());
		
		// the PWAL Event Publisher
		this.eventPublisher = new PWALEventPublisher();
		
		// the Unit Format for Celsius degrees
		UnitFormat uf = UnitFormat.getInstance();
		uf.label(SI.CELSIUS, "C");
		uf.alias(SI.CELSIUS, "C");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getPwalId()
	 */
	public String getPwalId()
	{
		return this.pwalId;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setPwalId(java.lang
	 * .String)
	 */
	public void setPwalId(String pwalId)
	{
		// store the assigned pwal id
		this.pwalId = pwalId;
		
		// generate the corresponding device topic
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDataFromDeviceTopic(
				DeviceNetworkType.IESMARTBIN, this.getPwalId()) });
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getId()
	 */
	public String getId()
	{
		// get the bin id as a String
		return "" + this.realBin.getId();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getType()
	 */
	public String getType()
	{
		// get the bin type, i.e. DeviceType.WASTE_BIN
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getNetworkType()
	 */
	public String getNetworkType()
	{
		// get the network type, i.e., DeviceNetworkType.IESMARTBIN.
		return this.networkType;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUpdatedAt()
	 */
	public String getUpdatedAt()
	{
		return this.updatedAt;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setUpdatedAt(java.
	 * lang.String)
	 */
	public void setUpdatedAt(String updatedAt)
	{
		this.updatedAt = updatedAt;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getExpiresAt()
	 */
	public String getExpiresAt()
	{
		return this.expiresAt;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setExpiresAt(java.
	 * lang.String)
	 */
	public void setExpiresAt(String expiresAt)
	{
		this.expiresAt = expiresAt;
	}
	
	/**
	 * Provides the latest update for this bin data, i.e., the time instant at
	 * which the data has been received from the SmartBin web service.
	 * 
	 * @return the latestUpdate
	 */
	@JsonIgnore
	public Date getLatestUpdate()
	{
		return latestUpdate;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getLocation()
	 */
	public Location getLocation()
	{
		// build a new location instance given the current latitude and
		// longitude.
		Location l = new Location();
		l.setLat(Double.valueOf(this.realBin.getLatitude()));
		l.setLon(Double.valueOf(this.realBin.getLongitude()));
		
		return l;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUnit()
	 */
	public Unit getUnit()
	{
		// describes the unit of measure according to the pWAL inner format.
		
		Unit u = new Unit();
		u.setSymbol("");
		u.setType("none");
		u.setValue("unknown");
		
		return u;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.model.WasteBin#getTemperature()
	 */
	@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState")
	public Double getTemperature()
	{
		// provides the temperature value back as a double number.
		return this.temperatureAsMeasure.getValue().doubleValue();
	}
	
	/**
	 * Gets the current bin temperature as a value with a unit of measure.
	 * 
	 * @return the temperature
	 */
	 @JsonIgnore
	public DecimalMeasure<Temperature> getTemperatureAsMeasure()
	{
		 return this.temperatureAsMeasure;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.model.WasteBin#getFillLevel()
	 */
	 @SemanticModel(value="http://almanac-project.eu/ontologies/smartcity.owl#FillLevelState", name = "class")
	public Double getLevel()
	{
		return this.fillLevel;
	}
	
	public void setLevel(double fillLevel)
	{
		this.fillLevel = fillLevel;
		this.latestUpdate = new Date();
	}
	
	@JsonIgnore
	public DecimalMeasure<Quantity> getLevelAsMeasure()
	{
		return DecimalMeasure.valueOf(this.getLevel()+" "+NonSI.PERCENT);
	}
	
	
	public void handleUpdate(IESmartBinData updatedData)
	{
		// extract updated values and store them in the current instance
		// variables.
		this.fillLevel = updatedData.getFillLevel();
		this.temperatureAsMeasure = DecimalMeasure.valueOf(updatedData.getTemperature() + " " + SI.CELSIUS.toString());
		
		// update the latest update time
		this.latestUpdate = new Date();
		
		// prepare the set of "update" values
		HashMap<String, Object> valuesMap = new HashMap<String, Object>();
		valuesMap.put("getTemperature", this.getTemperature());
		valuesMap.put("getLevel", this.getLevel());
		
		// generate a new PWAL data event
		PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.getUpdatedAt(), this.getPwalId(),
				this.getExpiresAt(), valuesMap, this);
		
		// log generation (debug)
		logger.info("Device {} is publishing a new data available event on topic: {}", this.getPwalId(),
				this.eventPublisher.getTopics());
		
		// publish the event on the pwal event bus
		this.eventPublisher.publish(event);
		
	}
	
	/**
	 * Returns the network-level id assigned to the real bin represented by this
	 * IESmartBin instance (i.e., the id assigned by the SmartBin service).
	 */
	public String getNetworkLevelId()
	{
		return this.getId();
	}
	
	// -------------- Empty methods ----------------
	
	public void setId(String id)
	{
		// do nothing
	}
	
	public void setLocation(Location location)
	{
		// do nothing
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setUnit(it.ismb.pertlab
	 * .pwal.api.devices.model.Unit)
	 */
	public void setUnit(Unit unit)
	{
		// do nothing
	}

	@Override
	public Integer getDepth()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
