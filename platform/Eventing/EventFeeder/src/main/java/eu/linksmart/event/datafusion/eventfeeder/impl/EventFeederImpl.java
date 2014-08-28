package eu.linksmart.event.datafusion.eventfeeder.impl;

import eu.linksmart.api.event.*;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.datafusion.core.*;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Implementation of {@link EventFeederLogic}, {@link EventFeeder} and {@link EventSubscriber}. <p>
 * This implementation is a {@link LinkSmartService}.
 * 
 * 
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see EventFeederLogic
 * @see EventFeeder
 * @see EventSubscriber
 * @see LinkSmartService
 * 
 * 
 * */
public class EventFeederImpl implements EventFeederLogic, EventFeeder, EventSubscriber {
	
	
	private ArrayList<DataFusionWrapper> dfwrappers;
	private ArrayList<String> subscribedTopics;
	private Map<String,ArrayList<DataFusionWrapper> > topicWrapper;
	private EventSubscriptionWrapper eventSubscriptionWrapper;
	/**
	 *  @see LinkSmartService
	 *  
	 * */
	protected boolean init(){
		
		dfwrappers = new ArrayList<DataFusionWrapper>();
		subscribedTopics = new ArrayList<String>();		
		topicWrapper = new  HashMap<String,ArrayList<DataFusionWrapper> >();
		
		return true;
	
	}
	/**
	 * Take the needed steps to bind correctly with the Event Broker, and also indicate to the broker to be ready for receive subscriptions.<p>
	 * @see  EventSubscriptionWrapper
	 * 
	 * */
	protected synchronized void bindWrapper(EventSubscriptionWrapper wrapper) {
		
		eventSubscriptionWrapper = wrapper;
		eventSubscriptionWrapper.findEventManager(SERVICE_ID, "EventManager:HM2014");
		eventSubscriptionWrapper.registerCallback(this, EventFeederImpl.class.getSimpleName());
		eventSubscriptionWrapper.subscribeWithTopic(EventFeederImpl.class.getSimpleName(), "EVENT/.*");
		
	}
	/**
	 * Take the needed steps to unbind correctly with the Event Broker, and also indicate to the broker to unsubscribe of everything.<p>
	 * @see  EventSubscriptionWrapper
	 * 
	 * */
	protected synchronized void unbindWrapper(EventSubscriptionWrapper wrapper) {
		eventSubscriptionWrapper.deregisterCallback(EventFeederImpl.class.getSimpleName());
	}
	/**
	 * 
	 * This is the moment when the feeder feed the signed CEP engine/s. 
	 * 
	 *  @see EventSubscriptionWrapper
	 *  
	 * */
	@Override
	public Boolean notify(String topic, Part[] parts) throws RemoteException {
		
		Hashtable<String,Object> eventMap = new Hashtable<String,Object>();
		Object[] event=  new Object[parts.length];
		
		int k=0;
		for (Part i : parts){
			eventMap.put(i.getKey(), i.getValue());
			
			event[k] = i.getValue();
			
			k++;
		}
		
		String[] topicParts=topic.split("/");
		
		String acc="";

		
			
		
		for (int i=0; i<topicParts.length;i++){
			acc+=topicParts[i] +"/";
				

			
			if (topicWrapper.containsKey(acc+".*")){
			
				for (DataFusionWrapper j : topicWrapper.get(acc+".*"))
					j.addEvent(topic, eventMap);
			}
			if (topicWrapper.containsKey(acc))
				for (DataFusionWrapper j : topicWrapper.get(acc))
					j.addEvent(topic, eventMap);
			
			
		}
		
		return true;
		
	}
	
	/**
	 *  @see EventSubscriptionWrapper
	 *  
	 * */
	@Override
	public Boolean notifyXmlEvent(String arg0) throws RemoteException {
		
		
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 *  @see LinkSmartService
	 *  
	 * */
	@Override
	public boolean suscribeToTopic(String topic) {
		
		subscribedTopics.add(topic);
		
		if (topicWrapper.containsKey(topic)){
			for(DataFusionWrapper dfw: dfwrappers)
				topicWrapper.get(topic).add(dfw);
			
		}else {
			 
			topicWrapper.put(topic, new ArrayList<DataFusionWrapper>());
			
			for(DataFusionWrapper dfw: dfwrappers)
				topicWrapper.get(topic).add(dfw);
		}
			
		// TODO Auto-generated method stub
		return true;
	}
	/**
	 *  @see EventFeeder
	 *  
	 * */
	@Override
	public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {
		

		dfwrappers.add(dfw);
		
		for (String topic: subscribedTopics){
			
			if (topicWrapper.containsKey(topic)){
				
				topicWrapper.get(topic).add(dfw);
				
			}else {
				 
				topicWrapper.put(topic, new ArrayList<DataFusionWrapper>());
				
				topicWrapper.get(topic).add(dfw);
			}
		}
		
		return true;
	}
	/**
	 *  @see EventFeeder
	 *  
	 * */
	@Override
	public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {
		
		
		return dfwrappers.remove(dfw);
	}
	private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";

	protected Logger LOG = Logger.getLogger(EventFeeder.class.getName());
	protected ComponentContext context;
	protected NetworkManager networkManager;
	public final String SERVICE_ID = EventFeeder.class.getSimpleName();
	protected String backbone;
	protected VirtualAddress myVirtualAddress;
	
	protected void activate(ComponentContext context) throws Exception {
		this.context = context;
		
		if (!init())
			throw new Exception("Error: The "+this.getClass().getCanonicalName()+" init was not able to execute successfully!");
		
		registerService();
	
	}
	
	protected void deactivate(ComponentContext context) throws Exception {
		//TODO: deregistration in the NM.
		
	}

	private VirtualAddress createService(String endpoint, String serviceDescription) {
		try {
			Registration registration = networkManager.registerService(
					new eu.linksmart.utils.Part[] { new eu.linksmart.utils.Part(ServiceAttribute.DESCRIPTION.name(),
							serviceDescription) }, endpoint, backbone);
			return registration.getVirtualAddress();
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e.getCause());
			return null;
		}
	}
	private void initBackbone() {
		try {
			String[] backbones = networkManager.getAvailableBackbones();
			this.backbone = null;
			for (String b : backbones) {
				if (b.contains("soap")) {
					this.backbone = b;
				}
			}
			if (backbone == null) {
				// Your web service will most likely only use "BackboneSOAPImpl"
				backbone = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
			}
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e.getCause());
		}
	}
	private void registerService(){
		String subscriberURL = CXF_SERVICES_PATH + EventFeeder.class.getSimpleName();
		// Save HID associated to service ID
		myVirtualAddress = createService(subscriberURL, EventFeeder.class.getSimpleName());
		//subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);

		// Publish as Web Service
		Hashtable props = new Hashtable();
		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "org.apache.cxf.ws");
		props.put("org.apache.cxf.ws.address", subscriberURL);
		context.getBundleContext().registerService(	EventFeeder.class.getName(), this, props);
	}
	protected synchronized void bindNet(NetworkManager nm) {
		networkManager =nm;
		
		initBackbone();
		
		
	}
	protected synchronized void unbindNet(NetworkManager nm) {
		try {
			networkManager.removeService(myVirtualAddress);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: cuando se va el core todo debe morir!
	}
	
	
}
