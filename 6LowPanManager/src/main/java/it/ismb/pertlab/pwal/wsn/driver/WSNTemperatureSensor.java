package it.ismb.pertlab.pwal.wsn.driver;

import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.wsn.driver.customUDP.Definitions;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class WSNTemperatureSensor extends WSNBaseDevice implements Thermometer{

	private String id;
	private String pwalId;
	private Double temperature;
	
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
		byte[] data=new byte[]{Definitions.REQ_DATA, 0x00, Definitions.SENSOR_TEMP, 0x00};
		synchronized(this){
			super.getManager().sendMessage(data, this.getAddress());
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return temperature;
	}

	@Override
	public void notifyMessage(byte[] payload) {
		this.temperature=new Double(getInt(payload, 2, payload.length));
		synchronized (this) {
			this.notify();
		}
	}

	private int getInt(byte [] payload, int start, int end){
		ByteBuffer buf =  ByteBuffer.wrap(Arrays.copyOfRange( payload,  start, end) ); // big-endian by default
		return buf.getInt();
	}

	@Override
	public String getPwalId() {
		return pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId=pwalId;
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
		return getManager().getNetworkType();
	}
}
