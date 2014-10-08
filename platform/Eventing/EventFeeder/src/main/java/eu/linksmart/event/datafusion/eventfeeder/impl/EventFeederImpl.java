package eu.linksmart.event.datafusion.eventfeeder.impl;

import com.google.gson.Gson;
import eu.linksmart.api.event.EventSubscriber;
import eu.linksmart.api.event.EventSubscriptionWrapper;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Properties;

/**
 * Implementation of {@link EventFeederLogic}, {@link EventFeeder} and {@link EventSubscriber}. <p>

 * 
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see EventFeederLogic
 * @see EventFeeder
 * @see EventSubscriber
 * 
 * 
 * */


@Component(name="EventFeeder")
@Service({EventFeeder.class})
@org.apache.felix.scr.annotations.Properties({
        @Property(name="service.exported.interfaces", value="*"),
        @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
        @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/EventFeeder")
})
 public class   EventFeederImpl implements EventFeeder, EventFeederLogic, EventSubscriber {
	
	
	private ArrayList<DataFusionWrapper> dfwrappers =null;
	private ArrayList<String> subscribedTopics = null;
	private Map<String,ArrayList<DataFusionWrapper> > topicWrapper = null;
    private String broker;
    private Gson parser =null;
    private Stack<String> lastTopic = null;
    @Reference(name="EventSubscriptionWrapper",
            cardinality = ReferenceCardinality.MANDATORY_MULTIPLE,
            bind="bindWrapper",
            unbind="unbindWrapper",
            policy= ReferencePolicy.DYNAMIC)
    EventSubscriptionWrapper eventSubscriptionWrapper;
    @Reference(name="DataFusionWrapper",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindDFwWraper",
            unbind="unbindDFwWraper",
            policy= ReferencePolicy.DYNAMIC)
    DataFusionWrapper dataFusionWrapper;
//    @Reference(name="DataFusionCore",
//            cardinality = ReferenceCardinality.MANDATORY_UNARY,
//            policy= ReferencePolicy.DYNAMIC)
//    DataFusionCore dfc;
	protected boolean init(){

        if(dfwrappers==null)
            dfwrappers = new ArrayList<DataFusionWrapper>();

        if(subscribedTopics==null)
		    subscribedTopics = new ArrayList<String>();

        if(topicWrapper==null)
	    	topicWrapper = new  HashMap<String,ArrayList<DataFusionWrapper> >();

        if(lastTopic==null)
            lastTopic =new Stack<String>();
        parser = new Gson();


        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        try {
            prop.load(inputStream);
            if (inputStream == null) {
                throw new Exception("property file '" + propFileName + "' not found in the classpath");
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }



		return true;
	
	}
	/**
	 * Take the needed steps to bind correctly with the Event Broker, and also indicate to the broker to be ready for receive subscriptions.<p>
	 * @see  EventSubscriptionWrapper
	 * 
	 * */

 	protected synchronized void bindWrapper(EventSubscriptionWrapper wrapper) {
        System.out.println("HOLA:bindWrapper!");
        Properties prop = new Properties();
        String propFileName = "config.properties";
        broker = "tcp://localhost:1883";
		eventSubscriptionWrapper = wrapper;
        eventSubscriptionWrapper.findEventManager(SERVICE_ID, broker);
        eventSubscriptionWrapper.registerCallback(this, SERVICE_ID);
		//eventSubscriptionWrapper.subscribeWithTopic(EventFeederImpl.class.getSimpleName(), "EVENT/.*");
		
	}
	/**
	 * Take the needed steps to unbind correctly with the Event Broker, and also indicate to the broker to unsubscribe of everything.<p>
	 * @see  EventSubscriptionWrapper
	 * 
	 * */
	protected synchronized void unbindWrapper(EventSubscriptionWrapper wrapper) {
        System.out.println("HOLA!");
        eventSubscriptionWrapper.deregisterCallback(SERVICE_ID);
	}
    protected synchronized void bindDFwWraper(DataFusionWrapper dfw){
        this.dataFusionWrapperSignIn(dfw);
    }
    protected synchronized void unbindDFwWraper(DataFusionWrapper dfw){
        this.dataFusionWrapperSignIn(dfw);

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


        if(parts != null) {
            Hashtable<String, String> eventMap = new Hashtable<String, String>();
            Object[] event = new Object[parts.length];

            int k = 0;
            for (Part i : parts) {
                eventMap.put(i.getKey(), i.getValue());

                event[k] = i.getValue();

                k++;
            }

            String[] topicParts = topic.split("/");

            String acc = "";


            for (int i = 0; i < topicParts.length; i++) {
                acc += topicParts[i] + "/";


                if (topicWrapper.containsKey(acc + ".*")) {

                    for (DataFusionWrapper j : topicWrapper.get(acc + ".*"))
                        j.addEvent(topic, eventMap);
                }
                if (topicWrapper.containsKey(acc))
                    for (DataFusionWrapper j : topicWrapper.get(acc))
                        j.addEvent(topic, eventMap);


            }
        }
		
		return true;
		
	}
	
	/**
	 *  @see EventSubscriptionWrapper
	 *  
	 * */
	@Override
	public Boolean notifyXmlEvent(String topic, String arg0) throws RemoteException {

        if(arg0 != null || arg0 !="") {
            IoTEntityEventImpl even = null;
            arg0 = arg0.replace("\r","").replace("\n","");
            try {
                 even = parser.fromJson(arg0, IoTEntityEventImpl.class);

            }catch (Exception e){
                LOG.info("Error while parsing event: no IoT entity found!");
                return false;
            }


            String[] topicParts = topic.split("/");

            String acc = "";


            for (int i = 0; i < topicParts.length; i++) {
                acc += topicParts[i] ;


                if (topicWrapper.containsKey(acc + "/" + "*")||topicWrapper.containsKey(acc+ "*")) {

                    for (DataFusionWrapper j : topicWrapper.get(acc + ".*"))
                        j.addEvent(topic, even.getProperties()[0].toMap());
                }
                if (topicWrapper.containsKey(acc) ||topicWrapper.containsKey(acc+"/"))
                    for (DataFusionWrapper j : topicWrapper.get(acc))
                    try {

                        j.addEvent(topic, even.getProperties()[0].toMap());

                    }catch (Exception e){
                        LOG.info(e.getStackTrace());
                        return false;
                    }


                acc +=  "/";
            }



        }

        return true;
	}

	@Override
	public boolean suscribeToTopic(String topic) {
		
		subscribedTopics.add(topic);
        eventSubscriptionWrapper.subscribeWithTopic(SERVICE_ID,topic);
		
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

        System.out.println("HOLA:dataFusionWrapperSignIn!");

        if(dfwrappers==null)
            dfwrappers = new ArrayList<DataFusionWrapper>();


        if(subscribedTopics==null)
            subscribedTopics = new ArrayList<String>();

        if(topicWrapper==null)
            topicWrapper = new  HashMap<String,ArrayList<DataFusionWrapper> >();

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

        System.out.println("HOLA!");
		return dfwrappers.remove(dfw);
	}
	private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";

	protected Logger LOG = Logger.getLogger(EventFeeder.class.getName());
	protected ComponentContext context;
    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNet",
            unbind="unbindNet",
            policy= ReferencePolicy.DYNAMIC)
    protected NetworkManager networkManager;
	public final String SERVICE_ID = EventFeeder.class.getSimpleName();
	protected String backbone;
	protected VirtualAddress myVirtualAddress;

    @Activate
	protected void activate(ComponentContext context) throws Exception {
        System.out.println("HOLA: activate!");
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
		String subscriberURL = CXF_SERVICES_PATH + EventFeeder.class.getSimpleName();
		// Save HID associated to service ID
		myVirtualAddress = createService(subscriberURL, EventFeederLogic.class.getSimpleName());
		//subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);

		// Publish as Web Service
		Hashtable props = new Hashtable();
		props.put("service.exported.interfaces", "*");
		/*props.put("service.exported.configs", "org.apache.cxf.ws");
		props.put("org.apache.cxf.ws.address", subscriberURL);*/
		context.getBundleContext().registerService(	EventFeederLogic.class.getName(), this, props);


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
