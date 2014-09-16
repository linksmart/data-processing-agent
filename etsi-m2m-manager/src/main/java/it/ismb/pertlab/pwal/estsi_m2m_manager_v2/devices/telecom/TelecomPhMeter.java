package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.PhMeter;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.api.shared.PWALXmlMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom.base.TelecomBaseDevice;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomWaterJson;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomPhMeter extends TelecomBaseDevice implements PhMeter
{

    private String pwalId;
    private String id;
    private Double ph;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomPhMeter.class);

    public TelecomPhMeter(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
        this.contentInstancesUrl = contentInstanceUrl + "/latest";
    }

    @Override
    public Double getPh()
    {
        try
        {
            CloseableHttpResponse phResponse = PwalHttpClient.getInstance()
                    .executeRequest(contentInstancesRequest);
            ;
            if (phResponse.getEntity().getContent() != null)
            {
                ContentInstances cis = PWALXmlMapper.unmarshal(
                        ContentInstances.class, phResponse.getEntity()
                                .getContent());
                if (cis != null && cis.getContentInstanceCollection().getContentInstance().size() > 0)
                {
                    ContentInstance ci = cis.getContentInstanceCollection().getContentInstance().get(0);
                    TelecomWaterJson values = PWALJsonMapper.json2obj(
                            TelecomWaterJson.class, ci.getContent()
                                    .getTextContent());
                    this.ph = values.getPh();
                    log.debug("Json parsed: {}", values.toString());
                }
            }
        }
        catch (IOException | IllegalStateException | JAXBException e)
        {
            log.error("getPh: ", e);
        }
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUpdatedAt(String updatedAt)
    {
        // TODO Auto-generated method stub

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

}
