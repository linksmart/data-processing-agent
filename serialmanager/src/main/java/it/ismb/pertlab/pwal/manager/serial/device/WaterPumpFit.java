package it.ismb.pertlab.pwal.manager.serial.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

public class WaterPumpFit implements WaterPump {

	protected static final Logger log=LoggerFactory.getLogger(FlowMeterSensorFit.class);
	private String id;
	private String pwalId;
	private SerialManager manager;
	private Unit unit;
	
	public WaterPumpFit(SerialManager manager)
	{
		this.manager = manager;
		this.unit = new Unit();
		this.unit.setSymbol("m^3");
		this.unit.setValue("Cube meter");
	}
	
	@Override
	public String getPwalId() {
		return this.pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId = pwalId;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return DeviceType.WATER_PUMP;
	}

	@Override
	public String getNetworkType() {
		return this.manager.getNetworkType();
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
		return this.unit;
	}

	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@Override
	public void setVelocity(Double value) {
		String message = "*p1=" + value.intValue() + "$x#\n";
		log.info("Sending {}", message);
		manager.sendCommand(this.id, message);
	}

}
