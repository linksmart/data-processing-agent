package it.ismb.pertlab.pwal.smartsantander.devices;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

public class SmartSantanderVehicleCounterDevice implements VehicleCounter {

	String id;
	String pwalId;
	String type = DeviceType.VEHICLE_COUNTER;
	String networkType;
	Location location;
	String dateLastMeasurement;
	SmartSantanderRestClient restClient;
	SmartSantanderTrafficIntensityJson measure;
	
	public SmartSantanderVehicleCounterDevice(SmartSantanderRestClient restClient, String networkType)
	{
		this.restClient = restClient;
		this.networkType = networkType;
		this.measure = new SmartSantanderTrafficIntensityJson();
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public Double getOccupancy() {
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if(measure != null)
			return measure.getOccupancy();
		else
			return -1.0;	
	}

	public Double getCount() {
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if(measure != null)
			return measure.getCount();
		else
			return -1.0;	
	}

	public String getNetworkType() {
		return this.networkType;
	}

	public String getDateLastMeasurement() {
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if(measure != null)
			return measure.getDate();
		else
			return null;
	}

	@Override
	public String getPwalId() {
		return pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId = pwalId;
	}

	@Override
	public String getUpdatedAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location location) {
		this.location=location;
	}

	@Override
	public Unit getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUnit(Unit unit) {
		// TODO Auto-generated method stub
		
	}
}