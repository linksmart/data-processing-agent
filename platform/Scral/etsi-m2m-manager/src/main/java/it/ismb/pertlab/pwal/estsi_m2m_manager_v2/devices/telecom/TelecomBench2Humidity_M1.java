package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.estsi_m2m_manager.devices.telecom.base.TelecomBaseDevice;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomSmartBench2M1Json;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomBench2Humidity_M1 extends TelecomBaseDevice implements
        HumiditySensor, DataUpdateSubscriber<ContentInstances>
{

    private String pwalId;
    private String id;
    private Location location;
    private Unit unit;
    private Double humidity;
    private String updatedAt;
    private String expiresAt;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomBench2Humidity_M1.class);

    public TelecomBench2Humidity_M1(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
        this.humidity = -1.0;
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
        { PWALTopicsUtility.createDeviceNewDataTopic(DeviceNetworkType.M2M,
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
        return DeviceType.HUMIDITY_SENSOR;
    }

    @Override
    public String getNetworkType()
    {
        return DeviceNetworkType.M2M;
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
    public String getExpiresAt()
    {
        return this.expiresAt;
    }

    @Override
    public void setExpiresAt(String expireAt)
    {
        this.expiresAt = expireAt;
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
    public Double getHumidity()
    {
        return this.humidity;
    }

    @Override
    public void handleUpdate(ContentInstances updatedData)
    {
        try
        {
            if (updatedData.getContentInstanceCollection() != null
                    && updatedData.getContentInstanceCollection()
                            .getContentInstance() != null
                    && updatedData.getContentInstanceCollection()
                            .getContentInstance().size() > 0)
            {
                for (ContentInstance ci : updatedData
                        .getContentInstanceCollection().getContentInstance())
                {
                    for (String searchString : ci.getSearchStrings()
                            .getSearchString())
                    {
                        if (searchString.equals("M1"))
                        {
                            TelecomSmartBench2M1Json m2json = PWALJsonMapper
                                    .json2obj(TelecomSmartBench2M1Json.class,
                                            ci.getContent().getTextContent());
                            this.updatedAt = DateTime.now(DateTimeZone.UTC)
                                    .toString();
                            if (new Double(Double.parseDouble(m2json
                                    .getHumidity())) != null)
                                this.humidity = Double.parseDouble(m2json
                                        .getHumidity());
                            HashMap<String, Object> valuesMap = new HashMap<>();
                            valuesMap.put("getHumidity", this.getHumidity());
                            PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                                    this.updatedAt, this.getPwalId(),
                                    this.getExpiresAt(), valuesMap, this);
                            log.debug(
                                    "Device {} is publishing a new data available event on topic: {}",
                                    this.getPwalId(),
                                    this.eventPublisher.getTopics());
                            this.eventPublisher.publish(event);
                            return;
                        }
                    }
                }
            }
        }
        catch (IOException | IllegalStateException e)
        {
            log.error("getHumidity: {}", e.getLocalizedMessage());
        }
    }

    @Override
    public String getNetworkLevelId()
    {
        return this.id;
    }
}
