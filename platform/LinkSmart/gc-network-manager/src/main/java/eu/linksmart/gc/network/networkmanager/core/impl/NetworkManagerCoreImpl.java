package eu.linksmart.gc.network.networkmanager.core.impl;

import eu.linksmart.gc.api.network.*;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.security.communication.CommunicationSecurityManager;
import eu.linksmart.gc.api.security.communication.SecurityProperty;
import eu.linksmart.gc.api.utils.Part;
import eu.linksmart.gc.network.connection.Connection;
import eu.linksmart.gc.network.connection.ConnectionManager;
import eu.linksmart.gc.network.connection.MessageSerializerUtiliy;
import eu.linksmart.gc.tools.GetNetworkManagerStatus;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Properties;

/*
 * Core implementation of NetworkManagerCore Interface
 */
@Component(name="NetworkManagerCore", immediate=true)
@Service({NetworkManagerCore.class})
public class NetworkManagerCoreImpl implements NetworkManagerCore, MessageDistributor {
	
	protected ConnectionManager connectionManager = new ConnectionManager(this);
	/** The VirtualAddress of this NetworkManager and IdentityManager **/
	protected VirtualAddress myVirtualAddress;
    protected VirtualAddress myVirtualAddressAsync;
	protected String myDescription;

	/* Constants */
	private static String NETWORK_MGR_CORE = NetworkManagerCoreImpl.class.getSimpleName();
	private static final String STARTED_MESSAGE = "Started " + NETWORK_MGR_CORE;
	private static final String STARTING_MESSAGE = "Starting " + NETWORK_MGR_CORE;
	private static final String COMMUNICATION_PARAMETERS_ERROR = "Could not establish common communication parameters with remote endpoint";
	protected static final String UNPROCESSED_MSG = "Received a message which has not been processed";
	public static String SUCCESSFUL_PROCESSING = "OK";
	public static String ERROR_PROCESSING = "ERROR";
	private static String NETWORK_MGR_ENDPOINT = "http://localhost:9090/cxf/services/NetworkManager";

	/**
	 * logger
	 */
	Logger LOG = Logger.getLogger(NetworkManagerCoreImpl.class.getName());

	/* fields */
	private NetworkManagerCoreConfigurator configurator;
	private Map<String, ArrayList<MessageProcessor>> msgObservers = new HashMap<String, ArrayList<MessageProcessor>>();
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindConfigAdmin", 
			unbind="unbindConfigAdmin",
			policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindHttpService", 
			unbind="unbindHttpService", 
			policy=ReferencePolicy.STATIC)
	protected HttpService http = null;
	
	@Reference(name="IdentityManager",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindIdentityManager", 
			unbind="unbindIdentityManager",
			policy=ReferencePolicy.DYNAMIC)
	protected IdentityManager identityManager = null;
	
	@Reference(name="CommunicationSecurityManager",
			cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
			bind="bindCommunicationSecurityManager", 
			unbind="unbindCommunicationSecurityManager", 
			policy=ReferencePolicy.DYNAMIC)
	protected CommunicationSecurityManager communicationSecurityManager = null;
	
	@Reference(name="BackboneRouter",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindBackboneRouter", 
			unbind="unbindBackboneRouter",
			policy=ReferencePolicy.DYNAMIC)
	protected BackboneRouter backboneRouter = null;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		LOG.debug("NetworkManagerCore::binding ConfigurationAdmin");
		this.configAdmin = configAdmin;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("NetworkManagerCore::un-binding ConfigurationAdmin");
    	this.configAdmin = null;
    }
    
    protected void bindHttpService(HttpService http) {
    	LOG.debug("NetworkManagerCore::binding http-service");
    	this.http = http;
    }
    
    protected void unbindHttpService(HttpService http) {
    	LOG.debug("NetworkManagerCore::un-binding http-service");
    	this.http = null;
    	//TODO unregister the existing running servlets
    }

	protected void bindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr) {
		LOG.debug("NetworkManagerCore::binding communicarion-sec-manager");
		this.communicationSecurityManager = commSecMgr;
		this.connectionManager.setCommunicationSecurityManager(communicationSecurityManager);
	}

	protected void unbindCommunicationSecurityManager(CommunicationSecurityManager commSecMgr) {
		LOG.debug("NetworkManagerCore::un-binding communicarion-sec-manager");
		this.connectionManager.removeCommunicationSecurityManager(communicationSecurityManager);
		this.communicationSecurityManager = null;
	}

	protected void bindIdentityManager(IdentityManager identityManager) {
		LOG.debug("NetworkManagerCore::binding identity-manager");
		this.identityManager = identityManager;
		this.connectionManager.setIdentityManager(identityManager);
	}

	protected void unbindIdentityManager(IdentityManager identityMgr) {
		LOG.debug("NetworkManagerCore::un-binding identity-manager");
		this.identityManager = null;
		this.connectionManager.setIdentityManager(null);
	}

	protected void bindBackboneRouter(BackboneRouter backboneRouter) {
		LOG.debug("NetworkManagerCore::binding backbone-router");
		this.backboneRouter = backboneRouter;
	}

	protected void unbindBackboneRouter(BackboneRouter backboneRouter) {
		LOG.debug("NetworkManagerCore::un-binding backbone-router");
		this.backboneRouter = null;
	}

	/**
	 * Component activation method.
	 * @param context
	 */
	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("activating " + STARTING_MESSAGE);
		init(context);
		LOG.info(STARTED_MESSAGE);
	}
	
	/**
	 * Deactivate method
	 * 
	 * @param context the bundle's execution context
	 */
	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info(NETWORK_MGR_CORE + "stopped");
		//TODO cleanup the environment
	}
	
	/**
	 * Initializes the component, i.e. creates own VirtualAddress, and registers the NM
	 * status servlets.
	 * 
	 * @param context
	 */
	private void init(ComponentContext context) {
		this.configurator = new NetworkManagerCoreConfigurator(this, context.getBundleContext(), this.configAdmin);
		this.configurator.registerConfiguration();
		this.myDescription = this.configurator.get(NetworkManagerCoreConfigurator.NM_DESCRIPTION);
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(),	this.myDescription) };

		// Create a local VirtualAddress with SOAP Backbone for NetworkManager
		// TODO Make the Backbone a constant or enum somewhere. find another way
		// to tell the BackboneRouter that my local network manager's VirtualAddress has
		// BackboneSOAPImpl.
		try {
			this.myVirtualAddress = registerService(attributes, NETWORK_MGR_ENDPOINT,
					"eu.linksmart.gc.network.backbone.protocol.wrapper.WrapperProtocolImpl")
					.getVirtualAddress();
			LOG.info("network-manager-core VirtualAddress: " + this.myVirtualAddress.toString());

		} catch (RemoteException e) {
			LOG.error(
					"PANIC - RemoteException thrown on local access of own method",
					e);
		} catch (Exception e) {
			LOG.error("Error creating registraiton for NetworkManager. This will cause serious problems!", e);
		}

		// Init Servlets
		// TODO implement servlet registration with HttpService in Declarative
		// Services style
		//HttpService http = (HttpService) context.locateService("HttpService");
		try {
			http.registerServlet("/GetNetworkManagerStatus", new GetNetworkManagerStatus(this, identityManager, backboneRouter), null, null);
			LOG.info("registring /GetNetworkManagerStatus into servlet container");
			http.registerResources("/files", "/resources", null);
		} catch (ServletException e) {
			LOG.error("Error registering servlets", e);
		} catch (NamespaceException e) {
			LOG.error("Error registering servlet namespace", e);
		} catch (Exception e) {
			LOG.error("Error registering servlets", e);
		}
	}

	/**
	 * @deprecated getVirtualAddress() should be used instead.
	 */
	@Override
	@Deprecated
	public VirtualAddress getService() {
		return this.myVirtualAddress;
	}

	public VirtualAddress getVirtualAddress() {
		return this.myVirtualAddress;
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint,
			String backboneName) throws RemoteException {

		// PID should be unique, if the PID is already used, throw exception
		for (Part attribute : attributes) {
			if (attribute.getKey().equalsIgnoreCase("PID")) {
				Registration serviceInfo = getServiceByPID(attribute.getValue());
				if (serviceInfo != null) {
					throw new IllegalArgumentException(
							"PID already in use. Please choose a different one.");
				}
			}
		}

		Registration newRegistration = this.identityManager.createServiceByAttributes(attributes);
		if(newRegistration != null) {
			List<SecurityProperty> properties = this.backboneRouter
					.getBackboneSecurityProperties(backboneName);
			if(properties != null) {
				// register VirtualAddress with backbone policies in connection manager
				this.connectionManager.registerServicePolicy(newRegistration.getVirtualAddress(), properties, true);
			}
			// add route to selected backbone
			if(this.backboneRouter.addRouteToBackbone(newRegistration.getVirtualAddress(), backboneName,
					endpoint)) {
				return newRegistration;
			} else {
				//adding route to backbone failed so cleanup what has been done
				this.identityManager.removeService(newRegistration.getVirtualAddress());
				this.connectionManager.removeServicePolicy(newRegistration.getVirtualAddress());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data,
			boolean synch) throws RemoteException {
		return this.sendMessage(new Message(Message.TOPIC_APPLICATION, sender,
				receiver, data), synch);
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) throws RemoteException {
		Boolean virtualAddressRemoved = this.identityManager.removeService(virtualAddress);
		this.connectionManager.deleteServicePolicy(virtualAddress);
		this.backboneRouter.removeRoute(virtualAddress, null);
		return virtualAddressRemoved;
	}

	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		Registration receiverRegistrationInfo = null;
		Registration senderRegistrationInfo = identityManager.getServiceInfo(senderVirtualAddress);
		if (receiverVirtualAddress != null) {
			receiverRegistrationInfo = identityManager.getServiceInfo(receiverVirtualAddress);
		}
		if (receiverVirtualAddress == null
				|| (receiverRegistrationInfo != null && identityManager.getLocalServices()
				.contains(receiverRegistrationInfo))
				|| (senderRegistrationInfo != null && identityManager.getLocalServices()
				.contains(senderRegistrationInfo))) {
			// get connection belonging to services
			Connection conn;
			try {
				if (receiverVirtualAddress == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderVirtualAddress);
				} else {
					// to get proper connection use my VirtualAddress
					conn = getConnection(myVirtualAddress, senderVirtualAddress, data);
					//no common connection parameters could be established with the other end
					if(conn == null) {
						NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
						if(this.connectionManager.isHandshakeMessage(
								data, senderVirtualAddress, receiverVirtualAddress)) {
							response = this.connectionManager.getDeclineHandshakeMessage(
									senderVirtualAddress, getService());
						} else {
							response = createErrorMessage(receiverVirtualAddress, senderVirtualAddress,
									COMMUNICATION_PARAMETERS_ERROR, ErrorMessage.ERROR, null); 
						}
						return response;
					}
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for services: "
								+ senderVirtualAddress.toString() + " " + myVirtualAddress.toString(),
								e);
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				String errorMsg = "Error getting connection for services: "
						+ senderVirtualAddress.toString() + " " + myVirtualAddress.toString();
				response = createErrorMessage( 
						receiverVirtualAddress,
						senderVirtualAddress, errorMsg, ErrorMessage.RECEPTION_ERROR, null);
				return response;
			}

			Message msg = conn.processData(senderVirtualAddress, receiverVirtualAddress, data);

			//drop error messages from further processing
			if(msg instanceof ErrorMessage) {
				NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
				if(msg.getData() != null) {
					response.setBytesPrimary(true);
					try {
						response.setMessageBytes(conn.processMessage(msg));
					} catch (Exception e) {
						response = createErrorMessage(
								receiverVirtualAddress, 
								senderVirtualAddress,
								new String(msg.getData()), msg.getTopic(), conn);
					}
				}
				return response;
			}
			String topic = msg.getTopic();
			// go through MsgObservers for additional processing
			List<MessageProcessor> observers = msgObservers.get(topic);
			if (observers != null) {
				for (MessageProcessor observer : observers) {
					msg = observer.processMessage(msg);
					if (msg == null || msg.getData() == null) {
						NMResponse nmresp = new NMResponse();
						nmresp.setStatus(NMResponse.STATUS_SUCCESS);
						return nmresp;
					}
				}
			}

			if (msg != null && msg.getData() != null
					&& msg.getData().length != 0) {
				/*
				 * check if message is not intended for host VirtualAddress, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverVirtualAddress() == null) {
					LOG.warn(UNPROCESSED_MSG);
					NMResponse response = createErrorMessage(
							getVirtualAddress(), senderVirtualAddress,
							UNPROCESSED_MSG,
							ErrorMessage.RECEPTION_ERROR, conn);
					return response;
				} else {
					// if this is not the response first forward it
					if (!msg.getReceiverVirtualAddress().equals(senderVirtualAddress)) {
						// forward over sendMessage method of this and return
						// response
						// here the response message should include a message
						// object
						LOG.trace("Forwarding received message to " + msg.getReceiverVirtualAddress());
						msg = sendMessageSynch(msg, msg.getSenderVirtualAddress(),
								msg.getReceiverVirtualAddress()).getMessageObject();
					}
					NMResponse nmresp = new NMResponse();
					if (msg != null && msg.getReceiverVirtualAddress().equals(senderVirtualAddress)) {
						// create response with connection and etc
						nmresp.setStatus(NMResponse.STATUS_SUCCESS);
						try {
							nmresp.setBytesPrimary(true);
							nmresp.setMessageBytes(conn.processMessage(msg));
						} catch (Exception e) {
							nmresp = createErrorMessage(
									receiverVirtualAddress, senderVirtualAddress,
									"Error receiving message: " + e.getMessage(),
									ErrorMessage.ERROR, conn);
						}
					} else {
						nmresp = createErrorMessage(
								receiverVirtualAddress, senderVirtualAddress,
								"Error processing message",
								ErrorMessage.ERROR, conn);
					}
					return nmresp;
				}

			} else {
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_SUCCESS);
				return response;
			}
		} else {
			return backboneRouter.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
		}
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] data) {
		// open message only if it is for local entity or is broadcast
		Registration receiverRegistrationInfo = null;
		Registration senderRegistrationInfo = identityManager.getServiceInfo(senderVirtualAddress);
		if (receiverVirtualAddress != null) {
			receiverRegistrationInfo = identityManager.getServiceInfo(receiverVirtualAddress);
		}
		if (receiverVirtualAddress == null
				|| (receiverRegistrationInfo != null && identityManager.getLocalServices()
				.contains(receiverRegistrationInfo))
				|| (senderRegistrationInfo != null && identityManager.getLocalServices()
				.contains(senderRegistrationInfo))) {
			// get connection belonging to services
			Connection conn;
			try {
				if (receiverVirtualAddress == null) {
					// broadcast message
					conn = connectionManager.getBroadcastConnection(senderVirtualAddress);
				} else {
					conn = getConnection(myVirtualAddress, senderVirtualAddress, data);
					//no common connection parameters could be established with the other end
					if(conn == null) {
						if(this.connectionManager.isHandshakeMessage(
								data, senderVirtualAddress, receiverVirtualAddress)) {
							return this.connectionManager.getDeclineHandshakeMessage(
									senderVirtualAddress, getService());
						} else {
							NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
							response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
							return response;
						}
					}
				}
			} catch (Exception e) {
				LOG.warn(
						"Error getting connection for services: "
								+ senderVirtualAddress.toString() + " "
								+ receiverVirtualAddress.toString(), e);
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_ERROR);
				response.setMessage("Error getting connection for services: "
						+ senderVirtualAddress.toString() + " " + receiverVirtualAddress.toString());
				return response;
			}

			Message msg = conn.processData(senderVirtualAddress, receiverVirtualAddress, data);
			String topic = msg.getTopic();
			// go through MsgObservers for additional processing
			List<MessageProcessor> observers = msgObservers.get(topic);
			if (observers != null) {
				for (MessageProcessor observer : observers) {
					msg = observer.processMessage(msg);
					if (msg == null || msg.getData() == null) {
						NMResponse nmresp = new NMResponse();
						nmresp.setStatus(NMResponse.STATUS_SUCCESS);
						return nmresp;
					}
				}
			}
			// if message is still existing it has to be forwarded
			if (msg != null && msg.getData() != null
					&& msg.getData().length != 0) {
				/*
				 * check if message is not intended for host VirtualAddress, if yes and it
				 * has not been processed drop it
				 */
				if (msg.getReceiverVirtualAddress() == null) {
					//TODO #NM Mark remove or fix
					// || msg.getReceiverHID().equals(this.myHID)) {
					LOG.warn("Received a message which has not been processed");
					NMResponse response = new NMResponse();
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage("Received a message which has not been processed");
					return response;
				}
				/*
				 * send message over sendMessage method of this and return
				 * response of it
				 */
				LOG.trace("Forwarding received message to " + msg.getReceiverVirtualAddress());
				return sendMessageAsynch(msg, msg.getSenderVirtualAddress(), msg.getReceiverVirtualAddress());
			} else {
				NMResponse response = new NMResponse();
				response.setStatus(NMResponse.STATUS_SUCCESS);
				return response;
			}
		} else {
			return backboneRouter.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
		}
	}

	/**
	 * Updates the description of this NetworkManager instance. This update
	 * request is also forwarded to the IdentityManager
	 * 
	 * @param description
	 */
	public void updateDescription(String description) {
		this.myDescription = description;

		Properties attributes = new Properties();
		attributes.setProperty(ServiceAttribute.DESCRIPTION.name(), description);

		this.identityManager.updateServiceInfo(this.myVirtualAddress, attributes);

	}

	@Override
	public void subscribe(String topic, MessageProcessor observer) {
		// check if topic already exists
		if (msgObservers.containsKey(topic)) {
			// add new observer to topic
			if (msgObservers.get(topic).contains(observer)) {
				// observer is already in list
				return;
			} else {
				msgObservers.get(topic).add(observer);
			}
		} else {
			// create topic and add observer
			msgObservers.put(topic, new ArrayList<MessageProcessor>());
			msgObservers.get(topic).add(observer);
		}

	}

	@Override
	public void unsubscribe(String topic, MessageProcessor observer) {
		if (msgObservers.containsKey(topic)) {
			msgObservers.get(topic).remove(observer);
		}
	}

	@Override
	public NMResponse broadcastMessage(Message message) {
		VirtualAddress senderVirtualAddress = message.getSenderVirtualAddress();
		byte[] data;
		try {
			data = this.connectionManager.getBroadcastConnection(senderVirtualAddress)
					.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress(), e);
			NMResponse response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
			return response;
		}
		NMResponse response = this.backboneRouter
				.broadcastData(senderVirtualAddress, data);

		return response;
	}

	@Override
	public NMResponse sendMessage(Message message, boolean synch) {
		VirtualAddress senderVirtualAddress = message.getSenderVirtualAddress();
		VirtualAddress receiverVirtualAddress = message.getReceiverVirtualAddress();
		LOG.debug("Request to send message to " + receiverVirtualAddress + "from " + senderVirtualAddress);
		if (synch)
			return sendMessageSynch(message, senderVirtualAddress, receiverVirtualAddress);
		else
			return sendMessageAsynch(message, senderVirtualAddress, receiverVirtualAddress);
	}

	/**
	 * Internal method for sending messages with more possible parameters. The
	 * message contains the original sender and the final receiver but the
	 * parameters determine which connection to use for sending.
	 * 
	 * @param message
	 *            Message to send
	 * @param senderVirtualAddress
	 *            Sender endpoint of connection to open
	 * @param receiverVirtualAddress
	 *            Receiver endpoint of connection to open
	 * @return
	 */
	private NMResponse sendMessageAsynch(Message message, VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		byte[] data = null;
		try {
			Connection connection = getConnection(
					receiverVirtualAddress, myVirtualAddress, message.getData());

			//no common connection parameters could be established with the other end
			if(connection == null) {
				if(this.connectionManager.isHandshakeMessage(
						message.getData(), senderVirtualAddress, receiverVirtualAddress)) {
					return this.connectionManager.getDeclineHandshakeMessage(
							this.getService(), receiverVirtualAddress);
				} else {
					NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
					response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
					return response;
				}	
			}
			data = connection.processMessage(message);
		} catch (Exception e) {
			LOG.warn("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
		/* TODO: I remove security (ANGEL)
			//NMResponse response = new NMResponse();

			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Could not create packet from message from VirtualAddress: "
					+ message.getSenderVirtualAddress());
			return response;*/
            NMResponse response = this.backboneRouter.sendDataAsynch(senderVirtualAddress,
                    receiverVirtualAddress, data);
		}
		NMResponse response = this.backboneRouter.sendDataAsynch(senderVirtualAddress,
				receiverVirtualAddress, data);

		return response;
	}

	/**
	 * Internal method for sending messages with more possible parameters. The
	 * message contains the original sender and the final receiver but the
	 * parameters determine which connection to use for sending.
	 * 
	 * @param message
	 *            Message to send
	 * @param senderVirtualAddress
	 *            Sender endpoint of connection to open
	 * @param receiverVirtualAddress
	 *            Receiver endpoint of connection to open
	 * @return
	 */
	private NMResponse sendMessageSynch(Message message, VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		byte[] data = null;
		NMResponse response = new NMResponse();
		Message tempMessage = message;

		try {
			Connection connection = getConnection(
					receiverVirtualAddress, myVirtualAddress, message.getData());

			//no common connection parameters could be established with the other end
			if(connection == null) {
				if(this.connectionManager.isHandshakeMessage(
						message.getData(), senderVirtualAddress, receiverVirtualAddress)) {
					return this.connectionManager.getDeclineHandshakeMessage(
							this.getService(), receiverVirtualAddress);
				} else {
				/* TODO I remove security with this (ANGEL)
					response.setStatus(NMResponse.STATUS_ERROR);
					response.setMessage(COMMUNICATION_PARAMETERS_ERROR);
					return response;*/
                    response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
                            receiverVirtualAddress, data);

                    if(response.getStatus() == NMResponse.STATUS_SUCCESS ) {
                        // process response where message contains logical endpoints and
                        // connection contains physical endpoints
                        //turn around sender and receiver of the message as this is a response
                        tempMessage = connection.processData(
                                message.getReceiverVirtualAddress(),
                                message.getSenderVirtualAddress(),
                                (response.getMessage() != null)? response.getMessageBytes() : new byte[0]);
                        // repeat sending and receiving until security protocol is over
                        while (tempMessage != null
                                && tempMessage
                                .getTopic()
                                .contentEquals(
                                        CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
                            response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
                                    receiverVirtualAddress, connection.processMessage(tempMessage));
                            if(response.getStatus() == NMResponse.STATUS_SUCCESS) {
                                //turn around sender and receiver of the message as this is a response
                                tempMessage = connection.processData(message.getReceiverVirtualAddress(),
                                        message.getSenderVirtualAddress(), response.getMessage()
                                                .getBytes());
                            } else {
                                return response;
                            }
                        }
                    } else {
                        return response;
                    }
				}
			}
			// process outgoing message
			data = connection.processMessage(tempMessage);
			response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
					receiverVirtualAddress, data);

			if(response.getStatus() == NMResponse.STATUS_SUCCESS ) {
				// process response where message contains logical endpoints and
				// connection contains physical endpoints
				//turn around sender and receiver of the message as this is a response
				tempMessage = connection.processData(
						message.getReceiverVirtualAddress(),
						message.getSenderVirtualAddress(),
						(response.getMessage() != null)? response.getMessageBytes() : new byte[0]);
				// repeat sending and receiving until security protocol is over
				while (tempMessage != null
						&& tempMessage
						.getTopic()
						.contentEquals(
								CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
					response = this.backboneRouter.sendDataSynch(senderVirtualAddress,
							receiverVirtualAddress, connection.processMessage(tempMessage));
					if(response.getStatus() == NMResponse.STATUS_SUCCESS) {
						//turn around sender and receiver of the message as this is a response
						tempMessage = connection.processData(message.getReceiverVirtualAddress(),
								message.getSenderVirtualAddress(), response.getMessage()
								.getBytes());
					} else {
						return response;
					}
				}
			} else {
				return response;
			}
		} catch (Exception e) {
			LOG.warn("Error while sending message from VirtualAddress "
					+ message.getSenderVirtualAddress() + "to VirtualAddress: " + message.getReceiverVirtualAddress());
			response = new NMResponse();
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("Error while sending message: " + e.getClass().getName() + ":" + e.getMessage());
			return response;
		}

		if (tempMessage.getClass().equals(ErrorMessage.class)) {
			response.setStatus(NMResponse.STATUS_ERROR);
		} else {
			response.setStatus(NMResponse.STATUS_SUCCESS);
		}
		response.setMessageObject(tempMessage);
		response.setBytesPrimary(true);
		response.setMessageBytes(tempMessage.getData());
		return response;
	}

	/**
	 * Sets the number of minutes before a connection is closed.
	 * 
	 * @param timeout the timeout to set, in minutes.
	 */
	protected void setConnectionTimeout(int timeout) {
		this.connectionManager.setConnectionTimeout(timeout);
	}

	@Override
	public String[] getAvailableBackbones() {
		List<String> backbones = this.backboneRouter.getAvailableBackbones();
		String[] backboneNames = new String[backbones.size()];
		return backbones.toArray(backboneNames);
	}

	@Override
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
		this.backboneRouter.addRouteForRemoteService(senderVirtualAddress, remoteVirtualAddress);
		//add the security properties of the backbone to the new virtual address
		String backbone = this.backboneRouter.getRouteBackbone(remoteVirtualAddress);
		if(backbone != null) {
			List<SecurityProperty> secProps = this.backboneRouter.getBackboneSecurityProperties(backbone);
			if(secProps != null) {
				connectionManager.registerServicePolicy(remoteVirtualAddress, secProps, false);
			}
		}
	}

	@Override
	public Registration[] getServiceByDescription(String description) {

		Part part_description = new Part(ServiceAttribute.DESCRIPTION.name(),
				description);

		return getServiceByAttributes(new Part[] { part_description });
	}

	@Override
	public Registration getServiceByPID(String PID) throws IllegalArgumentException {
		if (PID == null || PID.length() == 0) {
			throw new IllegalArgumentException("PID not specificed");
		}

		Part part_description = new Part(ServiceAttribute.PID.name(), PID);

		Registration[] registrations = getServiceByAttributes(new Part[] { part_description });

		if (registrations.length > 1) {
			throw new RuntimeException("More than one service registration found to passed PID");
		} else if (registrations.length == 1) {
			return registrations[0];
		} else
			return null;
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes) {
		return identityManager.getServiceByAttributes(
				attributes,
				IdentityManager.SERVICE_RESOLVE_TIMEOUT,
				false,
				false);
	}

	@Override
	public Registration[] getServiceByQuery(String query) {
		return identityManager.getServicesByAttributes(query);
	}

	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut,
			boolean returnFirst, boolean isStrictRequest) {
		return identityManager.getServiceByAttributes(
				attributes, timeOut, returnFirst, isStrictRequest);
	}

	@Override
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate,
			List<SecurityProperty> properties) {
		for(VirtualAddress virtualAddress : virtualAddressesToUpdate) {
			// register VirtualAddress with backbone policies in connection manager
			this.connectionManager.registerServicePolicy(virtualAddress, properties, true);
		}
	}

	private Connection getConnection(
			VirtualAddress receiverVirtualAddress,
			VirtualAddress senderVirtualAddress,
			byte[] data) {
		//only allow one thread at a time to get a connection
		//this is necessary to avoid the creation of multiple handshake connections
		connectionManager.getLock();
		Connection con = this.connectionManager.getConnection(receiverVirtualAddress, senderVirtualAddress);
		if(con == null) {
			LOG.debug("Creating connection for sender " + senderVirtualAddress + " and receiver " + receiverVirtualAddress);
			try {
				con = this.connectionManager.createConnection(receiverVirtualAddress, senderVirtualAddress, data);
			} catch (Exception e) {
				LOG.error(
						"Error getting connection for entities " 
								+ receiverVirtualAddress + " " + senderVirtualAddress,
								e);
			}
		}
		return con;
	}

	/**
	 * 
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @param errorMessage
	 * @param errorType
	 * @param conn
	 * @return
	 */
	private NMResponse createErrorMessage (
			VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress,
			String errorMessage, String errorType, Connection conn) {
		ErrorMessage error = new ErrorMessage(
				errorType, 
				senderVirtualAddress,
				receiverVirtualAddress,
				errorMessage.getBytes());
		NMResponse response = new NMResponse(NMResponse.STATUS_ERROR);
		response.setBytesPrimary(true);
		if(conn == null) {
			response.setMessageBytes(MessageSerializerUtiliy.serializeMessage(error, false, true));
		} else {
			try {
				response.setMessageBytes(conn.processMessage(error));
			} catch (Exception e) {
				response.setMessageBytes(MessageSerializerUtiliy.serializeMessage(error, false, true));
			}
		}

		return response;
	}
}
