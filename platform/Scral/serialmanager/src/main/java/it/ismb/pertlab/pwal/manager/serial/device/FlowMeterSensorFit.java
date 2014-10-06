package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.FlowMeterFit;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class FlowMeterSensorFit extends BaseSerialDevice implements FlowMeterFit {

	protected static final Logger log=LoggerFactory.getLogger(FlowMeterSensorFit.class);
	private String id;
	private String pwalId;
	private SerialManager manager;
	private Integer flow;
	private Unit unit;
	
	public FlowMeterSensorFit(SerialManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void messageReceived(String payload) {
		log.debug("Received message: "+payload);
		Gson gson = new Gson();
		FlowMeterSensorFit values = gson.fromJson(payload, FlowMeterSensorFit.class);
		this.flow = values.flow;
		log.debug("Payload json parsed: {}", values.toString());
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
		return DeviceType.FLOW_METER_SENSOR;
	}

	@Override
	public String getNetworkType() {
		return manager.getNetworkType();
	}


//	@Override
//	public void setVelocity(Double value) {
//		String message = "*p1=" + value.intValue() + "$x#\n";
//		log.info("Sending {}", message);
//		manager.sendCommand(this.id, message);
//	}

	@Override
	public Integer getFlow() {
		return this.flow;
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

}
