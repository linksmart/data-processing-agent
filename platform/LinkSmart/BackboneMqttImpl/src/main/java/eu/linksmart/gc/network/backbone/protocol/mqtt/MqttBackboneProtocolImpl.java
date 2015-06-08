package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.utils.Configurator;
import eu.linksmart.gc.network.backbone.protocol.mqtt.conf.MqttBackboneProtocolConfigurator;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.security.communication.SecurityProperty;
import eu.linksmart.gc.api.types.Configurable;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.types.utils.SerializationUtil;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Component(name="BackboneMQTT", immediate=true)
@Service({Backbone.class})
public class MqttBackboneProtocolImpl implements Backbone, Observer, Configurable {



	private MqttBackboneProtocolConfigurator conf = null;
    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy= ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;
    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = null;
    }

	
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


    protected void unbindMQTTConfigurator(Configurator conf) {
        this.conf = null;
    }


    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());

    // this object map Broker with VAD
    //private Map<String, VirtualAddress>  endpointTopicVirtualAddress = new HashMap<String, VirtualAddress>();
    // this object map VAD with Broker
    private BidiMap endpointVirtualAddressTopic = new DualHashBidiMap();
    // this objects map how many clients/vad are hearing the same topic
    private Map<String, Set<VirtualAddress>> listeningVirtualAddresses = new HashMap<>();

    // this objects map how many clients/vad are hearing the same topic
    private Map<String, Set<VirtualAddress>> listeningWithWildcardVirtualAddresses = new HashMap<>();
    // this maps the topic which the listener who is haring it.
    private Map<String,ForwardingListener> openClients = new HashMap<>();

    // controls if one message had been sent already
    private Map<String,Boolean> MessageHashControl = new HashMap<>();
    // keep clean the MessageHashControl structure
    private Thread mapCleaner;
    // this is the unique identifier of this Backbone Protocol
    private final UUID MQTTProtocolID = UUID.randomUUID();

    private Map<Integer,Map<String, Boolean>> repetitionControl = null;
    private MessageDigest md5 = null;

    private BrokerConnectionService brokerService;

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

        startKeepCleanMessageControl();

        try {
            LOG.info("Starting broker main client with name:" +MQTTProtocolID.toString());

            brokerService = new BrokerConnectionService(conf.get(MqttBackboneProtocolConfigurator.BROKER_NAME),conf.get(MqttBackboneProtocolConfigurator.BROKER_PORT), MQTTProtocolID,networkManager,Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.BROKER_AS_SERVICE)));

            brokerService.connect();
        } catch (Exception e) {
            LOG.error("Activating error:"+e.getMessage(),e);

            throw new Exception(e);
        }



    }




    /**
     * TODO add description
     * */
    void startKeepCleanMessageControl(){

        if (Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.MESSAGE_REPETITION_CONTROL))) {
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
                    if (Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.MESSAGE_REPETITION_CONTROL)))
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
                        this.sleep(Integer.valueOf(conf.get(MqttBackboneProtocolConfigurator.MESSAGE_CONTROL_CLEANER_TIMEOUT)));
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
            conf.setConfiguration(MqttBackboneProtocolConfigurator.MESSAGE_REPETITION_CONTROL,"false");
        }
    }

    @Deactivate
	public void deactivate(ComponentContext context) {

        LOG.info("[de-activating Backbone MqttProtocol]");

        disconnectAll();

        try {

            brokerService.destroy();

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
                client.close();
            } catch (MqttException e) {
                LOG.error("While disconnecting listeners: " + e.getMessage(),e);
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
			String uriEndpoint = tunnelRequest.getPath().replace("*","#");

			if (uriEndpoint == null) {
				String message = "cannot send tunneled data to service at virtualAddress: " + receiverVirtualAddress.toString() + ", unknown endpoint";
				LOG.error(message);
				return createErrorMessage(404,message);
			}

			// determine HTTP method
			if(tunnelRequest.getMethod().equals(conf.get(MqttBackboneProtocolConfigurator.SUBSCRIBE_TO))) {
                if(Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.BROKER_AS_SERVICE)))
				    subscribe(senderVirtualAddress, uriEndpoint);
                else
                    subscribe(senderVirtualAddress,receiverVirtualAddress);
			} else if(tunnelRequest.getMethod().equals(conf.get(MqttBackboneProtocolConfigurator.PUBLISH_TO))) {
				publish(getSyncMessage(uriEndpoint, tunnelRequest.getBody()));
			} else if(tunnelRequest.getMethod().equals(conf.get(MqttBackboneProtocolConfigurator.RESUBSCRIBE_TO))) {
				resubscribe(senderVirtualAddress, uriEndpoint);
			} else if(tunnelRequest.getMethod().equals(conf.get(MqttBackboneProtocolConfigurator.UNSUBSCRIBE_TO))) {
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

        if (topic.contains("#") || topic.contains("+")) {
            addVadListener(listeningWithWildcardVirtualAddresses,senderVAD, topic);
        } else {
            addVadListener(listeningVirtualAddresses,senderVAD,topic);
        }

         subscribe(topic);

    }
    private void subscribe( VirtualAddress senderVAD,VirtualAddress receiverVAD) throws Exception {

        if(endpointVirtualAddressTopic.containsKey(receiverVAD)) {
            String topic = endpointVirtualAddressTopic.get(receiverVAD).toString();

            addVadListener(listeningVirtualAddresses,senderVAD,topic);

            subscribe(topic);

        }

    }
    private void subscribe( String topic ) throws Exception {
        // if there is no listener in this topic add one
        if (!openClients.containsKey(topic))
            openClients.put(topic, new ForwardingListener(brokerService.getBrokerName(), brokerService.getBrokerPort(), topic, MQTTProtocolID, this));

    }
    private static void addVadListener(Map<String, Set<VirtualAddress>> listeningVirtualAddresses,VirtualAddress vad, String topic){
        // create container structure if is needed
        if (listeningVirtualAddresses.get(topic) == null)
            listeningVirtualAddresses.put(topic, new HashSet<VirtualAddress>());

        if (!listeningVirtualAddresses.containsKey(vad))
            // add a virtual address to the listeners of this topic
            listeningVirtualAddresses.get(topic).add(vad);

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
                        data,Integer.valueOf(conf.get(MqttBackboneProtocolConfigurator.QoS)),
                        Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.PERSISTENCE)),
                        -1,
                        MQTTProtocolID
                );



        return ms;

    }
    private void publish(MqttTunnelledMessage ms) throws Exception {

            // if the message is not repartition or local,and I successful recovered then is published.
            if(ms != null && uniqueMessagePerTopic(ms) && uniqueMessageControl(ms))
                brokerService.publish(ms.getTopic(),ms.getPayload(),ms.getQoS(),ms.isRetained());


    }
    boolean uniqueMessagePerTopic(MqttTunnelledMessage ms){
        boolean send = true;

        if(!ms.isGenerated())
            // if the local loop is not allowed, check if this message was sent by this protocol, otherwise don't test if the message is local.
            if(!ms.getOriginProtocol().equals(MQTTProtocolID)  || (Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.ALLOWED_LOCAL_MESSAGING_LOOP)))){
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


            if (Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.MESSAGE_REPETITION_CONTROL))){
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
                    openClients.get(uriEndpoint).close();
                    openClients.remove(uriEndpoint);
                }
            }else if (listeningWithWildcardVirtualAddresses.containsKey(uriEndpoint)) {
                listeningWithWildcardVirtualAddresses.remove(uriEndpoint);
                if( listeningWithWildcardVirtualAddresses.isEmpty()) {
                    openClients.get(uriEndpoint).close();
                    openClients.remove(uriEndpoint);
                }
            } else {
                LOG.info("The unsubscription request cannot be handle because there is no sub with this topic");
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
            brokerService.publish(
                    conf.get(MqttBackboneProtocolConfigurator.BROADCAST),
                    data,
                    Integer.valueOf(conf.get(MqttBackboneProtocolConfigurator.QoS)),
                    Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.PERSISTENCE))

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
        if (this.endpointVirtualAddressTopic.containsKey(virtualAddress) || endpointVirtualAddressTopic.containsValue(endpoint)) {
        	LOG.info("virtual-address is already store for endpoint: " + endpoint);
            return false;
        }

        this.endpointVirtualAddressTopic.put(virtualAddress, endpoint);

        LOG.info("virtual-address is added for endpoint: " + endpoint);
        return true;

    }

	@Override
    public boolean removeEndpoint(VirtualAddress virtualAddress) {

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

        if(Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.BROKER_AS_SERVICE)))
            receiveDataBrokerBase(data);
        else
            receiveDataTopicBase(data);
    }
    private void receiveDataBrokerBase(MqttTunnelledMessage data){

        if(!listeningWithWildcardVirtualAddresses.isEmpty()) {
            boolean send = true;
            String[] obtainTopicTokens = data.getTopic().split("/");
            // send to all VAD who wants to receive this message
            for (String topic : listeningWithWildcardVirtualAddresses.keySet()) {
                String[] orgTopicTokens = topic.replace("#","").split("/");

                if (obtainTopicTokens.length >= orgTopicTokens.length){

                    for (int i= 0; i< orgTopicTokens.length;i++)
                        if (!obtainTopicTokens[i].equals(obtainTopicTokens[i])) {
                            send = false;
                            break;
                        }

                }

                if(send)
                    for (VirtualAddress vad : listeningWithWildcardVirtualAddresses.get(topic))
                        receiveDataAsynch(brokerService.getVirtualAddress(), vad, data.toBytes());

            }
        }
        for (VirtualAddress vad : listeningVirtualAddresses.get(data.getTopic()))
            receiveDataAsynch(brokerService.getVirtualAddress(), vad, data.toBytes());
    }
    private void receiveDataTopicBase(MqttTunnelledMessage data){
        for (VirtualAddress vad : listeningVirtualAddresses.get(data.getTopic()))
            receiveDataAsynch((VirtualAddress)endpointVirtualAddressTopic.getKey(data.getTopic()), vad, data.toBytes());
    }
    /**
     * make the necessary steps needed to update the configuration of the broker
     * @param map the updated info
     *
     * */
    public void applyConfigurations(Hashtable map){
        if (Boolean.valueOf(conf.get(MqttBackboneProtocolConfigurator.MESSAGE_REPETITION_CONTROL)))
            initMessageRepetitionControl();


        if (map.containsKey(MqttBackboneProtocolConfigurator.BROKER_NAME)|| map.containsKey(MqttBackboneProtocolConfigurator.BROKER_PORT)) {
            try {

                if (map.containsKey(MqttBackboneProtocolConfigurator.BROKER_NAME))
                    brokerService.setBrokerName(map.get(MqttBackboneProtocolConfigurator.BROKER_NAME).toString());
                if (map.containsKey(MqttBackboneProtocolConfigurator.BROKER_PORT))
                    brokerService.setBrokerPort(map.get(MqttBackboneProtocolConfigurator.BROKER_PORT).toString());
            }catch (Exception e){
                LOG.error("Error while updating broker configuration :"+e.getMessage(),e);
            }


            for (ForwardingListener fl : openClients.values())
                try {
                    fl.setBroker(brokerService.getBrokerName(),brokerService.getBrokerPort());

                } catch (Exception e) {
                    LOG.error("Error applying configuration: " + e.getMessage(), e);
                }
        }

        if(map.containsKey(MqttBackboneProtocolConfigurator.BROKER_AS_SERVICE))
            try {
                brokerService.setService(Boolean.valueOf(map.get(MqttBackboneProtocolConfigurator.BROKER_AS_SERVICE).toString()));
            }catch (Exception e) {
                LOG.error("Error applying configuration: " + e.getMessage(), e);
            }



        LOG.info("Configuration changes applied!");
    }

}
