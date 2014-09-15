//package it.ismb.pertlab.pwal.etsi_m2m_manager.utility;
//
//import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
//import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.EtsiM2MMessageParser;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Application;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Applications;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Container;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Containers;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ReferenceToNamedResource;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.SclBase;
//import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.events.M2MDeviceListener;
//
//import java.io.IOException;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.Dictionary;
//import java.util.List;
//
//import javax.xml.bind.JAXBException;
//
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class CopyOfM2MUtility
//{
//
//    private String baseUrl;
//    // private Dictionary<String, String> props; // TODO: find a better way to
//    // set
//    // the header content. It can be
//    // different every time
//    private EtsiM2MMessageParser messageParser;
//    private static Logger log = LoggerFactory.getLogger(CopyOfM2MUtility.class);
//    List<M2MDeviceListener> m2mDeviceListeners = new ArrayList<>();
//
//    public CopyOfM2MUtility(String baseUrl, Dictionary<String, String> props)
//    {
//        this.baseUrl = baseUrl;
//        this.messageParser = new EtsiM2MMessageParser();
//        // this.client = new
//        // this.props = props;
//    }
//
//    public void exploreM2MResourcesTree()
//    {
//        try
//        {
//            log.info("Start building m2m resources tree");
//            log.info("Getting SclBase at: {}", baseUrl);
//            HttpGet sclBaseRequest = new HttpGet(baseUrl);
//            // These headers should be dynamic
//            sclBaseRequest.setHeader("Authorization",
//                    "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
//            sclBaseRequest.setHeader("Content-Type", "application/xml");
//            sclBaseRequest.setHeader("Accept", "application/xml");
//            sclBaseRequest
//                    .setHeader("From",
//                            "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
//
//            CloseableHttpResponse resp = PwalHttpClient.getInstance()
//                    .executeRequest(sclBaseRequest);
//            SclBase sclBase = this.messageParser.parseSclBase(resp.getEntity()
//                    .getContent());
//            this.messageParser.toXml(SclBase.class, sclBase);
//            this.retrieveApplications(sclBase.getApplicationsReference());
//        }
//        catch (JAXBException | IllegalStateException | IOException e)
//        {
//            log.error("exploreM2MResourcesTree exception: {}", e);
//        }
//    }
//
//    private void retrieveApplications(String applicationsUrl)
//    {
//        log.info("Getting Applications at: {}", applicationsUrl);
//        // These headers should be dynamic
//        HttpGet applicationsRequest = new HttpGet(applicationsUrl);
//        applicationsRequest.setHeader("Authorization",
//                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
//        applicationsRequest.setHeader("Content-Type", "application/xml");
//        applicationsRequest.setHeader("Accept", "application/xml");
//        applicationsRequest.setHeader("From",
//                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
//
//        try
//        {
//            CloseableHttpResponse resp = PwalHttpClient.getInstance()
//                    .executeRequest(applicationsRequest);
//            Applications applications = this.messageParser
//                    .parseApplications(resp.getEntity().getContent());
//            this.messageParser.toXml(Applications.class, applications);
//            for (ReferenceToNamedResource rtnr : applications
//                    .getApplicationCollection().getNamedReference())
//            {
//                log.info("Start discovering application: {}", rtnr.getId());
//                this.retrieveApplication(rtnr.getValue());
//            }
//        }
//        catch (IOException | IllegalStateException | JAXBException e)
//        {
//            log.error("retrieveApplications: {}", e);
//        }
//    }
//
//    private void retrieveApplication(String applicationUrl)
//    {
//        log.info("Getting requested Application at: {}", applicationUrl);
//        HttpGet applicationRequest = new HttpGet(applicationUrl);
//        applicationRequest.setHeader("Authorization",
//                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
//        applicationRequest.setHeader("Content-Type", "application/xml");
//        applicationRequest.setHeader("Accept", "application/xml");
//        applicationRequest.setHeader("From",
//                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
//
//        try
//        {
//            CloseableHttpResponse resp = PwalHttpClient.getInstance()
//                    .executeRequest(applicationRequest);
//            Application application = this.messageParser.parseApplication(resp
//                    .getEntity().getContent());
//            this.messageParser.toXml(Application.class, application);
//            log.info("Getting related Containers");
//            retriveContainers(application.getContainersReference());
//        }
//        catch (IOException | IllegalStateException | JAXBException e)
//        {
//            log.error("retrieveApplication: {}", e);
//        }
//    }
//
//    private void retriveContainers(String containersUrl)
//    {
//        log.info("Getting requested Containers at: {}", containersUrl);
//        HttpGet containersRequest = new HttpGet(containersUrl);
//        containersRequest.setHeader("Authorization",
//                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
//        containersRequest.setHeader("Content-Type", "application/xml");
//        containersRequest.setHeader("Accept", "application/xml");
//        containersRequest.setHeader("From",
//                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
//
//        try
//        {
//            CloseableHttpResponse resp = PwalHttpClient.getInstance()
//                    .executeRequest(containersRequest);
//            Containers containers = this.messageParser.parseContainers(resp
//                    .getEntity().getContent());
//            this.messageParser.toXml(Containers.class, containers);
//            for (ReferenceToNamedResource rtnr : containers
//                    .getContainerCollection().getNamedReference())
//            {
//                log.info("Start discovering devices beloging to {}",
//                        rtnr.getId());
//                retrieveContainer(rtnr.getValue());
//            }
//        }
//        catch (IOException | IllegalStateException | JAXBException
//                | ClassNotFoundException e)
//        {
//            log.error("retriveContainers: {}", e);
//        }
//    }
//
//    private void retrieveContainer(String containerUrl)
//            throws ClassNotFoundException
//    {
//        log.info("Getting requested Container at: {}", containerUrl);
//        HttpGet containerRequest = new HttpGet(containerUrl);
//        containerRequest.setHeader("Authorization",
//                "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
//        containerRequest.setHeader("Content-Type", "application/xml");
//        containerRequest.setHeader("Accept", "application/xml");
//        containerRequest.setHeader("From",
//                "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
//
//        try
//        {
//            CloseableHttpResponse resp = PwalHttpClient.getInstance()
//                    .executeRequest(containerRequest);
//            // int c;
//            // while((c = resp.getEntity().getContent().read()) != -1)
//            // System.out.print((char)c);
//
//            Container container = this.messageParser.parseContainer(resp
//                    .getEntity().getContent());
//            this.messageParser.toXml(Container.class, container);
//            String containerName = container.getId();
//            log.info("Container name is: {}", containerName);
//            String[] containerNameTokens = containerName.split("/");
//            String deviceId = containerNameTokens[containerNameTokens.length - 1];
//            for (String searchString : container.getSearchStrings()
//                    .getSearchString())
//            {
//                log.debug("SearchString: {}", searchString);
//                try
//                {
//                    Class<?> classDevice = Class
//                            .forName("it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom."
//                                    + searchString);
//                    Constructor<?> constructor = classDevice
//                            .getConstructor(String.class);
//                    Device d = (Device) constructor.newInstance(container
//                            .getContentInstancesReference());
//                    d.setId(deviceId);
//                    for (M2MDeviceListener m : this.m2mDeviceListeners)
//                    {
//                        log.debug("NEW DEVICE FOUND. GENERATING EVENT.");
//                        m.notifyM2MDeviceAdded(d);
//                    }
//                }
//                catch (ClassNotFoundException ex)
//                {
//                    log.warn("Ops....class {} not found.", searchString);
//                }
//            }
//        }
//        catch (IOException | IllegalStateException | JAXBException
//                | NoSuchMethodException | SecurityException
//                | InstantiationException | IllegalAccessException
//                | IllegalArgumentException | InvocationTargetException e)
//        {
//            log.error("retrieveContainer:", e);
//        }
//    }
//
//    public void addM2MEventListener(M2MDeviceListener listener)
//    {
//        this.m2mDeviceListeners.add(listener);
//    }
//
//    public void removeM2MEventListener(M2MDeviceListener listener)
//    {
//        this.m2mDeviceListeners.remove(listener);
//    }
//}
