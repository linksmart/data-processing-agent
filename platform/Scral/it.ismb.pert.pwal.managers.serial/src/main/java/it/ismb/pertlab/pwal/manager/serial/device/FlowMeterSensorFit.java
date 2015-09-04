package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.manager.serial.device.payload.FlowMeterFitJson;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowMeterSensorFit extends BaseSerialDevice implements FlowMeter
{
	
	protected static final Logger log = LoggerFactory.getLogger(FlowMeterSensorFit.class);
	private String id;
	private String pwalId;
	private SerialManager manager;
	
	// the water consumption!!! PAY ATTENTION HERE!!!
	private double flow;
	
	// the flow rate
	private double rawFlow;
	private Unit unit;
	private String updatedAt;
	private String expiresAt;
	private PWALEventPublisher eventPublisher;
	private Location location;
	
	public FlowMeterSensorFit(SerialManager manager)
	{
		this.manager = manager;
		this.eventPublisher = new PWALEventPublisher();
		this.flow = 0.0;
		this.rawFlow = 0.0;
		this.location = new Location();
		this.unit = new Unit();
		this.unit.setSymbol("m^3");
		this.unit.setValue("Cube meter");
		
		// Set location to Rome
		this.location.setLat(41.900848);
		this.location.setLon(12.509006);
	}
	
	@Override
    public void messageReceived(String payload)
    {
        log.debug("Received message: " + payload);
        FlowMeterFitJson values;
        try
        {
            values = PWALJsonMapper.json2obj(FlowMeterFitJson.class, payload);
            
            //perform simple integration...
            //sample given every one second in liter per hour
            //1dm^3/h = 10exp-3m^3/3600s 
            // WARNING!!! at some point the delta computed will be not significant with respect to the total number and no mor changes will be detected!!!
            this.flow += (this.rawFlow * 0.001)/(3600);
            this.rawFlow = Double.parseDouble(values.getFlow());
            
            log.debug("Payload json parsed: {}", values.toString());
            DateTime timestamp = new DateTime(DateTime.now(), DateTimeZone.UTC);
            this.setUpdatedAt(timestamp.toString());
            this.setExpiresAt(timestamp.plusSeconds(1).toString());
            HashMap<String, Object> valuesUpdated = new HashMap<>();
            valuesUpdated.put("getFlow", this.getFlow());
            PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                    this.updatedAt, this.getPwalId(), this.expiresAt,
                    valuesUpdated, this);
            this.eventPublisher.publish(event);
        }
        catch (IOException e)
        {
            log.error("FlowMeter: {}", e);
        }
    }
	
	@Override
	public String getPwalId()
	{
		return this.pwalId;
	}
	
	@Override
	public void setPwalId(String pwalId)
	{
		this.pwalId = pwalId;
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDataFromDeviceTopic(
				DeviceNetworkType.M2M, this.getPwalId()) });
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}
	
	@Override
	public void setId(String id)
	{
		this.id = id;
	}
	
	@Override
	public String getType()
	{
		return DeviceType.FLOW_METER_SENSOR;
	}
	
	@Override
	public String getNetworkType()
	{
		return manager.getNetworkType();
	}
	
	@Override
	public Double getFlow()
	{
		return this.flow;
	}
	
	@Override
	public String getUpdatedAt()
	{
		return this.updatedAt;
	}
	
	@Override
	public void setUpdatedAt(String updatedAt)
	{
		this.updatedAt = updatedAt;
	}
	
	@Override
	public Location getLocation()
	{
		return this.location;
	}
	
	@Override
	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	@Override
	public Unit getUnit()
	{
		return this.unit;
	}
	
	@Override
	public void setUnit(Unit unit)
	{
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
