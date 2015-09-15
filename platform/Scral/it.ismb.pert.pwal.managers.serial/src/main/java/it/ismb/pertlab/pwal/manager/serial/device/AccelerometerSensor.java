package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

public class AccelerometerSensor  extends BaseSerialDevice implements Accelerometer {

	private String id;
	private String pwalId;
	private SerialManager sm;
	private Double xAxis,yAxis,zAxis;
	
	public AccelerometerSensor (SerialManager sm){
		this.sm=sm;
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
	public String getNetworkType() {
		return sm.getNetworkType();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(Location location) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void messageReceived(String payload) {
		//parse payload
		String[] data=payload.split(",");
		
		String[] xdata=data[0].split(":");
		String[] ydata=data[1].split(":");
		String[] zdata=data[2].split(":");
		
		this.xAxis = Double.valueOf(xdata[1]);
		this.yAxis = Double.valueOf(ydata[1]);
		this.zAxis = Double.valueOf(zdata[1]);
		
	}

	@Override
	public Double getXAcceleration() {
		return xAxis;
	}

	@Override
	public Double getYAcceleration() {
		return yAxis;
	}

	@Override
	public Double getZAcceleration() {
		return zAxis;
	}

    @Override
    public String getExpiresAt()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setExpiresAt(String expiresAt)
    {
        // TODO Auto-generated method stub
        
    }

}
