package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.DistanceSensor;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

public class UltrasoundDistanceSensor extends BaseSerialDevice implements DistanceSensor{

	private String id;
	//for future use
	private SerialManager manager;
	private Double distanceInch;
	
	public UltrasoundDistanceSensor(SerialManager manager){
		this.manager=manager;
	}
	
	@Override
	public Double getDistanceCm() {
		return distanceInch*2.54;
	}

	@Override
	public Double getDistanceInch() {
		return distanceInch;
	}

	@Override
	public void messageReceived(String payload) {
		distanceInch=Double.parseDouble(payload);
	}

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
		return DeviceType.DISTANCE_SENSOR;
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
		return manager.getNetworkType();
	}
}
