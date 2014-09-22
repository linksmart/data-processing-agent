package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.PhMeter;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.estsi_m2m_manager.devices.telecom.base.TelecomBaseDevice;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomWaterJson;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomPhMeter extends TelecomBaseDevice implements PhMeter, DataUpdateSubscriber<ContentInstances>
{

    private String pwalId;
    private String id;
    private Double ph;
    private String updatedAt;
    private String expiresAt;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomPhMeter.class);

    public TelecomPhMeter(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
    }

    @Override
    public Double getPh()
    {
        return this.ph;
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
        return DeviceType.PH_METER;
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
                ContentInstance ci = updatedData.getContentInstanceCollection().getContentInstance().get(0);
                TelecomWaterJson values = PWALJsonMapper.json2obj(
                        TelecomWaterJson.class, ci.getContent()
                                .getTextContent());
                this.ph = values.getPh();
                log.debug("Json parsed: {}", values.toString());
                return;
            }
        }
        catch (IOException | IllegalStateException e)
        {
            log.error("getPh: ", e);
        }
    }

    @Override
    public String getNetworkLevelId()
    {
       return this.id;
    }
}
