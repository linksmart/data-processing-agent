package it.ismb.pertlab.pwal.smartsantander.devices;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

public class SmartSantanderVehicleCounterDevice implements VehicleCounter,
		DataUpdateSubscriber<SmartSantanderTrafficIntensityJson>
{
	
	String id;
	String pwalId;
	String type = DeviceType.VEHICLE_COUNTER;
	String networkType;
	Location location;
	String dateLastMeasurement;
	SmartSantanderRestClient restClient;
	SmartSantanderTrafficIntensityJson measure;
	private String updatedAt;
	private String expiresAt;
	private Double occupancy;
	private Double count;
	private PWALEventPublisher eventPublisher;
	private Logger log = LoggerFactory.getLogger(SmartSantanderVehicleCounterDevice.class);
	
	public SmartSantanderVehicleCounterDevice(SmartSantanderRestClient restClient, String networkType)
	{
		this.restClient = restClient;
		this.networkType = networkType;
		this.measure = new SmartSantanderTrafficIntensityJson();
		this.eventPublisher = new PWALEventPublisher();
		this.count = 0.0;
		this.occupancy = 0.0;	
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public Double getOccupancy()
	{
		/*
		 * SmartSantanderTrafficIntensityJson measure =
		 * this.restClient.getLastMeasures(this.id); if (measure != null) return
		 * measure.getOccupancy(); else return -1.0;
		 */
		return this.occupancy;
	}
	
	public Double getCount()
	{
		/*
		 * SmartSantanderTrafficIntensityJson measure =
		 * this.restClient.getLastMeasures(this.id); if (measure != null) return
		 * measure.getCount(); else return -1.0;
		 */
		return this.count;
	}
	
	public String getNetworkType()
	{
		return this.networkType;
	}
	
	public String getDateLastMeasurement()
	{
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if (measure != null)
			return measure.getDate();
		else
			return null;
	}
	
	@Override
	public String getPwalId()
	{
		return pwalId;
	}
	
	@Override
	public void setPwalId(String pwalId)
	{
		this.pwalId = pwalId;
	          this.eventPublisher.setTopics(new String[]
	                        { PWALTopicsUtility.createNewDataFromDeviceTopic(DeviceNetworkType.SMARTSANTANDER,
	                                this.getPwalId()) });
	}
	
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
	
	@Override
	public Location getLocation()
	{
		return location;
	}
	
	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	@Override
	public Unit getUnit()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setUnit(Unit unit)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleUpdate(SmartSantanderTrafficIntensityJson updatedData)
	{
		// cast the received data
		SmartSantanderTrafficIntensityJson updatedJson = (SmartSantanderTrafficIntensityJson) updatedData;
		
		// get the measures
		if(updatedJson.getCount() != null)
		    this.count = updatedJson.getCount();
		if(updatedJson.getOccupancy() != null)
		    this.occupancy = updatedJson.getOccupancy();
		HashMap<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("getCount", this.getCount());
		valuesMap.put("getOccupancy", this.getOccupancy());
                PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                        this.updatedAt, this.getPwalId(), this.getExpiresAt(),
                        valuesMap, this);
                log.debug("Device {} is publishing new data available event on topic: {}", this.getPwalId(), this.eventPublisher.getTopics());
                this.eventPublisher.publish(event);
	}
	
	@Override
	public String getNetworkLevelId()
	{
		// TODO Auto-generated method stub
		return this.id;
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
