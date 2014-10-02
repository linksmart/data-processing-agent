package it.ismb.pertlab.pwal.etsi_m2m_manager.utility.polling;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.api.devices.polling.PWALPollingTask;
import it.ismb.pertlab.pwal.api.shared.PWALXmlMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.etsi_m2m_manager.EtsiM2MManager;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

public class EtsiM2MPollingTask extends PWALPollingTask<ContentInstances>
{
    // PWAL XML utility
    private PWALXmlMapper xmlMapper;
    // PWAL Http Client
    private PwalHttpClient httpClient;

    public EtsiM2MPollingTask(PollingDevicesManager<ContentInstances> manager,
            Logger log)
    {
        super(manager, log);
        this.xmlMapper = new PWALXmlMapper();
        this.httpClient = new PwalHttpClient();
    }

    @Override
    public void run()
    {
        log.info("Polling " + this.manager.getActiveSubscriptionsSize()
                + " subscription...");
        if (this.manager.getActiveSubscriptionsSize() > 0)
        {
            for (String key : ((EtsiM2MManager) this.manager)
                    .getContentInstancesList().keySet())
            {
                HttpGet contentInstancesRequest = new HttpGet(
                        ((EtsiM2MManager) this.manager)
                                .getContentInstancesList().get(key));
                contentInstancesRequest.setHeader("Authorization",
                        "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
                contentInstancesRequest.setHeader("Content-Type",
                        "application/xml");
                contentInstancesRequest.setHeader("Accept", "application/xml");
                contentInstancesRequest
                        .setHeader("From",
                                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
                try
                {
                    // this.httpClient = new PwalHttpClient();
                    log.debug("Retrieving {} content instances from {}", key,
                            contentInstancesRequest.getURI());
                    long time = System.currentTimeMillis();
                    CloseableHttpResponse contentInstanceResponse = this.httpClient
                            .executeRequest(contentInstancesRequest);
                    log.debug("Received response after {} millis.",
                            System.currentTimeMillis() - time);
                    InputStream is = contentInstanceResponse.getEntity()
                            .getContent();
                    if (is != null)
                    {
                        ContentInstances cis = this.xmlMapper.unmarshal(
                                ContentInstances.class, is);
                        Set<DataUpdateSubscription<ContentInstances>> subscriptionBucket = this.manager
                                .getSubscriptions(key);
                        if (subscriptionBucket != null)
                        {

                            for (DataUpdateSubscription<ContentInstances> dus : subscriptionBucket)
                            {
                                if (dus != null)
                                {
                                    long currentTime = System
                                            .currentTimeMillis();

                                    if (currentTime - dus.getTimestamp() >= (dus
                                            .getDeliveryTimeMillis()))
                                    {
                                        dus.setTimestamp(currentTime);
                                        if (cis != null
                                                && cis.getContentInstanceCollection()
                                                        .getContentInstance() != null
                                                && cis.getContentInstanceCollection()
                                                        .getContentInstance()
                                                        .size() > 0)
                                        {
                                            dus.getSubscriber().handleUpdate(
                                                    cis);
                                            DateTime updateAt = DateTime
                                                    .now(DateTimeZone.UTC);
                                            String expiresAt = updateAt
                                                    .plusMillis(
                                                            dus.getDeliveryTimeMillis())
                                                    .toString();
                                            ((Device) dus.getSubscriber())
                                                    .setUpdatedAt(updateAt
                                                            .toString());
                                            ((Device) dus.getSubscriber())
                                                    .setExpiresAt(expiresAt);
                                            log.info(
                                                    "Updating device: {} type: {}.",
                                                    ((Device) dus
                                                            .getSubscriber())
                                                            .getPwalId(),
                                                    ((Device) dus
                                                            .getSubscriber())
                                                            .getType());
                                        }
                                        else
                                            log.info(
                                                    "Recevied a null ContentInstances XML from: {}.",
                                                    contentInstancesRequest
                                                            .getURI());
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        log.info("Received a null input stream");
                    }
                    // Thread.sleep(15000);
                }
                catch (IOException | IllegalStateException | JAXBException e)// |
                                                                             // InterruptedException
                                                                             // e)
                {
                    log.error("EtsiM2MPolling task exception: ", e);
                }
            }
        }
    }
}
