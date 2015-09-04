package it.ismb.pertlab.pwal.manager.serial.device;

import java.util.Calendar;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Resistance;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

//TODO:add event support
public class GroundResistanceMeter extends BaseSerialDevice implements Resistance{
	private String updatedAt;
	private String pwalId;
	private String id;
	private Location location;
	private Unit unit;
	private Integer ohm;
	private SerialManager manager;
	
	public GroundResistanceMeter(SerialManager manager) {
		this.manager=manager;
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
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getType() {
		return DeviceType.RESISTANCE;
	}

	@Override
	public String getNetworkType() {
		return manager.getNetworkType();
	}

	@Override
	public String getUpdatedAt() {
		return this.updatedAt;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt=updatedAt;
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
		return unit;
	}

	@Override
	public void setUnit(Unit unit) {
		this.unit=unit;
	}

	@Override
	public Integer getOhm() {
		return ohm;
	}

	@Override
	public void messageReceived(String payload) {
		this.ohm=Integer.parseInt(payload.trim());
		this.updatedAt=Calendar.getInstance().toString();
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
