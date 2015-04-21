package eu.linksmart.gc.network.backbone.protocol.mqtt;


import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.types.utils.SerializationUtil;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

import eu.linksmart.gc.api.utils.Part;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.IOException;


@Component(name="BackboneMQTT", immediate=true)
@Service({Backbone.class})
public class MqttBackboneProtocolImpl implements Backbone, Observer {



	private MqttBackboneProtocolConfigurator conf = null;
	
	@Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy= ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;
	
	@Reference(name="BackboneRouter",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindBackboneRouter",
            unbind="unbindBackboneRouter",
            policy= ReferencePolicy.STATIC)
	private BackboneRouter bbRouter;

    // TODO maybe this will move to other service or to other bundle
    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNetworkManager",
            unbind="unbindNetworkManager",
            policy= ReferencePolicy.DYNAMIC)
    protected NetworkManager networkManager;

    protected void bindNetworkManager(NetworkManager networkManager) {
        LOG.debug("NetworkManagerRestPort::binding network-manager");
        this.networkManager = networkManager;
    }

    protected void unbindNetworkManager(NetworkManager networkManager) {
        LOG.debug("NetworkManagerRestPort::un-binding network-manager");
        this.networkManager = null;
    }


    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());

    // this object map Broker with VAD
    private Map<String, VirtualAddress>  endpointTopicVirtualAddress = new HashMap<String, VirtualAddress>();
    // this object map VAD with Broker
    private Map<VirtualAddress, String>  endpointVirtualAddressTopic = new HashMap<VirtualAddress, String>();
    // this objects map how many clients/vad are hearing the same topic
    private Map<String, Set<VirtualAddress>> listeningVirtualAddresses = new HashMap<>();
    // this maps the topic which the listener who is haring it.
    private Map<String,ForwardingListener> openClients = new HashMap<>();
    // this is the MQTT client to publish in the local broker
    private MqttClient mqttClient;
    // controls if one message had been sent already
    private Map<String,Boolean> MessageHashControl = new HashMap<>();
    // keep clean the MessageHashControl structure
    private Thread mapCleaner;
    // this is the unique identifier of this Backbone Protocol
    private final UUID MQTTProtocolID = UUID.randomUUID();

    private Map<Integer,Map<String, Boolean>> repetitionControl = null;
    private MessageDigest md5 = null;

    Registration brokerRegistrationInfo = null;

    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = null;
    }
    
    protected void bindBackboneRouter(BackboneRouter bbRouter) {
        this.bbRouter = bbRouter;
    }

    protected void unbindBackboneRouter(BackboneRouter bbRouter) {
        this.bbRouter = null;
    }
    @Activate
	protected void activate(ComponentContext context) throws Exception {
    	LOG.info("[activating Backbone MQTTProtocol]");
    	this.conf = new MqttBackboneProtocolConfigurator(this, context.getBundleContext(), mConfigAdmin);
		this.conf.registerConfiguration();

        startKeepCleanMessageControl();

        try {
            LOG.info("Starting broker main client with name:" +MQTTProtocolID.toString());
            mqttClient = new MqttClient(conf.get(conf.BROKER_URL),MQTTProtocolID.toString());

            mqttClient.connect();

        } catch (MqttException e) {
            LOG.error("Activating error:"+e.getMessage(),e);
            throw new Exception(e);
        }
        // TODO To be moved?
        registerBroker();

    }
    String getHostName(){
        String hostname = "localhost";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
    }
    // TODO To be moved?
    private void registerBroker() throws RemoteException {

        String hostName= getHostName(), domain = conf.get(conf.DOMAIN);

        Part[] attributes = {
                new Part("DESCRIPTION","Broker:"+hostName+"."+domain),
                new Part("UUID",MQTTProtocolID.toString())

        };

        brokerRegistrationInfo = networkManager.registerService(attributes,conf.get(conf.BROKER_URL), getName());
    }
    private void deRegisterBroker() throws RemoteException {

        networkManager.removeService(brokerRegistrationInfo.getVirtualAddress());
    }

    /**
     * TODO add description
     * */
    void startKeepCleanMessageControl(){

        if (Boolean.valueOf(conf.get(conf.MESSAGE_REPETITION_CONTROL))) {
            initMessageRepetitionControl();
        }

        mapCleaner = new Thread(){
            private Boolean alive = true;

            public void run(){
                while (alive){
                    synchronized (MessageHashControl) {
                        if (!MessageHashControl.isEmpty()) {
                            // for all messages already sent
                            for (String i : MessageHashControl.keySet()) {

                                // if I is the first time the thread knows about the message then mark to be erase the next cycle
                                if (MessageHashControl.get(i))

                                    MessageHashControl.put(i, false);
                                else // if was mark to be erased then erase it now!

                                    MessageHashControl.remove(i);
                            }
                        }
                    }
                    if (Boolean.valueOf(conf.get(conf.MESSAGE_REPETITION_CONTROL)))
                        synchronized (repetitionControl) {
                            if (!repetitionControl.isEmpty()) {
                                // for all messages already sent
                                for (Integer i : repetitionControl.keySet()) {
                                    Map<String, Boolean> ii = (Map<String,Boolean>)repetitionControl.get(i);

                                    for (String j : ii.keySet()) {
                                        // if I is the first time the thread knows about the message then mark to be erase the next cycle
                                        if (ii.get(j))

                                            ii.put(j, false);
                                        else // if was mark to be erased then erase it now!

                                            ii.remove(j);
                                    }

                                }
                            }
                        }
                    try {
                        this.sleep(Integer.valueOf(conf.get(conf.MESSAGE_CONTROL_CLEANER_TIMEOUT)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            public void setStop(){
                alive = false;
            }
        };
        mapCleaner.start();
    }
    void initMessageRepetitionControl(){
        try {
            md5 = MessageDigest.getInstance("MD5");
            repetitionControl = new HashMap<>();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(),e);
            conf.setConfiguration(conf.MESSAGE_REPETITION_CONTROL,"false");
        }
    }

    @Deactivate
	public void deactivate(ComponentContext context) {

        LOG.info("[de-activating Backbone MqttProtocol]");

        disconnectAll();

        try {
            mqttClient.disconnect();
            mqttClient.close();

            deRegisterBroker();
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
        }
	}
    /**
     * TODO add description
     * */
    private void disconnectAll(){
        for (ForwardingListener client : openClients.values() )
            try {
                client.disconnect();
            } catch (MqttException e) {
                LOG.info(e.getMessage());
            }
    }
    @Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
    	
		try {

			TunnelRequest tunnelRequest = recoverRequest(data,senderVirtualAddress);

			LOG.debug("method: " + tunnelRequest.getMethod());
			LOG.debug("path: " + tunnelRequest.getPath());
			LOG.debug("headers: " + tunnelRequest.getHeaders().length);
			LOG.debug("body: " + new String(tunnelRequest.getBody()));

			// check if service endpoint is available
			String uriEndpoint = tunnelRequest.getPath();

			if (uriEndpoint == null) {
				String message = "cannot send tunneled data to service at virtualAddress: " + receiverVirtualAddress.toString() + ", unknown endpoint";
				LOG.error(message);
				return createErrorMessage(404,message);
			}

			// determine HTTP method
			if(tunnelRequest.getMethod().equals(conf.get(conf.SUBSCRIBE_TO))) {
				subscribe(senderVirtualAddress, uriEndpoint);
			} else if(tunnelRequest.getMethod().equals(conf.get(conf.PUBLISH_TO))) {
				publish(getSyncMessage(uriEndpoint, tunnelRequest.getBody()));
			} else if(tunnelRequest.getMethod().equals(conf.get(conf.RESUBSCRIBE_TO))) {
				resubscribe(senderVirtualAddress, uriEndpoint);
			} else if(tunnelRequest.getMethod().equals(conf.get(conf.UNSUBSCRIBE_TO))) {
				unsubscribe( uriEndpoint);
			} else {
				throw new Exception("unsupported MQTT method for endpoint:" + uriEndpoint);
			}

			// creating & encoding LinkSmart Message Object



			return packResponse(createAcceptResponse(),senderVirtualAddress,receiverVirtualAddress);

		} catch (Exception e) {

            try {
                return  packResponse(createErrorMessage(500, e.getMessage()),senderVirtualAddress,receiverVirtualAddress);
            } catch (Exception e1) {

            }
            finally {
                LOG.error(e.getMessage(),e);
            }
        }
        return null;
	}
    NMResponse packResponse(NMResponse response, VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress) throws Exception {
        Message r_message = new Message("applicationData", senderVirtualAddress, receiverVirtualAddress, response.getMessageBytes());
        response.setMessageBytes(SerializationUtil.serializeMessage(r_message, true));

        return response;
    }
    /**
     * TODO add description
     * */
    private static NMResponse createErrorMessage(int errorCode, String message) {

        TunnelResponse tunnel_response = new TunnelResponse();
        tunnel_response.setStatusCode(errorCode);
        tunnel_response.setBody(message.getBytes());
        NMResponse nm_response = new NMResponse();
        nm_response.setStatus(NMResponse.STATUS_ERROR);
        nm_response.setBytesPrimary(true);
        try { nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response)); } catch (IOException e1) {	e1.printStackTrace(); }
        return nm_response;

    }
    /**
     * TODO add description
     * */
    private static NMResponse createAcceptResponse() throws IOException {


        TunnelResponse tunnel_response = new TunnelResponse();
        tunnel_response.setStatusCode(202);
        tunnel_response.setBody("202 Accepted".getBytes());
        NMResponse nm_response = new NMResponse();
        nm_response.setStatus(NMResponse.STATUS_SUCCESS);
        nm_response.setBytesPrimary(true);
        try { nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response)); } catch (IOException e1) {	e1.printStackTrace(); }

        return nm_response;

    }
    /**
     * TODO add description
     * */
    private  TunnelRequest recoverRequest(byte[] rawRequest, VirtualAddress senderVirtualAddress) throws Exception {
        TunnelRequest tunnelRequest = null;

            // decoding LinkSmart Message Object
            byte[] tunnel_data = SerializationUtil.deserializeMessage(rawRequest, senderVirtualAddress).getData();


            // deserialize tunnel request
            Object obj = SerializationUtil.deserialize(tunnel_data, TunnelRequest.class);


            tunnelRequest = (TunnelRequest)obj;


        return tunnelRequest;


    }

    /**
     * TODO add description
     * */
    private void subscribe( VirtualAddress senderVAD, String topic) throws Exception {

        // create container structure if is needed
        if(listeningVirtualAddresses.get(topic) == null  )
            listeningVirtualAddresses.put(topic, new HashSet<VirtualAddress>());

        // add a virtual address to the listeners of this topic
        listeningVirtualAddresses.get(topic).add(senderVAD);

        // if there is no listener in this topic add one
        if(!openClients.containsKey(topic))
            openClients.put(topic, new ForwardingListener(conf.get(conf.BROKER_URL), topic, MQTTProtocolID, this));


    }
    /**
     * TODO add description
     * */
    private MqttTunnelledMessage getAsyncMessage( byte[] data){
        MqttTunnelledMessage ms =null;
        try {
            ms = MqttTunnelledMessage.deserialize(data);
        }catch (Exception e){
               e.printStackTrace();

        }
        return ms;

    }
    /**
     * TODO add description
     * */
    private MqttTunnelledMessage getSyncMessage(String topic, byte[] data){
        MqttTunnelledMessage ms =null;

                ms = new MqttTunnelledMessage(
                         topic,
                        data,Integer.valueOf(conf.get(conf.QoS)),
                        Boolean.valueOf(conf.get(conf.PERSISTENCE)),
                        -1,
                        MQTTProtocolID
                );



        return ms;

    }
    private void publish(MqttTunnelledMessage ms) throws Exception {


        // check if I'm connected, if not try three times to connect.
        for (int i =0; i<3 && !mqttClient.isConnected(); i++)
            mqttClient.connect();

        // if now I'm not connected then I  report the error
        if (!mqttClient.isConnected()) {
            throw new Exception("Unable to establish connection with the broker");
        }



            // if the message is not repartition or local,and I successful recovered then is published.
            if(ms != null && uniqueMessagePerTopic(ms) && uniqueMessageControl(ms))
                mqttClient.publish(ms.getTopic(),ms.getPayload(),ms.getQoS(),ms.isRetained());




    }
    boolean uniqueMessagePerTopic(MqttTunnelledMessage ms){
        boolean send = true;

        if(!ms.isGenerated())
            // if the local loop is not allowed, check if this message was sent by this protocol, otherwise don't test if the message is local.
            if(!ms.getOriginProtocol().equals(MQTTProtocolID)  || (Boolean.valueOf(conf.get(conf.ALLOWED_LOCAL_MESSAGING_LOOP)))){
                // check if this message was already send
                synchronized (MessageHashControl){
                    // if was sent, mark to no send it again
                    if(MessageHashControl.containsKey(ms.getMessageHash()))
                        send = false;
                    else // if not send yet, add it in the sent messages
                        MessageHashControl.put(ms.getMessageHash(),true);
                }
            }else {
                LOG.info("A message was discarded because was generated by the same MQTT Protocol");
                send =false;
            }

        return send;
    }
    boolean uniqueMessageControl(MqttTunnelledMessage ms){
        boolean send = true;


            if (Boolean.valueOf(conf.get(conf.MESSAGE_REPETITION_CONTROL))){
                String hash = (new BigInteger(1,md5.digest(ms.getPayload()))).toString();
                synchronized (repetitionControl){

                    if(repetitionControl.containsKey(ms.getPayload().length)) {
                       Map<String,Boolean> selected =(Map<String,Boolean>)repetitionControl.get(ms.getPayload().length);

                        if (selected.containsKey(hash))
                            // if was sent, mark to no send it again
                            send = false;
                        else
                            selected.put(hash,true);


                    }else { // if not send yet, add it in the sent messages
                        repetitionControl.put(ms.getPayload().length, new HashMap<String, Boolean>());
                        repetitionControl.get(ms.getPayload().length).put(hash,true);
                    }
                }
            }

        return send;

    }
    private void resubscribe(VirtualAddress senderVAD, String uriEndpoint) throws Exception {

        unsubscribe( uriEndpoint);
        subscribe(senderVAD, uriEndpoint);

    }
    
    private void unsubscribe( String uriEndpoint) throws Exception {

        if(openClients.containsKey(uriEndpoint)){
            if (listeningVirtualAddresses.containsKey(uriEndpoint)) {
                listeningVirtualAddresses.remove(uriEndpoint);
                if( listeningVirtualAddresses.isEmpty()) {
                    openClients.get(uriEndpoint).disconnect();
                    openClients.remove(uriEndpoint);
                }
            }else {
                LOG.info("Unexpected situation there is topic listeners that are not in the listener list");
            }
        }

    }

    @Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
        try {

            publish(getAsyncMessage(data));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new NMResponse(NMResponse.STATUS_SUCCESS);

    }
	
    @Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] receivedData) {

        throw new UnsupportedOperationException("MQTT Protocol do not support receive-Data-Synchronize operation!");

	}
	
    @Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] receivedData) {

        byte[] data;
        try {
           data = SerializationUtil.serializeMessage(new Message("applicationData", senderVirtualAddress, receiverVirtualAddress, receivedData), true);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            return null;
        }

        return bbRouter.receiveDataAsynch(senderVirtualAddress,receiverVirtualAddress,data,this);


	}
    
    @Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
        LOG.info("Making broadcast in the MQTT Protocol");
        try {
            mqttClient.publish(
                    conf.get(conf.BROADCAST),
                    data,
                    Integer.valueOf(conf.get(conf.QoS)),
                    Boolean.valueOf(conf.get(conf.PERSISTENCE))

            );

        }catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return new NMResponse(NMResponse.STATUS_SUCCESS);

    }

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		String configuredSecurity = this.conf.get(MqttBackboneProtocolConfigurator.SECURITY_PARAMETERS);
		String[] securityTypes = configuredSecurity.split("\\|");
		SecurityProperty oneProperty;
		List<SecurityProperty> answer = new ArrayList<SecurityProperty>();
		for (String s : securityTypes) {
			try {
				oneProperty = SecurityProperty.valueOf(s);
				answer.add(oneProperty);
			} catch (Exception e) {
				LOG.error("Security property value from configuration is not recognized: " + s + ": " + e);
			}
		}
		return answer;
	}
	
	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
        if (!endpointVirtualAddressTopic.containsKey(virtualAddress)) {
            return null;
        }
        return endpointVirtualAddressTopic.get(virtualAddress).toString();
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
        if (this.endpointVirtualAddressTopic.containsKey(virtualAddress) || endpointTopicVirtualAddress.containsKey(endpoint)) {
        	LOG.info("virtual-address is already store for endpoint: " + endpoint);
            return false;
        }

        this.endpointVirtualAddressTopic.put(virtualAddress, endpoint);
        this.endpointTopicVirtualAddress.put(endpoint,virtualAddress);

        LOG.info("virtual-address is added for endpoint: " + endpoint);
        return true;

    }

	@Override
    public boolean removeEndpoint(VirtualAddress virtualAddress) {

        this.endpointTopicVirtualAddress.remove(endpointVirtualAddressTopic.get(virtualAddress));
        this.endpointVirtualAddressTopic.remove(virtualAddress);
        return true;
	}
	
	@Override
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {

        LOG.info("Mqtt Protocol do not keep track of remote services");
    }
    /**
     *
     * Reimplemented the getName function so the name of the broker for the BackboneRouter is the name of the Broker this Backbone Protocol is connect to.
     * @return name of the backone protocol this case the broker is connected to.
     *
     * */
    @Override
    public  String getName() {

		return MqttBackboneProtocolImpl.class.getName();
	}

	public Dictionary getConfiguration() {
		return this.conf.getConfiguration();
	}

    /**
     *
     * When a message is received by a listener is reported to the MQTT Backbone Protocol
     * Then the BBP unpack it and report received data per each VAD hearing this topic
     *
     * @param forwardingListener the listener which catch the MQTT message
     * @param mqttTunnelledMessage the catch message
     *
     *
     * */
    @Override
    public void update(Observable forwardingListener, Object mqttTunnelledMessage) {
        // recovering objects
        MqttTunnelledMessage data = (MqttTunnelledMessage)mqttTunnelledMessage;

        // get the VAD which this topic is subscribed
       // VirtualAddress senderVAD =endpointTopicVirtualAddress.get(conf.get(conf.BROKER_URL));

            // send to all VAD who wants to receive this message
            for (VirtualAddress vad : listeningVirtualAddresses.get(data.getTopic()))
                receiveDataAsynch(brokerRegistrationInfo.getVirtualAddress(), vad, data.toBytes());

    }
    /**
     * make the necessary steps needed to update the configuration of the broker
     * @param map the updated info
     *
     * */
    public void applyConfigurations(Hashtable map){
        if (Boolean.valueOf(conf.get(conf.MESSAGE_REPETITION_CONTROL)))
            initMessageRepetitionControl();


        if (map.containsKey(conf.BROKER_URL)) {

            LOG.error("The broker had being change but the BackboneRouter don't support the reloading operation of a Backbone. This will make just a partial change");

            for (ForwardingListener fl : openClients.values())
                try {
                    fl.setBrokerURL(map.get(conf.BROKER_URL).toString());
                    fl.restart();
                } catch (MqttException e) {
                    LOG.error("Error applying configuration: " + e.getMessage(), e);
                }
        }
    }
    /**
     * brief ENUM use to declare if a message is synchronous or asynchronous
     *
     * */
    private enum Type{
        Sync,
        Async,
    }

}
