package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.commands.impl.SetSpeed;
import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaterPumpFit implements WaterPump
{

    protected static final Logger log = LoggerFactory
            .getLogger(WaterPumpFit.class);
    private String id;
    private String pwalId;
    private SerialManager manager;
    private Unit unit;
    private Location location;
    private PWALEventPublisher eventPublisher;
    private Double speed;
    private String updatedAt;
    private String expiresAt;
    
    // support to "reflection-based" access to device commands
    // TODO: revise this because it is not convenient
    private HashMap<String, AbstractCommand> commands;

    public WaterPumpFit(SerialManager manager)
    {
        this.manager = manager;
        this.location = new Location();
        this.speed = 0.0;
        // Set location to Rome
        this.location.setLat(41.900600);
        this.location.setLon(12.509006);
        this.eventPublisher = new PWALEventPublisher();
        
        //initialize the commands map
        this.commands = new HashMap<String, AbstractCommand>();
        
        //create the setSpeed command stub
        SetSpeed setSpeedCommand = new SetSpeed(this);
        
        //store the command stub
        this.commands.put(setSpeedCommand.getCommandName(), setSpeedCommand);
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
        this.eventPublisher.setTopics(new String[]
                { PWALTopicsUtility.createNewDataFromDeviceTopic(this.getNetworkType(),
                        this.getPwalId()) });
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
        return DeviceType.WATER_PUMP;
    }

    @Override
    public String getNetworkType()
    {
        return this.manager.getNetworkType();
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
    public void setSpeed(Double value)
    {
        String message = "*p1=" + value.intValue() + "$x#\n";
        log.info("Sending {}", message);
        manager.sendCommand(this.id, message);
        this.speed = value;
        DateTime updated = new DateTime(DateTime.now(), DateTimeZone.UTC);
        this.updatedAt = updated.toString();
        this.expiresAt = updated.toString();
        HashMap<String, Object> valuesUpdated = new HashMap<>();
        valuesUpdated.put("getSpeed", this.speed);
        PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                this.getUpdatedAt(), this.getPwalId(), this.getExpiresAt(),
                valuesUpdated, this);
        this.eventPublisher.publish(event);
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

    @Override
    public Double getSpeed()
    {
        return this.speed;
    }

	@Override
	public HashMap<String, AbstractCommand> getSupportedCommand()
	{
		return this.commands;
	}

}
