package it.ismb.pertlab.pwal.estsi_m2m_manager_v2;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.Application;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.Applications;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.Container;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.Containers;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.ReferenceToNamedResource;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb.SclBase;
import it.ismb.pertlab.pwal.estsi_m2m_manager_v2.parser.EtsiM2MMessageParser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class EtsiM2MManager extends DevicesManager
{

	private String baseUrl;
	private CloseableHttpClient client;
	private EtsiM2MMessageParser messageParser;
//	private ObjectFactory objectFactory;
	private SclBase sclBase;
	private HashMap<String, ArrayList<String>> devicesMapping;
	
	public EtsiM2MManager(String baseUrl, HashMap<String, ArrayList<String>> devicesMapping)
	{
		this.baseUrl = baseUrl;
		this.client = HttpClients.createDefault();
		this.messageParser = new EtsiM2MMessageParser();
//		this.objectFactory = new ObjectFactory();
		this.sclBase = new SclBase();
		this.devicesMapping = devicesMapping;
	}
	
	public void run() {
		log.info("M2M manager started.");
//		log.info("Devices: {}",this.devicesMapping.keySet().size());
//		if(this.devicesMapping.keySet().size() == 0)
//			log.info("No devices mapping.");
//		for (String k : this.devicesMapping.keySet()) {
//			log.info("Key: {}",k);
//			for (String value : this.devicesMapping.get(k)) {
//				log.info("Value: {}",value);
//			}
//		}
		exploreM2MResourcesTree();
		while(!t.isInterrupted())
		{
			
		}
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.M2M;
	}
	
	private void exploreM2MResourcesTree()
	{
		try {
			log.info("Start building m2m resources tree");
			log.info("Getting SclBase at: {}", this.baseUrl);
			HttpGet sclBaseRequest = new HttpGet(this.baseUrl);
			//These headers should be dynamic
			sclBaseRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
			sclBaseRequest.setHeader("Content-Type", "application/xml");
			sclBaseRequest.setHeader("Accept", "application/xml");
			sclBaseRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
			
			CloseableHttpResponse resp = client.execute(sclBaseRequest);
			this.sclBase = this.messageParser.parseSclBase(resp.getEntity().getContent());
			this.messageParser.toXml(SclBase.class, this.sclBase);
			this.retrieveApplications(this.sclBase.getApplicationsReference());
		} catch (JAXBException | IllegalStateException | IOException e) {
			log.error("exploreM2MResourcesTree exception: {}", e);
		}
	}
	
	private void retrieveApplications(String applicationsUrl)
	{
		log.info("Getting Applications at: {}", applicationsUrl);
		//These headers should be dynamic
		HttpGet applicationsRequest=new HttpGet(applicationsUrl);
		applicationsRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		applicationsRequest.setHeader("Content-Type", "application/xml");
		applicationsRequest.setHeader("Accept", "application/xml");
		applicationsRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		
		try {
			CloseableHttpResponse resp=client.execute(applicationsRequest);
			Applications applications = this.messageParser.parseApplications(resp.getEntity().getContent());
			this.messageParser.toXml(Applications.class, applications);
			for (ReferenceToNamedResource rtnr : applications.getApplicationCollection().getNamedReference()) {
				log.info("Start discovering application: {}", rtnr.getId());
				this.retrieveApplication(rtnr.getValue());
			}
		} catch (IOException | IllegalStateException | JAXBException e ) {
			log.error("retrieveApplications: {}", e);
		} 
	}
	
	private void retrieveApplication(String applicationUrl)
	{
		log.info("Getting requested Application at: {}", applicationUrl);
		HttpGet applicationRequest=new HttpGet(applicationUrl);
		applicationRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		applicationRequest.setHeader("Content-Type", "application/xml");
		applicationRequest.setHeader("Accept", "application/xml");
		applicationRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		
		try {
			CloseableHttpResponse resp=client.execute(applicationRequest);
			Application application = this.messageParser.parseApplication(resp.getEntity().getContent());
			this.messageParser.toXml(Application.class, application);
			log.info("Getting related Containers");
			retriveContainers(application.getContainersReference());
		} catch (IOException | IllegalStateException | JAXBException e) {
			log.error("retrieveApplication: {}", e);
		}
	}

	private void retriveContainers(String containersUrl) {
		log.info("Getting requested Containers at: {}", containersUrl);
		HttpGet containersRequest=new HttpGet(containersUrl);
		containersRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		containersRequest.setHeader("Content-Type", "application/xml");
		containersRequest.setHeader("Accept", "application/xml");
		containersRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		
		try {
			CloseableHttpResponse resp=client.execute(containersRequest);
			Containers containers = this.messageParser.parseContainers(resp.getEntity().getContent());
			this.messageParser.toXml(Containers.class, containers);
			for (ReferenceToNamedResource rtnr : containers.getContainerCollection().getNamedReference()) {
				log.info("Start discovering devices beloging to {}", rtnr.getId());
				retrieveContainer(rtnr.getValue());
			}
		} catch (IOException | IllegalStateException | JAXBException | ClassNotFoundException e) {
			log.error("retriveContainers: {}", e);
		}
	
	}

	private void retrieveContainer(String containerUrl) throws ClassNotFoundException {
		log.info("Getting requested Container at: {}", containerUrl);
		HttpGet containerRequest=new HttpGet(containerUrl);
		containerRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		containerRequest.setHeader("Content-Type", "application/xml");
		containerRequest.setHeader("Accept", "application/xml");
		containerRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		
		try {
			CloseableHttpResponse resp=client.execute(containerRequest);
//			int c;
//			while((c = resp.getEntity().getContent().read()) != -1)
//				System.out.print((char)c);
				
			Container container = this.messageParser.parseContainer(resp.getEntity().getContent());
			this.messageParser.toXml(Container.class, container);
//			String[] containerTokens = container.getId().split("/");
			String containerName = container.getId(); //containerTokens[containerTokens.length - 1];
			log.info("Container name is: {}", containerName);
			String deviceId = this.generateId();
			for (String searchString : container.getSearchStrings().getSearchString()) {
				log.debug("SearchString: {}", searchString);
				try
				{
					Class<?> classDevice = Class.forName("it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom."+searchString);
					Constructor<?> constructor=classDevice.getConstructor(String.class, CloseableHttpClient.class);
					Device d = (Device) constructor.newInstance(container.getContentInstancesReference(), this.client);
					d.setId(deviceId);
					this.devicesDiscovered.put(deviceId, d);
					for(DeviceListener l:deviceListener)
					{
						log.info("New M2M device discovered. Generating event.");
						l.notifyDeviceAdded(d);
					}
				}
				catch(ClassNotFoundException ex)
				{
					log.warn("Ops....class {} not found.", searchString);
				}
			}
		} catch (IOException | IllegalStateException | JAXBException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("retrieveContainer:", e);
		}
	}
}