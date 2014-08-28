package eu.linksmart.event.datafusion.core.impl;


import eu.linksmart.api.event.datafusion.core.*;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.rmi.RemoteException;
import java.util.Hashtable;


/**
 * Implementation of {@link DataFusionCore}. <p>
 * This implementation is a  LinkSmartService.
 * 
 * 
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see DataFusionCore
 * 
 * 
 * */
public class DataFusionCoreImpl implements DataFusionCore {
	/// Address of the feeder
	private EventFeederLogic feeder;
	/// Address of the handler
	private ComplexEventHandlerLogic handler;


	protected boolean init() {
		// TODO Auto-generated method stub
		return true;
	}
	/**
	 * Take the needed steps for bind correctly with the feeder.<p>
	 * @see  EventFeederLogic
	 * 
	 * */
	public synchronized void bindFeeder(EventFeederLogic feeder) {
		this.feeder = feeder;
		
		if(this.feeder!= null && this.handler!= null)
			addQuery("test","select value from EVENT.Transport3.EnergyMeasurement.win:time(1 sec)","EVENT/Transport3/EnergyMeasurement" );
	}
	/**
	 * Take the needed steps for unbind correctly with the feeder.<p>
	 * @see  EventFeederLogic
	 * 
	 * */
	public synchronized void unbindFeeder(EventFeederLogic feeder) {
		//TODO: cuando se va el core todo debe morir!
	}
	/**
	 * Take the needed steps for bind correctly with the handler.<p>
	 * @see  ComplexEventHandlerLogic
	 * 
	 * */
	public synchronized void bindCEH(ComplexEventHandlerLogic handler) {
		this.handler = handler;
		if(this.feeder!= null && this.handler!= null)
			addQuery("test","select value from EVENT.Transport3.EnergyMeasurement.win:time(1 sec)","EVENT/Transport3/EnergyMeasurement" );
		
	}
	/**
	 * Take the needed steps for unbind correctly with the handler.<p>
	 * @see  ComplexEventHandlerLogic
	 * 
	 * */
	public synchronized void unbindCEH(ComplexEventHandlerLogic handler) {
		//TODO: cuando se va el core todo debe morir!
	}
	/**
	 *  @see DataFusionCore
	 *  
	 * */
	@Override
	public boolean addQuery(String name, Object query, String topic) {

		this.feeder.suscribeToTopic(topic);
		
		this.handler.addHandler(name,(String)query,topic);
		
		// TODO Auto-generated method stub
		return false;
	}

	
	private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";

	protected Logger LOG = Logger.getLogger(this.getClass().getName());
	protected ComponentContext context;
	protected NetworkManager networkManager;
	public final String SERVICE_ID = this.getClass().getSimpleName();
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
		String subscriberURL = CXF_SERVICES_PATH + DataFusionCore.class.getSimpleName();
		// Save HID associated to service ID
		myVirtualAddress = createService(subscriberURL, DataFusionCore.class.getSimpleName());
		//subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);

		// Publish as Web Service
		Hashtable props = new Hashtable();
		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "org.apache.cxf.ws");
		props.put("org.apache.cxf.ws.address", subscriberURL);
		context.getBundleContext().registerService(DataFusionCore.class.getName(), this, props);
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
	@Override
	public boolean pauseQuery(String name) {
		
		return false;
	}
	@Override
	public boolean removeQuery(String name) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean startQuery(String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
