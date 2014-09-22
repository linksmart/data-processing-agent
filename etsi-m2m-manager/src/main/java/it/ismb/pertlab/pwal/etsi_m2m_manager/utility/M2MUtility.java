package it.ismb.pertlab.pwal.etsi_m2m_manager.utility;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.shared.PWALXmlMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Container;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Discovery;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.events.M2MDeviceListener;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class M2MUtility extends TimerTask
{

    private String baseDiscoveryUrl;
    private DateTime createAfter;
    private DateTime createBefore;
    private List<String> currentDevicesList;
    private List<String> futureDevicesList;
    // private Dictionary<String, String> props; // TODO: find a better way to
    // set the header content. It can be different every time
    private static Logger log = LoggerFactory.getLogger(M2MUtility.class);
    //list of m2m devices notifications listeners
    private List<M2MDeviceListener> m2mDeviceListeners = new ArrayList<>();
    private HashMap<String, String> contentInstancesUrlMap;
    //PWAL XML utility
    private PWALXmlMapper xmlMapper;
    //PWAL Http client
    private PwalHttpClient httpClient;

    public M2MUtility(String baseUrl, Dictionary<String, String> props)
    {
        this.baseDiscoveryUrl = baseUrl
                + "/discovery?searchPrefix=application&inType=CT&createdAfter=%s&createdBefore=%s";
        this.createAfter = new DateTime("1970-01-01T00:00:00Z",
                DateTimeZone.UTC);
        this.createBefore = new DateTime(DateTimeZone.UTC);
        this.currentDevicesList = new ArrayList<>();
        this.futureDevicesList = new ArrayList<>();
        this.contentInstancesUrlMap = new HashMap<>();
        this.xmlMapper = new PWALXmlMapper();
        this.httpClient = new PwalHttpClient();
    }

    /**
     * This method requestes every time all the containers list since the
     * 1/1/1970 and check if there are removed or added containers
     */
    public void discoverM2MDevices()
    {
        log.info("Start building m2m resources tree");
        this.createBefore = new DateTime(DateTimeZone.UTC);
        ;
        String completeBaseDiscoveryUrl = String.format(this.baseDiscoveryUrl,
                this.createAfter.toString(), this.createBefore.toString());
        log.debug("baseDiscoveryUlr: {}", completeBaseDiscoveryUrl);
        log.info("Getting devices at: {}", completeBaseDiscoveryUrl);
        HttpGet sclBaseRequest = new HttpGet(completeBaseDiscoveryUrl);
        // These headers should be dynamic
        sclBaseRequest.setHeader("Authorization",
                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
        sclBaseRequest.setHeader("Content-Type", "application/xml");
        sclBaseRequest.setHeader("Accept", "application/xml");
        sclBaseRequest.setHeader("From",
                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");

        try
        {
            CloseableHttpResponse resp = this.httpClient
                    .executeRequest(sclBaseRequest);
            Discovery discoveryResult = this.xmlMapper.unmarshal(
                    Discovery.class, resp.getEntity().getContent());
//            PWALXmlMapper.toXml(Discovery.class, discoveryResult);
            if (discoveryResult.getDiscoveryURI() != null)
            {
                for (String devicesUri : discoveryResult.getDiscoveryURI()
                        .getReference())
                {
                    String[] tokens = devicesUri.split("/");
                    String containerName = tokens[tokens.length - 1];
                    if (!this.futureDevicesList.contains(containerName))
                        this.futureDevicesList.add(containerName);
                    
                    if (!this.currentDevicesList.contains(containerName))
                        this.retrieveContainer(devicesUri);
                    else
                        log.info("Device {} already exists.", containerName);
                }
            }
            else
                log.info("No news from the M2M platform");
            // Same epoch date because the first version will make an
            // intersection between the two devices lists
            // this.createAfter = new DateTime(DateTimeZone.UTC);
            if (this.currentDevicesList.size() != 0)
            {
                List<String> removedDevices = this.intersect(
                        this.futureDevicesList, this.currentDevicesList);
                log.debug(
                        "After intersection there are {} devices that have to be removed.",
                        removedDevices.size());
                for (String s : removedDevices)
                {
                    if(this.currentDevicesList.contains(s))
                    {
                        this.currentDevicesList.remove(s);
                        log.info("Removing device: {}", s);
                    }
                }
            }
//            this.currentDevicesList.addAll(this.futureDevicesList);
            this.futureDevicesList.clear();
        }
        catch (IOException | IllegalStateException | JAXBException e)
        {
            log.error("discoverM2MDevices exception: {}",
                    e.getLocalizedMessage());
        }
    }

    private void retrieveContainer(String containerUrl)
    {
        log.info("Getting Container at: {}", containerUrl);
        HttpGet containerRequest = new HttpGet(containerUrl);
        containerRequest.setHeader("Authorization",
                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
        containerRequest.setHeader("Content-Type", "application/xml");
        containerRequest.setHeader("Accept", "application/xml");
        containerRequest.setHeader("From",
                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");

        try
        {
            CloseableHttpResponse resp = this.httpClient
                    .executeRequest(containerRequest);
            // print received input stream
            // int c;
            // while((c = resp.getEntity().getContent().read()) != -1)
            // System.out.print((char)c);

            Container container = this.xmlMapper.unmarshal(Container.class, resp
                    .getEntity().getContent());
//            PWALXmlMapper.toXml(Container.class, container);
            String containerName = container.getId();
            log.info("Container name is: {}", containerName);
            String[] containerNameTokens = containerName.split("/");
            String deviceId = containerNameTokens[containerNameTokens.length - 1];
            for (String searchString : container.getSearchStrings()
                    .getSearchString())
            {
                // this.previousDevicesList.add(deviceId);
                log.debug("SearchString: {}", searchString);
                try
                {
                    Class<?> classDevice = Class
                            .forName("it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom."
                                    + searchString);
                    Constructor<?> constructor = classDevice
                            .getConstructor(String.class);
                    Device d = (Device) constructor.newInstance(container
                            .getContentInstancesReference());
                    d.setId(deviceId);
                    for (M2MDeviceListener m : this.m2mDeviceListeners)
                    {
                        log.debug("NEW DEVICE FOUND. GENERATING EVENT.");
                        this.currentDevicesList.add(deviceId);
                        this.contentInstancesUrlMap.put(deviceId, container.getContentInstancesReference());
                        m.notifyM2MDeviceAdded(d);
                    }
                }
                catch (ClassNotFoundException ex)
                {
                    log.warn("Ops....class {} not found.", searchString);
                }
            }
        }
        catch (IOException | IllegalStateException | JAXBException
                | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e)
        {
            log.error("retrieveContainer:", e);
        }
    }

    /**
     * Compare the two list received as paramenters and check which elements in
     * A are not present in B (A - B)
     * 
     * @param A a list of string
     * @param B a list of string
     * @return the difference between the two list
     */
    private List<String> intersect(List<String> A, List<String> B)
    {
        List<String> rtnList = new ArrayList<>();
        for (String dto : A)
        {
            if (!B.contains(dto))
            {
                rtnList.add(dto);
            }
        }
        return rtnList;
    }

    public void addM2MEventListener(M2MDeviceListener listener)
    {
        this.m2mDeviceListeners.add(listener);
    }

    public void removeM2MEventListener(M2MDeviceListener listener)
    {
        this.m2mDeviceListeners.remove(listener);
    }
    
    /**
     * Return an hash map with all the devices and their content instances
     * @return an hash map with the container name as key and a content instance url
     */
    public HashMap<String, String> getContentInstancesUrl()
    {
        return this.contentInstancesUrlMap;
    }
    
    @Override
    public void run()
    {
        this.discoverM2MDevices();
    }
}
