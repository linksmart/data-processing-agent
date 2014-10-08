package eu.linksmart.event.datafusion.complexeventhandler.impl;

import eu.linksmart.api.event.EventPublicationWrapper;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.ResponseSet;
import eu.linksmart.api.event.datafusion.core.ComplexEventHandlerLogic;
import eu.linksmart.network.*;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


/**
 * Implementation of {@link ComplexEventHandler} and {@link ComplexEventHandlerLogic}. <p>
 * This implementation is a LinkSmart Service
 * 
 * 
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see ComplexEventHandler
 * @see ComplexEventHandlerLogic
 * 
 * 
 * */

@Component(name="ComplexEventHandler")
@Service
public class ComplexEventHandlerImpl implements ComplexEventHandler, ComplexEventHandlerLogic {


	/// Location of the publication wrapper EventBrokerManager
    @Reference(name="DataFusionWrapper",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindDFWraper",
            unbind="unbindDFWraper",
            policy= ReferencePolicy.DYNAMIC)
    DataFusionWrapper dummy;

    @Reference(name="EventPublicationWrapper",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindWrapper",
            unbind="unbindWrapper",
            policy= ReferencePolicy.DYNAMIC)
    private EventPublicationWrapper publicationWrapper;
	/// Subscribed wrappers in the handler 
	private ArrayList<DataFusionWrapper> dfWrappers = null;
	
	/// Buffer of queries. The buffer is for the DFWrappers who come after a query was added into this handler. 
	private Map<String,String> nameQuery= null;
    private Map<String,String[]> nameTopics= null;

	/// Buffer of responses. The buffer storage the responses of the CEP engine/s till the EventBroker is available. 
	private ArrayList<ResponseSet> queuedResponse= null;
			
	/**
	 *  
	 * */
	protected boolean init() {
		if(dfWrappers == null)
		    dfWrappers = new  ArrayList<DataFusionWrapper>();

        if(nameQuery == null)
            nameQuery = new  Hashtable<String, String>();

        if(nameTopics == null)
            nameTopics = new  Hashtable<String, String[]>();

        if(queuedResponse == null)
		    queuedResponse = new ArrayList<ResponseSet>();

		
		
		return true;
	}
	/**
	 * Take the needed steps for bind correctly with the Event Broker, and also indicate to the broker to be ready to send.<p>
	 * @see  EventPublicationWrapper
	 * 
	 * */
	protected synchronized void bindWrapper(EventPublicationWrapper wrapper) {
        System.out.println("HOLA: bindWrapper!");
		publicationWrapper = wrapper;
		publicationWrapper.findEventManager(SERVICE_ID, "tcp://localhost:1883");
		
		
	}
	/**
	 * Take the needed steps for unbind correctly with the Event Broker.
	 * @see  EventPublicationWrapper
	 * 
	 * */
	protected synchronized void unbindWrapper(EventPublicationWrapper wrapper) {
		//TODO: handle case when the Wrapper unbind
        System.out.println("HOLA!");

		
	}
    protected synchronized void bindDFWraper(DataFusionWrapper dfw){
        this.dataFusionWrapperSignIn(dfw);
    }
    protected synchronized void unbindDFWraper(DataFusionWrapper dfw){
        this.dataFusionWrapperSignIn(dfw);

    }
	/**
	 *  @see ComplexEventHandlerLogic
	 *  
	 * */
	@Override
	public boolean addHandler(String name, String query, String[] topics) {
		// TODO Auto-generated method stub
		for (DataFusionWrapper i: dfWrappers)
			i.addQuery(name, query,topics);

        if (dfWrappers.isEmpty()) {
            nameQuery.put(name, query);
            nameTopics.put(name, topics);
        }
		
		
		return true;
	}
	/**
	 *  @see ComplexEventHandler
	 *  
	 * */
	@Override
	public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {

        if(dfWrappers == null)
            dfWrappers = new  ArrayList<DataFusionWrapper>();

        if(nameQuery == null)
            nameQuery = new  Hashtable<String, String>();

        if(nameTopics == null)
            nameTopics = new  Hashtable<String, String[]>();

        if(queuedResponse == null)
            queuedResponse = new ArrayList<ResponseSet>();

        System.out.println("HOLA: dataFusionWrapperSignIn!");
		boolean ret = dfWrappers.add(dfw);
		
		for(String i: nameQuery.keySet()) {
            dfw.addQuery(i, nameQuery.get(i), nameTopics.get(i));
            nameQuery.remove(i);
            nameTopics.remove(i);
        }
		return ret;
	}
	/**
	 *  @see ComplexEventHandler
	 *  
	 * */
	@Override
	public boolean callerback(ResponseSet answer) {
	
		if(!publicationWrapper.isEventManagerLocated(SERVICE_ID)){
			queuedResponse.add(answer);
			synchronized(this){
				
				while(!publicationWrapper.isEventManagerLocated(SERVICE_ID)){
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		int n=0;
		for (ResponseSet.Response row : answer.getResponses()){
			Part[] parts = new Part[row.size()];
			int i=0;
			
			for(String key: row.getColumnsNames()){
				
				parts[i]= new Part(key,row.getValueOf(key).toString());
				
				i++;
			}
			
			try {
				if (publicationWrapper.publishEvent(SERVICE_ID, "CEP/"+answer.getName(), parts))
					n++;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return n==answer.size();
	}
	/**
	 *  @see ComplexEventHandler
	 *  
	 * */
	@Override
	public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {
        System.out.println("HOLA!");
		return dfWrappers.remove(dfw);
	}
	/// =======================================================================================================================
	private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";

	protected Logger LOG = Logger.getLogger(this.getClass().getName());
	protected ComponentContext context;
    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNet",
            unbind="unbindNet",
            policy= ReferencePolicy.DYNAMIC)
    protected NetworkManager networkManager;
	public final String SERVICE_ID = this.getClass().getSimpleName();
	protected String backbone;
	protected VirtualAddress myVirtualAddress;
    @Activate
	protected void activate(ComponentContext context) throws Exception {
        System.out.println("HOLA activate handler!");
		this.context = context;
		
		if (!init())
			throw new Exception("Error: The "+this.getClass().getCanonicalName()+" init was not able to execute successfully!");
		
		registerService();
	
	}
    @Deactivate
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
		String subscriberURL = CXF_SERVICES_PATH + ComplexEventHandler.class.getSimpleName();
		// Save HID associated to service ID
		myVirtualAddress = createService(subscriberURL, ComplexEventHandler.class.getSimpleName());
		//subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);

		// Publish as Web Service
		Hashtable props = new Hashtable();
		props.put("service.exported.interfaces", "*");
		props.put("service.exported.configs", "org.apache.cxf.ws");
		props.put("org.apache.cxf.ws.address", subscriberURL);
		context.getBundleContext().registerService(	ComplexEventHandler.class.getName(), this, props);
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
	/// =======================================================================================================================
	@Override
	public boolean pauseQuery(String name) {
		boolean ret = true;
		for (DataFusionWrapper i: dfWrappers)
			if(!i.puseQuery(name))
				ret = false;
		return ret;
	}
	@Override
	public boolean removeQuery(String name) {
		boolean ret = true;
		for (DataFusionWrapper i: dfWrappers)
			if(!i.removeQuery(name))
				ret = false;
		return ret;
	}
	@Override
	public boolean startQuery(String name) {
		boolean ret = true;
		for (DataFusionWrapper i: dfWrappers)
			if(!i.startQuery(name))
				ret = false;
		return ret;
	}
	
	
}
