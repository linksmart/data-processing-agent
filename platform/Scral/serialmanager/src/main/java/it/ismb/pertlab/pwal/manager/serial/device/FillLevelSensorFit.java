package it.ismb.pertlab.pwal.manager.serial.device;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class FillLevelSensorFit extends BaseSerialDevice implements FillLevel
{

    protected static final Logger log = LoggerFactory
            .getLogger(FillLevelSensorFit.class);
    private String id;
    private String pwalId;
    private SerialManager manager;
    private Integer level = 0;
    private Integer depth = 0;
    private String updatedAt;
    private String expiresAt;
    private PWALEventPublisher eventPublisher;

    public FillLevelSensorFit(SerialManager manager)
    {
        this.manager = manager;
        this.eventPublisher = new PWALEventPublisher();
    }

    @Override
    public void messageReceived(String payload)
    {
        log.debug("Received message: " + payload);
        Gson gson = new Gson();
        FillLevelSensorFit values = gson.fromJson(payload,
                FillLevelSensorFit.class);
        this.depth = values.depth;
        this.level = values.level;
        DateTime updated = new DateTime(DateTime.now(), DateTimeZone.UTC);
        this.updatedAt = updated.toString();
        this.expiresAt = this.updatedAt;
        HashMap<String, Object> valuesUpdated = new HashMap<>();
        valuesUpdated.put("getDepth", this.getDepth());
        valuesUpdated.put("getLevel", this.getLevel());
        PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(this.updatedAt, this.getPwalId(), this.expiresAt, valuesUpdated, this);
        this.eventPublisher.publish(event);
        log.debug("Payload json parsed: {}", values.toString());
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
        { PWALTopicsUtility.createDeviceNewDataTopic(this.getNetworkType(),
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
    public Integer getLevel()
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLocation(Location location)
    {
        // TODO Auto-generated method stub

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
