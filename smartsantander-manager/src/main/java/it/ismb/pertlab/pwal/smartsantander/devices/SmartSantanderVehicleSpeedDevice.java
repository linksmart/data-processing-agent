package it.ismb.pertlab.pwal.smartsantander.devices;

import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

public class SmartSantanderVehicleSpeedDevice implements VehicleSpeed {

	String id; 
	String type = DeviceType.VEHICLE_SPEED;
	String networkType = DeviceNetworkType.SMARTSANTANDER;
	Double latitude;
	Double longitude;
	String dateLastMeasurement;
	SmartSantanderRestClient restClient;
	
	public SmartSantanderVehicleSpeedDevice(SmartSantanderRestClient restClient) {
		this.restClient = restClient;
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

	public Double getMedianSpeed() {
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if(measure != null)
			return measure.getMedian_speed();
		else
			return -1.0;
	}

	public Double getAverageSpeed() {
		SmartSantanderTrafficIntensityJson measure = this.restClient.getLastMeasures(this.id);
		if(measure != null)
			return measure.getAverage_speed();
		else
			return -1.0;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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
}
