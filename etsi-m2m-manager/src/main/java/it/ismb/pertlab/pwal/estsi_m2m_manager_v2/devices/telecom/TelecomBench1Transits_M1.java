package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.TransitsCounter;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.api.shared.PWALXmlMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom.base.TelecomBaseDevice;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomSmartBench1M1Json;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomBench1Transits_M1 extends TelecomBaseDevice implements
        TransitsCounter
{

    private String pwalId;
    private String id;
    private Location location;
    private Unit unit;
    private static final Logger log = LoggerFactory
            .getLogger(TelecomBench1Transits_M1.class);

    public TelecomBench1Transits_M1(String contentInstanceUrl)
    {
        super(contentInstanceUrl);
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
        return DeviceType.TRANSITS_COUNTER;
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
    public Integer getTransitCount()
    {
        try
        {
            CloseableHttpResponse transitResponse = PwalHttpClient
                    .getInstance().executeRequest(this.contentInstancesRequest);
            if (transitResponse.getEntity() != null
                    && transitResponse.getEntity().getContent() != null)
            {
                ContentInstances cis = PWALXmlMapper.unmarshal(
                        ContentInstances.class, transitResponse.getEntity()
                                .getContent());
                if (cis.getContentInstanceCollection() != null
                        && cis.getContentInstanceCollection() != null)
                {
                    for (ContentInstance ci : cis
                            .getContentInstanceCollection()
                            .getContentInstance())
                    {
                        for (String searchString : ci.getSearchStrings()
                                .getSearchString())
                        {
                            if (searchString.equals("M1"))
                            {
                                TelecomSmartBench1M1Json m1json = PWALJsonMapper
                                        .json2obj(
                                                TelecomSmartBench1M1Json.class,
                                                ci.getContent()
                                                        .getTextContent());
                                return Integer.parseInt(m1json.getTransits());
                            }
                        }
                    }
                }
            }
        }
        catch (IOException | IllegalStateException | JAXBException e)
        {
            log.error("getTransitCount: {}", e.getLocalizedMessage());
        }
        return -1;
    }
}
