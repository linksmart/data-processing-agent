package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.DewPointSensor;
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

public class TelecomBench1DewPoint_M2 extends TelecomBaseDevice implements
        DewPointSensor, DataUpdateSubscriber<ContentInstances>
{

    private String pwalId;
    private String id;
    private Location location;
    private Unit unit;
    private String updatedAt;
    private String expiresAt;
    private Double dewPoint;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomBench1DewPoint_M2.class);

    public TelecomBench1DewPoint_M2(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
        this.dewPoint = 0.0;
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
        return DeviceType.DEW_POINT_SENSOR;
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
    public Double getDewPointTemperature()
    {
        return this.dewPoint;
    }

    @Override
    public void handleUpdate(ContentInstances updatedData)
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
                        TelecomSmartBench1M2Json m2json;
                        try
                        {
                            m2json = PWALJsonMapper.json2obj(
                                    TelecomSmartBench1M2Json.class, ci
                                            .getContent().getTextContent());
                            Double dp = Double
                                    .parseDouble(m2json.getDewpoint());
                            if (dp != null)
                                this.dewPoint = dp;
                            HashMap<String, Object> valuesMap = new HashMap<>();
                            valuesMap.put("getDewPointTemperature",
                                    this.getDewPointTemperature());
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
                        catch (IOException e)
                        {
                            log.error("DewPoint: ", e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getNetworkLevelId()
    {
        return this.id;
    }
}
