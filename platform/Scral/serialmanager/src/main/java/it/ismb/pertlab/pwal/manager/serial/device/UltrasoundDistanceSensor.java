package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.DistanceSensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

public class UltrasoundDistanceSensor extends BaseSerialDevice implements DistanceSensor{

	private String id;
	private String pwalId;
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
	public String getNetworkType() {
		return manager.getNetworkType();
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
