package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.manager.serial.device.payload.FillLevelFitJson;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FillLevelSensorFit extends BaseSerialDevice implements FillLevel
{

    protected static final Logger log = LoggerFactory
            .getLogger(FillLevelSensorFit.class);
    private String id;
    private String pwalId;
    private SerialManager manager;
    private Double level = 0.0;
    private Integer depth = 0;
    private String updatedAt;
    private String expiresAt;
    private PWALEventPublisher eventPublisher;
    
    private Location location;

    public FillLevelSensorFit(SerialManager manager)
    {
        this.manager = manager;
        this.eventPublisher = new PWALEventPublisher();
        this.location = new Location();
        
        //Set location to Rome
        this.location.setLat(45.06478);
        this.location.setLon(7.658514);
    }

    @Override
    public void messageReceived(String payload)
    {
        log.debug("Received message: " + payload);
        FillLevelFitJson values;
        try
        {
            values = PWALJsonMapper.json2obj(FillLevelFitJson.class, payload);
            this.depth = Integer.parseInt(values.getDepth());
            this.level = Double.valueOf(values.getLevel().trim()); //devo moltiplicare per 10 per ottenere un valore sensato.
            DateTime updated = new DateTime(DateTime.now(), DateTimeZone.UTC);
            this.updatedAt = updated.toString();
            this.expiresAt = updated.plusSeconds(1).toString();
            HashMap<String, Object> valuesUpdated = new HashMap<>();
            valuesUpdated.put("getDepth", this.depth);
            valuesUpdated.put("getLevel", this.level);
            PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.updatedAt, this.getPwalId(), this.expiresAt, valuesUpdated, this);
            this.eventPublisher.publish(event);
            log.debug("Payload json parsed: {}", values.toString());
        }
        catch (IOException e)
        {
            log.error("FillLevel: {}", e);
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
        return DeviceType.FILL_LEVEL_SENSOR;
    }

    @Override
    public String getNetworkType()
    {
        return manager.getNetworkType();
    }

    @Override
    public Integer getDepth()
    {
        return this.depth;
    }

    @Override
    public Double getLevel()
    {
        return this.level;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUnit(Unit unit)
    {
        // TODO Auto-generated method stub

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
