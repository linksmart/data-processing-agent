package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.AirQualirySensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.estsi_m2m_manager.devices.telecom.base.TelecomBaseDevice;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomSmartBench1M2Json;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomBench1AirQuality_M2 extends TelecomBaseDevice implements
        AirQualirySensor, DataUpdateSubscriber<ContentInstances>
{

    private String pwalId;
    private String id;
    private String updatedAt;
    private String expiresAt;
    private Location location;
    private Unit unit;
    private Double co2level;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomBench1AirQuality_M2.class);

    public TelecomBench1AirQuality_M2(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
        this.co2level = -1.0;
        this.setLocation(this.getRadomLocation());
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
        { PWALTopicsUtility.createNewDataFromDeviceTopic(DeviceNetworkType.M2M,
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
        return DeviceType.AIR_QUALITY_SENSOR;
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
    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
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
    public Double getCO2Level()
    {
        return this.co2level;
    }

    @Override
    public String getNetworkLevelId()
    {
        return this.id;
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
                        if (searchString.equals("M2"))
                        {
                            TelecomSmartBench1M2Json m2json = PWALJsonMapper
                                    .json2obj(TelecomSmartBench1M2Json.class,
                                            ci.getContent().getTextContent());
                            Double co2 = Double.parseDouble(m2json
                                    .getAirquality());
                            HashMap<String, Object> valuesMap = new HashMap<>();
                            valuesMap.put("getCO2Level", this.getCO2Level());
                            PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                                    this.updatedAt, this.getPwalId(),
                                    this.getExpiresAt(), valuesMap, this);
                            log.debug(
                                    "Device {} is publishing a new data available event on topic: {}",
                                    this.getPwalId(),
                                    this.eventPublisher.getTopics());
                            this.eventPublisher.publish(event);
                            if (co2 != null)
                                this.co2level = co2;
                            return;
                        }
                    }
                }
            }
        }
        catch (IOException | IllegalStateException e)
        {
            log.error("getCO2Level: {}", e.getLocalizedMessage());
        }
    }
}