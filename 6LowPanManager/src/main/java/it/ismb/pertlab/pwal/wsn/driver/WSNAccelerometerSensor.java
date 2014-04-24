package it.ismb.pertlab.pwal.wsn.driver;

import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;

public class WSNAccelerometerSensor extends WSNBaseDevice implements Accelerometer{ 
	private String id;
	private String updatedAt;
	private Location location;
	private Double xAcceleration;
	private Double yAcceleration;
	private Double zAcceleration;
	
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
		return DeviceType.ACCELEROMETER;
	}

	@Override
	public Double getXAcceleration() {
		
		return xAcceleration;
	}

	@Override
	public Double getYAcceleration() {
		return yAcceleration;
	}

	@Override
	public Double getZAcceleration() {
		return zAcceleration;
	}

	@Override
	public void notifyMessage(byte[] payload) {
		
	}

	@Override
	public String getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

}