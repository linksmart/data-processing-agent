package it.ismb.pertlab.pwal.manager.serial.device;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.model.FlowMeterFit;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
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
	private String updatedAt;
	private String expiresAt;
	private PWALEventPublisher eventPublisher;
	
	public FlowMeterSensorFit(SerialManager manager)
	{
		this.manager = manager;
		this.eventPublisher = new PWALEventPublisher();
	}
	
	@Override
	public void messageReceived(String payload) {
		log.debug("Received message: "+payload);
		Gson gson = new Gson();
		FlowMeterSensorFit values = gson.fromJson(payload, FlowMeterSensorFit.class);
		this.flow = values.flow;
		log.debug("Payload json parsed: {}", values.toString());
		DateTime timestamp = new DateTime(DateTime.now(), DateTimeZone.UTC);
		this.setUpdatedAt(timestamp.toString());
		this.setExpiresAt(this.getUpdatedAt());
		HashMap<String, Object> valuesUpdated = new HashMap<>();
	        valuesUpdated.put("getDepth", this.getFlow());
	        PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.updatedAt, this.getPwalId(), this.expiresAt, valuesUpdated, this);
	        this.eventPublisher.publish(event);
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
		return this.updatedAt;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
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
    public String getExpiresAt()
    {
        return this.expiresAt;
    }

    @Override
    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }

}
