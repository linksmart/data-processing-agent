/*
 * PWAL -Water Meter Data Simulator
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
package it.ismb.pertlab.pwal.watermetersimulator.devices;

import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;
import it.ismb.pertlab.pwal.watermetersimulator.data.WaterMeterSensorData;

import java.util.Date;
import java.util.HashMap;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Volume;
import javax.measure.unit.SI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A device representing a simulated (fake) waste bin located at the given
 * latitude and longitude.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * 
 */
public class SimulatedWaterMeter implements FlowMeter, DataUpdateSubscriber<WaterMeterSensorData>
{
	// the logger
	protected static final Logger logger = LoggerFactory.getLogger(SimulatedWaterMeter.class);
	
	// the pwalId
	private String pwalId;
	
	// the sensor type
	private String type = DeviceType.FLOW_METER_SENSOR;
	
	// the network type
	private String networkType = DeviceNetworkType.WATERMETERSIMULATOR;
	
	// the latest update time stamp
	private Date latestUpdate;
	
	// the current flow-rate as a measure
	private DecimalMeasure<Volume> flowRate;
	
	// the sensor latitude
	private double latitude;
	
	// the sensor longitude
	private double longitude;
	
	// data timestamp
	private String updatedAt;
	
	// data expiration
	private String expiresAt;
	
	// the unique id of this bin
	private String id;
	
	// pwal event publisher
	private PWALEventPublisher eventPublisher;
	
	public SimulatedWaterMeter(double latitude, double longitude, String id)
	{
		// store the latitude and longitude
		this.latitude = latitude;
		this.longitude = longitude;
		
		//store the id
		this.id = id;
		
		// set the current flow rate at 0
		this.flowRate = DecimalMeasure.valueOf("0 " + (SI.CUBIC_METRE));
		
		// now updated for the first time
		this.latestUpdate = new Date();
		this.eventPublisher = new PWALEventPublisher();
	}
	
	/**
	 * Provides the longitude of the bin location.
	 * 
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return this.longitude;
	}
	
	/**
	 * Provide the latitude of the bin location.
	 * 
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return this.latitude;
	}
	
	@JsonIgnore
	public DecimalMeasure<Volume> getFlowAsMeasure()
	{
		return this.flowRate;
	}
	
	/**
	 * Sets the current bin temperature as a value with a unit of measure.
	 * 
	 * @param temperature
	 *            the temperature to set
	 */
	@JsonIgnore
	public void setFlowAsMeasure(DecimalMeasure<Volume> flowRate)
	{
		this.flowRate = flowRate;
	}
	
	/**
	 * Provides the latest update for this bin data
	 * 
	 * @return the latestUpdate
	 */
	@JsonIgnore
	public Date getLatestUpdate()
	{
		return latestUpdate;
	}
	
	/**
	 * Handles updates coming from the network as result of the applied polling
	 * mechanism.
	 */
	public void handleUpdate(WaterMeterSensorData updatedData)
	{
		// extract updated values and store them in the current instance
		// variables.
		this.flowRate = updatedData.getFlowRate();
		
		// update the latest update time
		this.latestUpdate = new Date();
		HashMap<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("getFlow", this.getFlow());
		PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.getUpdatedAt(), this.getPwalId(),
				this.getExpiresAt(), valuesMap, this);
		logger.info("Device {} is publishing a new data available event on topic: {}", this.getPwalId(),
				this.eventPublisher.getTopics());
		this.eventPublisher.publish(event);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber#
	 * getNetworkLevelId()
	 */
	@JsonIgnore
	@Override
	public String getNetworkLevelId()
	{
		return this.getId();
	}
	
	/****************************************************************************
	 * 
	 * Methods inherited from device
	 * 
	 ****************************************************************************/
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getPwalId()
	 */
	@Override
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
	@Override
	public void setPwalId(String pwalId)
	{
		this.pwalId = pwalId;
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDataFromDeviceTopic(
				DeviceNetworkType.WATERMETERSIMULATOR, this.getPwalId()) });
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getId()
	 */
	@Override
	public String getId()
	{
		return this.id;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getType()
	 */
	@Override
	public String getType()
	{
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getNetworkType()
	 */
	@Override
	public String getNetworkType()
	{
		return this.networkType;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUpdatedAt()
	 */
	@Override
	public String getUpdatedAt()
	{
		return this.updatedAt;
	}
	
	@Override
	public void setUpdatedAt(String updatedAt)
	{
		this.updatedAt = updatedAt;
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getLocation()
	 */
	@Override
	public Location getLocation()
	{
		// build a new location instance given the current latitude and
		// longitude.
		Location l = new Location();
		l.setLat(this.latitude);
		l.setLon(this.longitude);
		
		return l;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setLocation(it.ismb
	 * .pertlab.pwal.api.devices.model.Location)
	 */
	@Override
	public void setLocation(Location location)
	{
		// extracts latitude and longitude from the given location object
		this.latitude = location.getLat();
		this.longitude = location.getLon();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.Device#getUnit()
	 */
	@Override
	public Unit getUnit()
	{
		// describes the unit of measure according to the pWAL inner format.
		Unit u = new Unit();
		u.setSymbol(SI.CUBIC_METRE.toString());
		u.setType("derivedSI");
		u.setValue(SI.CUBIC_METRE.toString());
		
		return u;
	}
	
	@Override
	public void setUnit(Unit unit)
	{
	}
	
	@Override
	public void setId(String id)
	{
		
	}
	
	@Override
	public String getExpiresAt()
	{
		return this.expiresAt;
	}
	
	@Override
	public void setExpiresAt(String expiresAt)
	{
		
	}
	
	@Override
	@SemanticModel(value="http://almanac-project.eu/ontologies/smartcity.owl#FlowRateState", name = "class")
	public Double getFlow()
	{
		// TODO Auto-generated method stub
		return this.getFlowAsMeasure().doubleValue(this.getFlowAsMeasure().getUnit());
	}
}
