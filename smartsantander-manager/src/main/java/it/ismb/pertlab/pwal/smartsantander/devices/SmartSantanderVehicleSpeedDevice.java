package it.ismb.pertlab.pwal.smartsantander.devices;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

public class SmartSantanderVehicleSpeedDevice implements VehicleSpeed,
		DataUpdateSubscriber<SmartSantanderTrafficIntensityJson>
{
	
	String id;
	String pwalId;
	String type = DeviceType.VEHICLE_SPEED;
	String networkType = DeviceNetworkType.SMARTSANTANDER;
	Location location;
	String dateLastMeasurement;
	SmartSantanderRestClient restClient;
	
	private Double occupancy = 0.0;
	private Double count = 0.0;
	private Double medianSpeed = 0.0;
	private Double averageSpeed = 0.0;
	
	public SmartSantanderVehicleSpeedDevice(SmartSantanderRestClient restClient, String networkType)
	{
		this.restClient = restClient;
		this.networkType = networkType;
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
	
	public Double getMedianSpeed()
	{
		/*
		 * SmartSantanderTrafficIntensityJson measure =
		 * this.restClient.getLastMeasures(this.id); if (measure != null) return
		 * measure.getMedian_speed(); else return -1.0;
		 */
		return this.medianSpeed;
	}
	
	public Double getAverageSpeed()
	{
		/*
		 * SmartSantanderTrafficIntensityJson measure =
		 * this.restClient.getLastMeasures(this.id); if (measure != null) return
		 * measure.getAverage_speed(); else return -1.0;
		 */
		return this.averageSpeed;
	}
	
	public String getNetworkType()
	{
		return this.networkType;
	}
	
	public String getDateLastMeasurement()
	{
		/*
		 * SmartSantanderTrafficIntensityJson measure =
		 * this.restClient.getLastMeasures(this.id); if (measure != null) return
		 * measure.getDate(); else return null;
		 */
		return this.dateLastMeasurement;
	}
	
	@Override
	public String getPwalId()
	{
		return this.pwalId;
	}
	
	@Override
	public void setPwalId(String pwalId)
	{
		this.pwalId = pwalId;
	}
	
	@Override
	public String getUpdatedAt()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setUpdatedAt(String updatedAt)
	{
		// TODO Auto-generated method stub
		
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
		this.count = updatedJson.getCount();
		this.occupancy = updatedJson.getOccupancy();
		this.medianSpeed = updatedJson.getMedian_speed();
		this.averageSpeed = updatedJson.getAverage_speed();
		this.dateLastMeasurement = updatedJson.getDate();
		
	}
	
	@Override
	public String getNetworkLevelId()
	{
		// TODO Auto-generated method stub
		return this.id;
	}
}
