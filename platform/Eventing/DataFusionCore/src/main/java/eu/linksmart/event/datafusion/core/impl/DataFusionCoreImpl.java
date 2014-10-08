package eu.linksmart.event.datafusion.core.impl;


import com.google.gson.Gson;
import eu.linksmart.api.event.datafusion.core.ComplexEventHandlerLogic;
import eu.linksmart.api.event.datafusion.core.DataFusionCore;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import eu.linksmart.network.Registration;
import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.network.networkmanager.NetworkManager;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.osgi.service.component.ComponentContext;

import java.rmi.RemoteException;


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

@Component(name="DataFusionCore", immediate = true)
@Service({DataFusionCore.class})
@org.apache.felix.scr.annotations.Properties({
        @Property(name="service.exported.interfaces", value="*"),
        @Property(name="service.exported.configs", value="org.apache.cxf.ws"),
        @Property(name="org.apache.cxf.ws.address", value="http://0.0.0.0:9090/cxf/services/DataFusionCore")
})
public class DataFusionCoreImpl  implements DataFusionCore, MqttCallback {
    private MqttClient client;
    Gson parser ;
	/// Address of the feeder
    @Reference(name="EventFeederLogic",
            cardinality = ReferenceCardinality.MANDATORY_MULTIPLE,
            bind="bindFeeder",
            unbind="unbindFeeder",
            policy= ReferencePolicy.DYNAMIC)
	private EventFeederLogic feeder;
	/// Address of the handler
    @Reference(name="ComplexEventHandlerLogic",
            cardinality = ReferenceCardinality.MANDATORY_MULTIPLE,
            bind="bindCEH",
            unbind="unbindCEH",
            policy= ReferencePolicy.DYNAMIC)
	private ComplexEventHandlerLogic handler;



	protected boolean init() {
        LOG.info("Start init"+this.getClass().getName());
        parser = new Gson();
        try {

            LOG.info("Start mqtt connection");

            client = new MqttClient("tcp://localhost:1883", "dfm2");
            if (!client.isConnected()) {

                client.connect();
                client.setCallback(this);
                client.subscribe("DFM");

                LOG.info("Connection Started");
            }
        } catch (MqttException e) {
            LOG.error(e.getStackTrace());
            return false;
        }

        LOG.info("Inited!");
        return true;
	}
	/**
	 * Take the needed steps for bind correctly with the feeder.<p>
	 * @see  EventFeederLogic
	 * 
	 * */
	public synchronized void bindFeeder(EventFeederLogic feeder) {
        //System.out.println("hola feeder");
        LOG.info("Binding with feeder...");
		this.feeder = feeder;
        LOG.info("feeder binded!");
		
	//	if(this.feeder!= null && this.handler!= null)
	//		addQuery("test","select value from EVENT.Transport3.EnergyMeasurement.win:time(1 sec)","EVENT/Transport3/EnergyMeasurement" );
	}
	/**
	 * Take the needed steps for unbind correctly with the feeder.<p>
	 * @see  EventFeederLogic
	 * 
	 * */
	public synchronized void unbindFeeder(EventFeederLogic feeder) {
		//TODO: cuando se va el core todo debe morir!
        LOG.info("Unbinded a feeder!");

	}
	/**
	 * Take the needed steps for bind correctly with the handler.<p>
	 * @see  ComplexEventHandlerLogic
	 * 
	 * */
	public synchronized void bindCEH(ComplexEventHandlerLogic handler) {

        LOG.info("Binding a handler...");
		this.handler = handler;
        LOG.info("Handler binded!");

	//	if(this.feeder!= null && this.handler!= null)
		//	addQuery("test","select value from EVENT.Transport3.EnergyMeasurement.win:time(1 sec)","EVENT/Transport3/EnergyMeasurement" );
		
	}
	/**
	 * Take the needed steps for unbind correctly with the handler.<p>
	 * @see  ComplexEventHandlerLogic
	 * 
	 * */
	public synchronized void unbindCEH(ComplexEventHandlerLogic handler) {

		//TODO: cuando se va el core todo debe morir!
        LOG.info("handler unbinded!");
	}
	/**
	 *  @see DataFusionCore
	 *  
	 * */
	@Override
	public boolean addQuery(String name, Object query, String[] topics) {

        for(String topic:topics)
		    this.feeder.suscribeToTopic(topic);
		
		this.handler.addHandler(name,(String)query,topics);
		
		// TODO Auto-generated method stub
		return false;
	}


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
        LOG.info("Starting activating ...");
		this.context = context;

		if (!init())
			throw new Exception("Error: The "+this.getClass().getCanonicalName()+" init was not able to execute successfully!");


       registerService();

       LOG.info("Activated!");
	}
    @Deactivate
	protected void deactivate(ComponentContext context) throws Exception {
		//TODO: deregistration in the NM.
        LOG.info("Starting deactivating ...");
		client.disconnect();
        LOG.info("Deactivated!");
	}

	private VirtualAddress createService(String endpoint, String serviceDescription) {
        LOG.info("Registering to the NM ...");
		try {
			Registration registration = networkManager.registerService(
					new eu.linksmart.utils.Part[] { new eu.linksmart.utils.Part(ServiceAttribute.DESCRIPTION.name(),
							serviceDescription) }, endpoint, backbone);
            LOG.info("Service registered");
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
        LOG.info("Starting registration ...");
		String subscriberURL = CXF_SERVICES_PATH + DataFusionCore.class.getSimpleName();
		// Save HID associated to service ID
		myVirtualAddress = createService(subscriberURL, DataFusionCore.class.getSimpleName());
		//subscriberHIDs.put(DataFusionCore.class.getName(), virtualAddress);


        LOG.info("Service registered!");
	}
	protected synchronized void bindNet(NetworkManager nm) {

        LOG.info("Binding a NetworkManager...");
        networkManager =nm;

        initBackbone();
        LOG.info("NetworkManager binded!");
		
		
	}
	protected synchronized void unbindNet(NetworkManager nm) {
        LOG.info("unbinding a NetworkManager...");
		try {
			networkManager.removeService(myVirtualAddress);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LOG.info("NetworkManager unbinded!");
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

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        LOG.info("Adding query ...");
        String msg =new String(mqttMessage.getPayload(), "UTF-8");
       msg =  msg.replace("\r","").replace("\n","").replace("\t","");

        DFQuery query =null;
        try {
            query = parser.fromJson(msg,DFQuery.class);
        }catch (Exception e){
            LOG.info(e.toString());
            LOG.info("Error in the query format");
        }

        if(query!=null) {
            this.addQuery(query.getName(), query.getQuery(), query.getTopics());
            LOG.info("Query added");
        }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
