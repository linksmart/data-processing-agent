package eu.linksmart.gc.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.security.communication.CommunicationSecurityManager;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

/**
 * Manages and creates connections between two services.
 * @author Vinkovits
 *
 */
public class ConnectionManager {
	/**
	 * Logger from log4j
	 */
	Logger logger = Logger.getLogger(ConnectionManager.class.getName());
	/**
	 * Stores the last usage of a connection.
	 */
	private HashMap<Connection, Date> timeouts = new HashMap<Connection, Date>();
	/**
	 * List of stored connections.
	 */
	protected List<Connection> connections = new ArrayList<Connection>();
	/**
	 * List of connections where the handshake failed.
	 */
	private List<Connection> bannedConnections = new ArrayList<Connection>();
	/**
	 * Number of minutes a connection can at least live after last use.
	 */
	private int timeoutMinutes = 30;
	/**
	 * Timer called with period of timeout/2 to clear connections.
	 */
	private Timer timer = null;
	/**
	 * Required properties stored by VirtualAddress. These {@link SecurityProperty} have to
	 * be provided by {@link Connection} between these services.
	 */
	protected HashMap<VirtualAddress, List<SecurityProperty>> servicePolicies = new HashMap<VirtualAddress, List<SecurityProperty>>();
	/**
	 * List of available managers to use for connection protection.
	 */
	private ArrayList<CommunicationSecurityManager> communicationSecurityManagers = 
			new ArrayList<CommunicationSecurityManager>();

	/**
	 * Connections which are not used but serve as placeholder to find proper connection.
	 */
	private List<Connection> aliases = new ArrayList<Connection>();

	/**
	 * Used to avoid access and change of policies
	 */
	private Object policyModificationLock = new Object();

	protected static final String HANDSHAKE_COMSECMGRS_KEY = "CommunicationSecurityManagers";
	protected static final String HANDSHAKE_SECPROPS_KEY = "SecurityProperties";
	protected static final String HANDSHAKE_DECLINE = "CommunicationDeclined";
	protected static final String HANDSHAKE_ACCEPT = "CommunicationAccepted";

	private NetworkManagerCore nmCore;
	private IdentityManager idM;
	private boolean busy;
	private Map<String, Connection> sessions = new HashMap<String, Connection>();


	public ConnectionManager(NetworkManagerCore nmCore){
		this.nmCore = nmCore;
		//Timer which periodically calls the clearer task
		timer = new Timer(true);
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}

	public void setIdentityManager(IdentityManager idM) {
		this.idM = idM;
	}

	public void setCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.add(comSecMgr);
	}

	public void removeCommunicationSecurityManager(CommunicationSecurityManager comSecMgr){
		this.communicationSecurityManagers.remove(comSecMgr);
	}

	/**
	 * Saves this VirtualAddress to always use a specific configuration.
	 * This includes for example type of security to use.
	 * @param regulatedVirtualAddress 
	 * @param forceChange if true and the policy existed before it is overwritten
	 */
	public void registerServicePolicy(
			VirtualAddress regulatedVirtualAddress, List<SecurityProperty> properties, boolean forceChange){
		synchronized(policyModificationLock){
			if (forceChange) {
				/*remove all connections which have this virtual address*/
				List<Connection> toRemove = new ArrayList<Connection>();
				//find the relevant connections
				for(Connection con : connections) {
					if(con.getServerVirtualAddress().equals(regulatedVirtualAddress) 
							|| (!(con instanceof BroadcastConnection) 
									&& con.getClientVirtualAddress().equals(regulatedVirtualAddress))) {
						toRemove.add(con);
					}
				}
				if(toRemove.size() != 0) {
					//remove collected connections
					for(Connection con : toRemove) {
						deleteConnection(con);
					}
				}
				//add new policy
				this.servicePolicies.put(regulatedVirtualAddress, properties);
			} else if (!servicePolicies.containsKey(regulatedVirtualAddress)) {
				//add new policy as there was none before
				this.servicePolicies.put(regulatedVirtualAddress, properties);
			} 
		}
	}

	/**
	 * Removes the policies registered for a given VirtualAddress.
	 * @param virtualAddress
	 */
	public void removeServicePolicy(VirtualAddress virtualAddress) {
		servicePolicies.remove(virtualAddress);
	}

	/**
	 * Removes the VirtualAddress's policy. After this call the VirtualAddress
	 * will always be handled with the default settings.
	 * @param regulatedVirtualAddress
	 */
	public void deleteServicePolicy(VirtualAddress regulatedVirtualAddress){
		this.servicePolicies.remove(regulatedVirtualAddress);
	}

	/**
	 * Returns {@link Connection} object associated to two entities.
	 * Returns null if there exists none yet.
	 * @param receiverVirtualAddress physical endpoint of connection
	 * @param senderVirtualAddress physical endpoint of connection
	 * @return Connection to use for processing of communication, null if no connection existing
	 */
	public synchronized Connection getConnection(VirtualAddress receiverVirtualAddress, VirtualAddress senderVirtualAddress){
		Calendar cal = Calendar.getInstance();
		//find connection belonging to these services
		for(Connection c : connections){
			if(!(c instanceof BroadcastConnection) &&
					((c.getClientVirtualAddress().equals(receiverVirtualAddress) && c.getServerVirtualAddress().equals(senderVirtualAddress))
							|| (c.getClientVirtualAddress().equals(senderVirtualAddress) && c.getServerVirtualAddress().equals(receiverVirtualAddress)))){
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				setNotBusy();
				return c;
			}
		}

		//if there was no connection look if there is an alias set up
		for(Connection c : aliases){
			if(!(c instanceof BroadcastConnection) &&
					((c.getClientVirtualAddress().equals(receiverVirtualAddress) && c.getServerVirtualAddress().equals(senderVirtualAddress))
							|| (c.getClientVirtualAddress().equals(senderVirtualAddress) && c.getServerVirtualAddress().equals(receiverVirtualAddress)))){
				//if we found one we take the the real connection based on the session id
				Connection con = sessions.get(c.sessionId);				
				//set last use date of connection
				timeouts.put(con, cal.getTime());
				setNotBusy();
				return con;
			}
		}

		if(receiverVirtualAddress.equals(senderVirtualAddress)){
			//add nopconnection to list as this is a reflection message
			Connection conn = new NOPConnection(senderVirtualAddress, receiverVirtualAddress);
			connections.add(conn);
			timeouts.put(conn, cal.getTime());
			setNotBusy();
			return conn;
		}
		return null;
	}

	/**
	 * Processes or starts a handshake with the other node deciding on the communication
	 * parameters to be used. Depending on the result of the handshake returns the created
	 * communication object.
	 * @param receiverVirtualAddress
	 * @param senderVirtualAddress
	 * @param data
	 * @return
	 * @throws Exception If there are policies for an VirtualAddress but no CommunicationSecurityManager
	 */
	public synchronized Connection createConnection(
			VirtualAddress receiverVirtualAddress,
			VirtualAddress senderVirtualAddress,
			byte[] data) throws Exception {
		//method variables
		Calendar cal = Calendar.getInstance();
		CommunicationSecurityManager agreedComSecMgr = null;
		String comSecMgrName = null;
		boolean match = false;

		//the remote side of this connection
		VirtualAddress remoteEndpoint = null;
		if(receiverVirtualAddress.equals(nmCore.getVirtualAddress()) || senderVirtualAddress.equals(nmCore.getVirtualAddress())) {
			//get virtual address of remote entity
			remoteEndpoint = nmCore.getVirtualAddress().equals(receiverVirtualAddress)? senderVirtualAddress : receiverVirtualAddress;
		}

		Registration receiverRegInfo = idM.getServiceInfo(remoteEndpoint);
		//check if the remote side is a local service and than do not start the handshake
		if(receiverRegInfo != null && idM.getLocalServices().contains(receiverRegInfo)) {
			setNotBusy();
			return createConnectionForLocalServices(receiverVirtualAddress, senderVirtualAddress);
		}

		//this connection is temporal until the handshake is able to resolve the proper connection
		HandshakeConnection tempCon = new HandshakeConnection(senderVirtualAddress, receiverVirtualAddress, this);
		if(bannedConnections.contains(tempCon)) {
			setNotBusy();
			return null;
		} else {
			connections.add(tempCon);
			//this ensures that if worse comes to worse the connection is removed
			timeouts.put(tempCon, Calendar.getInstance().getTime());
		}

		//it is possible to release the lock now as the handshake connection is created
		setNotBusy();

		//if the policies are not there yet  the networkmanager has not met this endpoint before
		if(!servicePolicies.containsKey(remoteEndpoint)) {	
			nmCore.addRemoteVirtualAddress(remoteEndpoint, remoteEndpoint);
			if(!servicePolicies.containsKey(remoteEndpoint)) {
				//we do not know anything about the remote endpoint so we close this attempt
				logger.warn("No information available of remote endpoint: " + remoteEndpoint.toString());
				handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
				return null;
			}
		}

		//if the message could not be opened or it was opened and it is not a handshake message we start the handshake
		boolean isHandshakeMsg = isHandshakeMessage(data, senderVirtualAddress, receiverVirtualAddress);
		String sessionId = null;
		if(isHandshakeMsg) {
			logger.trace("Received handshake message from: " + senderVirtualAddress);
			//open message in standard way
			Message message = 
					MessageSerializerUtiliy.
					unserializeMessage(data, true, senderVirtualAddress, receiverVirtualAddress, false);
			//we received a handshake message
			//open handshake body
			Properties properties = new Properties();
			try {
				properties.loadFromXML(new ByteArrayInputStream(message.getData()));
			} catch (InvalidPropertiesFormatException e) {
				logger.error(
						"Unable to load properties from XML data. Data is not valid XML: "
								+ new String(message.getData()), e);
				handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
				return null;
			} catch (IOException e) {
				logger.error("Unable to load properties from XML data: "
						+ new String(message.getData()), e);
				handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
				return null;
			}

			String[] availableComSecMgrs = properties.getProperty(HANDSHAKE_COMSECMGRS_KEY).split(";");
			String[] requiredSecProps = properties.getProperty(HANDSHAKE_SECPROPS_KEY).split(";");
			sessionId = properties.getProperty(Message.SESSION_ID_KEY);
			//if the session id is colliding we create another one - two collisions are improbable
			if(sessions.containsKey(sessionId)) {
				do {
					sessionId = UUID.randomUUID().toString();
				} while(sessions.containsKey(sessionId));
			}

			//find common agreement of provided CommunicationSecurityManagers and required policies
			comSecMgrName = findMatchingCommunicationSecurityManager(
					servicePolicies.get(remoteEndpoint),
					requiredSecProps,
					availableComSecMgrs);
		} else {
			//we only start the handshake if we are the initiator of the communication
			if(remoteEndpoint.equals(receiverVirtualAddress)) {
				logger.debug("Starting handshake with remote endpint: " + receiverVirtualAddress);
				do {
					sessionId = UUID.randomUUID().toString();
				} while(sessions.containsKey(sessionId));

				String[] handshakeResults = null;
				handshakeResults = startHandshakeOnCommunicationProperties(receiverVirtualAddress, senderVirtualAddress, sessionId);
				if(handshakeResults != null) {
					comSecMgrName = handshakeResults[0];
					if(handshakeResults.length > 1) {
						//the other end wished to change the session id as it already has a connection
						sessionId = handshakeResults[1];
						logger.trace("Remote endpoint changed session id to: " + sessionId);
						//if we have a connection with this session id we use it also for this entities
						if (sessions.containsKey(sessionId)) {
							Connection dummy = new Connection(receiverVirtualAddress, senderVirtualAddress);
							dummy.sessionId = sessionId;
							aliases.add(dummy);
							//use connection as stored for this session id
							Connection conn = sessions.get(sessionId);
							timeouts.put(conn, cal.getTime());
							//remove temporary connection as proper connection has been added now
							connections.remove(tempCon);
							timeouts.remove(tempCon);
							tempCon.setStateResolved();
							return conn;
						}
					}
				}
			} else {
				//open message in standard way
				Message message = 
						MessageSerializerUtiliy.
						unserializeMessage(data, true, senderVirtualAddress, receiverVirtualAddress, true);
				if(message == null || message instanceof ErrorMessage) {
					return null;
				}
				//check whether the message already contains a valid session id
				if (message.getKeySet().contains(Message.SESSION_ID_KEY)
						&& sessions.containsKey(message.getProperty(Message.SESSION_ID_KEY))) {
					logger.debug("Adding endpoint " + senderVirtualAddress + " to session " + message.getProperty(Message.SESSION_ID_KEY));
					//use same connection as stored for this session id
					Connection conn = sessions.get(message.getProperty(Message.SESSION_ID_KEY));
					timeouts.put(conn, cal.getTime());
					//remove temporary connection as proper connection has been added now
					connections.remove(tempCon);
					timeouts.remove(tempCon);
					tempCon.setStateResolved();
					return conn;
				}
				else
				{
					logger.warn("Remote endpoint did not start with communication handshake: " + senderVirtualAddress);
					//this is only allowed if there is no security required
					if(servicePolicies.containsKey(remoteEndpoint) 
							&& servicePolicies.get(remoteEndpoint).contains(SecurityProperty.NoSecurity)) {
						//we check whether we can open message with standard settings
						comSecMgrName = findMatchingCommunicationSecurityManager(
								servicePolicies.get(remoteEndpoint), null, null);
						logger.debug("Solved issue with " + senderVirtualAddress + " with NoSecurity as it is allowed");
					} else {
						//ignore message
						comSecMgrName = null;
					}
				}
			}
		}
		
		logger.debug("Selected communication method for remote party " + senderVirtualAddress + ":" + 
				((comSecMgrName == null)?"declined":comSecMgrName));

		if(comSecMgrName == null) {
			//communication has been declined
			handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
			return null;
		} else {
			//determine common communicationSecurityManager
			for(CommunicationSecurityManager comSecMgr : communicationSecurityManagers) {
				if(comSecMgr.getClass().getName().equals(comSecMgrName)) {
					agreedComSecMgr = comSecMgr;
					match = true;
					break;
				}
			}
			//there was no matching comSecMgr but no security could still match
			if(!match && (comSecMgrName.equals(SecurityProperty.NoSecurity.name()) 
					&& servicePolicies.get(remoteEndpoint).contains(SecurityProperty.NoSecurity))) {
				match = true;
			}

			if(!match) {
				//agreed CommunicationSecurityManager was not found on our side
				handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
				return null;
			}
		}

		//create the connection
		//if there was no connection that means that the sender is the client and the receiver is the server

		//check if connection should be a no encoding connection
		Connection conn = null;
		if(servicePolicies.get(remoteEndpoint).contains(SecurityProperty.NoEncoding)) {
			//if no encoding were not a match the matching part would have already failed
			conn = new NOPConnection(senderVirtualAddress, receiverVirtualAddress);
		} else {
			conn = new Connection(senderVirtualAddress, receiverVirtualAddress);
		}
		if (agreedComSecMgr != null) {
			conn.setCommunicationSecMgr(agreedComSecMgr);
		}
		conn.sessionId = sessionId;

		//if this was only a trial check if message is plausible
		if(!isHandshakeMsg && remoteEndpoint.equals(senderVirtualAddress)) {
			Message msg = conn.processData(senderVirtualAddress, receiverVirtualAddress, data);
			if(msg instanceof ErrorMessage) {
				//connection did not work out
				handleUnsuccesfulHandshake(remoteEndpoint, tempCon);
				return null;
			}
		}
		//add connection to list
		connections.add(conn);
		sessions.put(sessionId, conn);
		timeouts.put(conn, cal.getTime());
		//remove temporary connection as proper connection has been added now
		connections.remove(tempCon);
		timeouts.remove(tempCon);
		tempCon.setStateResolved();

		return conn;
	}

	private Connection createConnectionForLocalServices(VirtualAddress receiverVirtualAddress, VirtualAddress senderVirtualAddress) throws Exception{
		//policies only apply for connections between this NM and other entity
		List<SecurityProperty> policies = new ArrayList<SecurityProperty>();
		if(receiverVirtualAddress.equals(nmCore.getVirtualAddress()) || senderVirtualAddress.equals(nmCore.getVirtualAddress())) {
			//get virtual address of remote entity
			VirtualAddress remoteVirtualAddress = nmCore.getVirtualAddress().equals(receiverVirtualAddress)? senderVirtualAddress : receiverVirtualAddress;
			//check if there are policies for the VirtualAddress
			if(this.servicePolicies.containsKey(remoteVirtualAddress)){
				//add policies to requirement list
				policies = servicePolicies.get(remoteVirtualAddress);
			}

			//check if requirements are not colliding
			boolean noEncoding = policies.contains(SecurityProperty.NoEncoding);
			boolean noSecurity = policies.contains(SecurityProperty.NoSecurity);
			boolean justNoEncNoSec = policies.size() == 2 && noEncoding && noSecurity;
			//if there are more requirements and noEnc or noSec is included it must be colliding
			if (!justNoEncNoSec && (policies.size() > 1 && (noEncoding || noSecurity))) {
				throw new Exception("Colliding policies for services");
			}
		}


		//check if an active connection or a dummy connection is needed
		Connection conn = null;
		if(policies.contains(SecurityProperty.NoEncoding)) {
			conn = new NOPConnection(senderVirtualAddress, receiverVirtualAddress);
		} else {
			conn = new Connection(senderVirtualAddress, receiverVirtualAddress);
		}

		boolean foundComSecMgr = false;
		if (!this.communicationSecurityManagers.isEmpty()) {
			for (CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
				if (matchingPolicies(policies, comSecMgr.getProperties())){
					conn.setCommunicationSecMgr(comSecMgr);
					foundComSecMgr = true;
					break;
				}
			}
		}
		if (!foundComSecMgr 
				&& policies.size() != 0 
				&& !policies.contains(SecurityProperty.NoSecurity)) {
			//no available communication security manager although required
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}

		//add connection to list
		connections.add(conn);
		timeouts.put(conn, Calendar.getInstance().getTime());

		return conn;
	}

	private String findMatchingCommunicationSecurityManager(
			List<SecurityProperty> policies, String[] requiredSecProps,
			String[] availableComSecMgrs) throws Exception {

		//check if requirements are not colliding in local policies
		boolean noEncoding = policies.contains(SecurityProperty.NoEncoding);
		boolean noSecurity = policies.contains(SecurityProperty.NoSecurity);
		boolean justNoEncNoSec = policies.size() == 2 && noEncoding && noSecurity;
		//if there are more requirements and noEnc or noSec is included it must be colliding
		if (!justNoEncNoSec && (policies.size() > 1 && (noEncoding || noSecurity))) {
			throw new Exception("Colliding policies for services");
		}

		//parse received security properties
		List<SecurityProperty> requiredSecPropsList = new ArrayList<SecurityProperty>();
		List<String> requiredComSecMgrList = null;
		boolean noSecurityRequired = false;
		if (requiredSecProps != null) {
			for(String secProp : requiredSecProps) {
				requiredSecPropsList.add(SecurityProperty.valueOf(secProp));
			}
			//parse received ComSecMgrs
			requiredComSecMgrList = Arrays.asList(availableComSecMgrs);


			//check if requirements are not colliding on requiring side
			boolean noEncodingRequired = requiredSecPropsList.contains(SecurityProperty.NoEncoding);
			noSecurityRequired = requiredSecPropsList.contains(SecurityProperty.NoSecurity);
			boolean justNoEncNoSecRequired = requiredSecPropsList.size() == 2 && noEncoding && noSecurity;
			//if there are more requirements and noEnc or noSec is included it must be colliding
			if (!justNoEncNoSecRequired &&
					(requiredSecPropsList.size() > 1 && (noEncodingRequired || noSecurityRequired))) {
				throw new Exception("Colliding policies for services");
			}

			//ensure that the two sides agree on encoding or no encoding
			if(noEncodingRequired != noEncoding) {
				return null;
			}
		}
		//go through the available comSecMgrs and check whether they can fulfill both requirements
		boolean ownRequirementsOk = false;
		if (!this.communicationSecurityManagers.isEmpty()) {
			for (CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
				//first check against local policies
				if (matchingPolicies(policies, comSecMgr.getProperties())){
					if (requiredComSecMgrList == null) {
						return comSecMgr.getClass().getName();
					} else {
						ownRequirementsOk = true;
					}
					//now check against received policies
					if(requiredComSecMgrList.contains(comSecMgr.getClass().getName()) &&
							matchingPolicies(requiredSecPropsList, comSecMgr.getProperties())) {
						return comSecMgr.getClass().getName();
					}
				}
			}
		}

		//there was no matching comSecMgr so check whether no security is an option
		if ((policies.size() != 0 && !noSecurity && !ownRequirementsOk)) {
			//no available communication security manager although required locally - that is a problem
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}
		if((noSecurity && requiredComSecMgrList == null) || (noSecurity && noSecurityRequired)) {
			return SecurityProperty.NoSecurity.name();
		}
		//no match found so just return
		return null;
	}

	//Compose message of communication properties and send it to other end. Wait for answer of handshake. 
	private String[] startHandshakeOnCommunicationProperties(
			VirtualAddress receiverVirtualAddress,
			VirtualAddress senderVirtualAddress,
			String sessionId) {
		List<String> handshakeResults = new ArrayList<String>(); 
		//compose message with communication parameters
		//collect list of available communication security managers
		StringBuilder comSecMgrNames = new StringBuilder();
		for(CommunicationSecurityManager comSecMgr : communicationSecurityManagers){
			comSecMgrNames.append(comSecMgr.getClass().getName() + ";");
		}

		//compose list of security properties required by own configuration		
		StringBuilder secProperties = new StringBuilder();
		//by now servicePolicies should have an entry
		for (SecurityProperty prop : servicePolicies.get(receiverVirtualAddress)) {
			secProperties.append(prop.name() + ";");
		}

		Properties props = new Properties();
		props.put(Message.SESSION_ID_KEY, sessionId);
		props.put(HANDSHAKE_COMSECMGRS_KEY, comSecMgrNames.toString());
		props.put(HANDSHAKE_SECPROPS_KEY, secProperties.toString());

		//convert properties to xml and put it into stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedPayload = null;
		try {
			props.storeToXML(bos, null);
			serializedPayload = bos.toByteArray();
		} catch (IOException e) {
			logger.warn("Message to be sent cannot be parsed!");
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				logger.error("Error closing stream", e);
			}
		}
		//finally create the actual message to be sent
		Message handshakeMsg = new Message(
				Message.TOPIC_CONNECTION_HANDSHAKE,
				senderVirtualAddress,
				receiverVirtualAddress,
				serializedPayload);

		//the response contains the communication parameters or a decline
		NMResponse response = nmCore.sendMessage(handshakeMsg, true);
		Message respMsg = response.getMessageObject();

		if (respMsg != null && new String(respMsg.getData()).startsWith(HANDSHAKE_ACCEPT)) {
			//take the matching ComSecMgr and return it
			String comSecMgrName = new String(respMsg.getData()).
					substring(HANDSHAKE_ACCEPT.length() + 1);
			handshakeResults.add(comSecMgrName);
			if(respMsg.getKeySet().contains(Message.SESSION_ID_KEY)) {
				if(!respMsg.getProperty(Message.SESSION_ID_KEY).equals(sessionId)) {
					//the other end had a collision and changed the session id
					handshakeResults.add(respMsg.getProperty(Message.SESSION_ID_KEY));				
				}
			}
			return handshakeResults.toArray(new String[]{});
		} else {
			if(respMsg != null && new String(response.getMessage().getBytes()).contains(HANDSHAKE_DECLINE)) {
				//decline message looks like "HANDSHAKE_DECLINED [PropertiesOfOtherEndpoint]"
				logger.warn("Could not establish common parameters with " + receiverVirtualAddress + " as it required:" +
						new String(response.getMessage().getBytes()).substring(HANDSHAKE_DECLINE.length() + 1));
			} else {
				logger.error("Something went wrong during the communication handshake process!");
			}
			return null;
		}
	}

	/**
	 * Returns the {@Connection} which does general processing for
	 * broadcast messages.
	 * @param senderVirtualAddress Sender of the message
	 * @return Connection to be used for getting the contents of the received data
	 * @throws Exception If there are policies for an VirtualAddress but no CommunicationSecurityManager
	 */
	public synchronized Connection getBroadcastConnection(VirtualAddress senderVirtualAddress) throws Exception {
		Calendar cal = Calendar.getInstance();
		//find connection belonging to this VirtualAddress
		for(Connection c : connections){
			if(c.getServerVirtualAddress().equals(senderVirtualAddress) && c instanceof BroadcastConnection) {
				//set last use date of connection
				timeouts.put(c, cal.getTime());
				return c;
			}
		}

		//FIXME broadcast messages and policies have to be re-thought

		//there was no connection found so create new connection	
		List<SecurityProperty> policies = null;
		if(!senderVirtualAddress.equals(nmCore.getVirtualAddress())) {
			policies = this.servicePolicies.get(senderVirtualAddress);
		}
		//check if an active connection or a dummy connection is needed
		BroadcastConnection conn = null;
		if(policies != null && policies.contains(SecurityProperty.NoEncoding)) {
			conn = new NOPBroadcastConnection(senderVirtualAddress);
		} else {
			conn = new BroadcastConnection(senderVirtualAddress);
		}

		boolean foundComSecMgr = false;
		if(!this.communicationSecurityManagers.isEmpty()) {
			if(policies != null){
				for(CommunicationSecurityManager comSecMgr : this.communicationSecurityManagers) {
					if(matchingPolicies(policies, comSecMgr.getProperties())
							&& comSecMgr.getProperties().contains(SecurityProperty.Broadcast)) {
						conn.setCommunicationSecMgr(comSecMgr);
						foundComSecMgr = true;
					}
				}
			}
		}
		if(!foundComSecMgr
				&& policies != null 
				&& policies.size() != 0
				&& policies.contains(SecurityProperty.Broadcast)
				&& !policies.contains(SecurityProperty.NoSecurity)) {
			//no available communication security manager although required
			throw new Exception("Required properties not fulfilled by ConnectionSecurityManagers");
		}
		//add connection to list
		connections.add(conn);
		timeouts.put(conn, cal.getTime());

		return conn;
	}

	/**
	 * Sets the number of minutes before a connection is closed
	 * @param minutes Timeout to be used
	 */
	public void setConnectionTimeout(int minutes){
		this.timeoutMinutes = minutes;
		timer.cancel();
		timer.schedule(new ConnectionClearer(), 0, timeoutMinutes * 60 * 1000 / 2);
	}

	/**
	 * Checks whether two sets of policies math each other.
	 * @param required List of required properties
	 * @param provided List of provided properties
	 * @return
	 */
	private boolean matchingPolicies(List<SecurityProperty> required, List<SecurityProperty> provided){
		for(SecurityProperty req : required){
			if(!provided.contains(req)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes connection and its references in timeout.
	 * @param con
	 */
	private void deleteConnection(Connection con) {
		connections.remove(con);
		timeouts.remove(con);
	}

	/**
	 * Puts the remote endpoint on the banned list until the connection timeout expires.
	 * @param remoteEndpoint The remote side of the communication
	 * @param tempCon The created handshake connection
	 */
	private void handleUnsuccesfulHandshake(VirtualAddress remoteEndpoint, HandshakeConnection tempCon) {
		connections.remove(tempCon);
		timeouts.remove(tempCon);
		tempCon.setFailed();

		//		Connection bannedConnection;
		//		try {
		//			bannedConnection = new HandshakeConnection(nmCore.getVirtualAddress(), remoteEndpoint, this);
		//			if(!bannedConnections.contains(bannedConnection)) {
		//				this.bannedConnections.add(bannedConnection);	
		//			}
		//			this.timeouts.put(bannedConnection, Calendar.getInstance().getTime());
		//		} catch(RemoteException e) {
		//			//local invocation
		//		}
	}

	public void setBusy() {
		this.busy = true;
	}

	public boolean isBusy() {
		return this.busy;
	}

	public void setNotBusy() {
		this.busy = false;
		synchronized(this) {
			this.notifyAll();
		}
	}

	/**
	 * Returns the declining handshake message for the provided entities.
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @return
	 */
	public NMResponse getDeclineHandshakeMessage(
			VirtualAddress senderVirtualAddress,
			VirtualAddress receiverVirtualAddress) {
		String declineMessage = HANDSHAKE_DECLINE + " ";
		VirtualAddress nmVA = null;
		try {
			nmVA = nmCore.getVirtualAddress();
		} catch (RemoteException e1) {
			//local invocation
		}
		//find the policies which would have been required
		//the remote side of this connection
		VirtualAddress remoteEndpoint = null;
		if(receiverVirtualAddress.equals(nmVA) || senderVirtualAddress.equals(nmVA)) {
			//get virtual address of remote entity
			remoteEndpoint = nmVA.equals(receiverVirtualAddress)? senderVirtualAddress : receiverVirtualAddress;
		}
		//add the policies
		if(servicePolicies.containsKey(remoteEndpoint)) {
			StringBuilder secProperties = new StringBuilder();
			for (SecurityProperty prop : servicePolicies.get(remoteEndpoint)) {
				secProperties.append(prop.name() + ";");
			}
			declineMessage = declineMessage.concat(secProperties.toString());
		}

		NMResponse resp = new NMResponse(NMResponse.STATUS_ERROR);
		Connection tempCon = null;
		for(Connection c : bannedConnections) {
			if(!(c instanceof BroadcastConnection) &&
					((c.getClientVirtualAddress().equals(nmVA) && c.getServerVirtualAddress().equals(remoteEndpoint))
							|| (c.getClientVirtualAddress().equals(remoteEndpoint) && c.getServerVirtualAddress().equals(nmVA)))){
				tempCon = c;
			}
		}
		ErrorMessage errorMsg = 
			new ErrorMessage(
					ErrorMessage.TOPIC_CONNECTION_HANDSHAKE,
					senderVirtualAddress, receiverVirtualAddress, 
					declineMessage.getBytes());
		if(tempCon != null) {
			try {
				resp.setBytesPrimary(true);
				resp.setMessageBytes(tempCon.processMessage(errorMsg));
			} catch (Exception e) {
				resp.setBytesPrimary(true);
				resp.setMessageBytes(MessageSerializerUtiliy.serializeMessage(errorMsg, true, true));
			}
		} else {
			resp.setBytesPrimary(true);
			resp.setMessageBytes(MessageSerializerUtiliy.serializeMessage(errorMsg, true, true));
		}
		return resp;
	}

	/**
	 * Checks whether the data contains a message with handshake topic
	 * @param data
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @return
	 */
	public boolean isHandshakeMessage(byte[] data, 
			VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress) {
		//try to open message in standard way
		Message handshakeMessage = 
				MessageSerializerUtiliy.
				unserializeMessage(data, true, senderVirtualAddress, receiverVirtualAddress, false);
		if(handshakeMessage instanceof ErrorMessage 
				|| !handshakeMessage.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)) {
			return false;
		} else if (handshakeMessage.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)){
			return true;
		}
		return false;
	}

	public void getLock() {
		synchronized(this) {
			while(isBusy()) {
				try {
					wait(2500);
				} catch (InterruptedException e) {
					// nothing to handle
				}
			}
			setBusy();
		}
	}

	/**
	 * Clears not used connections from list.
	 * @author Vinkovits
	 *
	 */
	private class ConnectionClearer extends TimerTask{

		/**
		 * Runs through all referenced and banned connections and deletes them if timeout expired
		 */
		public void run() {
			try{
				getLock();
				//go through all references and remove them when needed
				Set<Connection> livingConns = timeouts.keySet();
				Iterator<Connection> i = livingConns.iterator();
				Calendar calendar = Calendar.getInstance();
				while(i.hasNext()){
					Connection con = i.next();
					if(calendar.getTimeInMillis() - timeouts.get(con).getTime() > timeoutMinutes * 60 * 1000){
						//timeout has expired so delete it
						String sessionId = con.sessionId;
						i.remove();
						if(connections.contains(con)) {
							sessions.remove(con.sessionId);
							connections.remove(con);
						} else if (bannedConnections.contains(con)) {
							bannedConnections.remove(con);
						}
						if(con instanceof HandshakeConnection) {
							((HandshakeConnection) con).setFailed();
						}
						//check if there was an alias using this connection
						Iterator<Connection> it = aliases.iterator();
						while(it.hasNext()) {
							Connection c = it.next();
							if(c.sessionId.equals(sessionId)) {
								it.remove();
							}
						}
					}
				}
			} finally {
				setNotBusy();
			}
		}	
	}
}
