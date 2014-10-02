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

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WasteBin;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.wastebinsimulator.data.WasteBinSensorData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A device representing a simulated (fake) waste bin located at the given
 * latitude and longitude.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * 
 */
public class SimulatedWasteBin implements WasteBin,
        DataUpdateSubscriber<WasteBinSensorData>
{
    // the logger
    protected static final Logger logger = LoggerFactory
            .getLogger(SimulatedWasteBin.class);

    // the pwalId
    private String pwalId;

    // the sensor type
    private String type = DeviceType.WASTE_BIN;

    // the network type
    private String networkType = DeviceNetworkType.WASTEBINSIMULATOR;

    // the latest update time stamp
    private Date latestUpdate;

    // the sensor latitude
    private double longitude;

    // the sensor longitude
    private double latitude;

    // the temperature value as a measure
    @JsonIgnore
    private DecimalMeasure<Temperature> temperature;

    // the current fill level
    private int fillLevel;

    // the number of days in which it gets filled
    @JsonIgnore
    private int nDaysToFull;

    // the waste bin identifier
    private String id;

    // data timestamp
    private String updatedAt;

    // data expiration
    private String expiresAt;
    
    // pwal event publisher
    private PWALEventPublisher eventPublisher;

    /**
     * Creates a new {@link SimulatedWasteBin} instance specifying the bin
     * location, current fill level and temperature and the low-level unique id
     * identifying the bin.
     * 
     * @param longitude The bin longitude
     * @param latitude The bin latitude
     * @param temperature The bin temperature
     * @param fillLevel The bin fill level
     * @param id The bin id
     */
    public SimulatedWasteBin(double longitude, double latitude,
            DecimalMeasure<Temperature> temperature, int fillLevel, String id)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.temperature = temperature;
        this.fillLevel = fillLevel;
        this.id = id;
        // number of days to become full randomly changes between 1 and 7
        this.nDaysToFull = 1 + (int) Math.round(Math.random() * 6);

        // now updated for the first time
        this.latestUpdate = new Date();
        this.eventPublisher = new PWALEventPublisher();
    }

    /**
     * Creates a new {@link SimulatedWasteBin} instance specifying the current
     * bin location and the low-level unique id identifying the bin.
     * 
     * @param longitude The bin longitude
     * @param latitude The bin latitude
     * @param id The bin id
     */
    public SimulatedWasteBin(double longitude, double latitude, String id)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = id;
        this.temperature = DecimalMeasure.valueOf(0+" "+SI.CELSIUS);
        this.fillLevel = 0;
        // number of days to become full randomly changes between 1 and 7
        this.nDaysToFull = 1 + (int) Math.round(Math.random() * 6);

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
        return longitude;
    }

    /**
     * Sets the longitude of the bin location.
     * 
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * Provide the latitude of the bin location.
     * 
     * @return the latitude
     */
    public double getLatitude()
    {
        return latitude;
    }

    /**
     * Sets the latitude of the bin location.
     * 
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * Gets the current bin temperature as a value with a unit of measure.
     * 
     * @return the temperature
     */
//    @JsonIgnore
//    public DecimalMeasure<Temperature> getTemperatureAsMeasure()
//    {
//        return temperature;
//    }

    /**
     * Sets the current bin temperature as a value with a unit of measure.
     * 
     * @param temperature the temperature to set
     */
    @JsonIgnore
    public void setTemperatureAsMeasure(DecimalMeasure<Temperature> temperature)
    {
        this.temperature = temperature;
    }

    /**
     * Provides the current fill level of the bin, as a percent value.
     * 
     * @return the fillLevel
     */
    public Integer getFillLevel()
    {
        return fillLevel;
    }

    /**
     * Sets the current fill level of the bin, as a percent value.
     * 
     * @param fillLevel the fillLevel to set
     */
    public void setFillLevel(int fillLevel)
    {
        this.fillLevel = fillLevel;
        this.latestUpdate = new Date();
    }

    /**
     * Provides the average number of days in which this bin instance becomes
     * full
     * 
     * @return the nDaysToFull
     */
    @JsonIgnore
    public int getnDaysToFull()
    {
        return nDaysToFull;
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
    public void handleUpdate(WasteBinSensorData updatedData)
    {
        // extract updated values and store them in the current instance
        // variables.
        this.fillLevel = updatedData.getFillLevel();
        this.temperature = updatedData.getTemperature();

        // update the latest update time
        this.latestUpdate = new Date();
        HashMap<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("getTemperature", this.getTemperature());
        valuesMap.put("getFillLevel", this.getFillLevel());
        PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                this.updatedAt, this.getPwalId(),
                this.getExpiresAt(), valuesMap, this);
        logger.info("Device {} is publishing a new data available event on topic: {}", this.getPwalId(), this.eventPublisher.getTopics());
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
        return this.id;
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
        this.eventPublisher.setTopics(new String[]
        { PWALTopicsUtility.createDeviceNewDataTopic(DeviceNetworkType.WASTEBINSIMULATOR,
                this.getPwalId()) });
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
     * @see
     * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setId(java.lang.String
     * )
     */
    @Override
    public void setId(String id)
    {
        this.id = id;
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
        // use ISO8601 format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        this.updatedAt = df.format(this.latestUpdate);
        // return the last update as a string
        return this.updatedAt;
    }

    @Override
    public void setUpdatedAt(String updatedAt)
    {
        // use ISO8601 format
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
        {
            // try parsing the last update provided as string
            this.latestUpdate = df.parse(updatedAt);
        }
        catch (ParseException e)
        {
            SimulatedWasteBin.logger.error(
                    "Error while setting the last updated value", e);
        }

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
        u.setSymbol(SI.CELSIUS.toString());
        u.setType("basicSI");
        u.setValue("Celsius");

        return u;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * it.ismb.pertlab.pwal.api.devices.interfaces.Device#setUnit(it.ismb.pertlab
     * .pwal.api.devices.model.Unit)
     */
    @Override
    public void setUnit(Unit unit)
    {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see it.ismb.pertlab.pwal.api.devices.model.WasteBin#getTemperature()
     */
    @Override
    public Double getTemperature()
    {
        // provides the temperature value back as a double number.
        return this.temperature.getValue().doubleValue();
    }

    @Override
    public String getExpiresAt()
    {
        return this.expiresAt;
    }

    @Override
    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }
}
