package it.ismb.pertlab.pwal.manager.thermometer.device;

import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;

public class ThermometerFakeDevice implements Thermometer{

	private String id;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getType() {
		return DeviceType.THERMOMETER;
	}

	@Override
	public Double getTemperature() {
		return ((30-18)*Math.random())+18;
	}

	@Override
	public Double getLatitude() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLatitude(Double latitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getLongitude() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLongitude(Double longitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNetworkType() {
		// TODO Auto-generated method stub
		return null;
	}

}
