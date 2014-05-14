package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

public class TemperatureSensor extends BaseSerialDevice implements Thermometer{

	private String id;
	private String pwalId;
	private SerialManager sm;
	private Double temp;
	
	public TemperatureSensor(SerialManager sm){
		this.sm=sm;
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
		return DeviceType.THERMOMETER;
	}

	@Override
	public void messageReceived(String payload) {
		this.temp=Double.valueOf(payload);
	}

	@Override
	public Double getTemperature() {
		return temp;
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
		return sm.getNetworkType();
	}
	@Override
	public String getPwalId() {
		return pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId=pwalId;
	}

}
