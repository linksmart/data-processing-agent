package it.ismb.pertlab.pwal.wsn.driver;

import java.nio.ByteBuffer;
import java.util.Arrays;

import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.wsn.driver.customUDP.Definitions;

public class WSNAccelerometerSensor extends WSNBaseDevice implements Accelerometer{
	private String pwalId;
	private String id;
	private String updatedAt;
	private Location location;
	private Unit unit;
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
		requestValues();
		return xAcceleration;
	}

	@Override
	public Double getYAcceleration() {
		requestValues();
		return yAcceleration;
	}

	@Override
	public Double getZAcceleration() {
		requestValues();
		return zAcceleration;
	}
	
	private void requestValues()
	{
		byte[] data=new byte[]{Definitions.REQ_DATA, 0x00, Definitions.SENSOR_ACCEL, 0x00};
		synchronized(this){
			super.getManager().sendMessage(data, this.getAddress());
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void notifyMessage(byte[] payload) {
		
		this.xAcceleration=new Double(getInt(payload, 2, 6));
		this.yAcceleration=new Double(getInt(payload, 6, 10));
		this.zAcceleration=new Double(getInt(payload, 10, 14));
		synchronized (this) {
			this.notify();
		}
	}
	
	private int getInt(byte [] payload, int start, int end){
		ByteBuffer buf =  ByteBuffer.wrap(Arrays.copyOfRange( payload,  start, end) ); // big-endian by default
		
		return buf.getInt();
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

	@Override
	public Unit getUnit() {
		return unit;
	}

	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public String getPwalId() {
		return this.pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId=pwalId;
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.SIXLOWPAN;
	}

}
