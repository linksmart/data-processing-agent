package eu.linksmart.gc.network.connection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.security.communication.CommunicationSecurityManager;
import eu.linksmart.gc.api.security.communication.CryptoException;
import eu.linksmart.gc.api.security.communication.SecurityProperty;
import eu.linksmart.gc.api.security.communication.SecurityProtocol;
import eu.linksmart.gc.api.security.communication.VerificationFailureException;

/**
 * Holds properties and objects relevant for a connection between two services.
 * 
 * @author Vinkovits
 * 
 */
public class Connection {

	/**
	 * Name of the property holding the applicaton data
	 */
	public static final String APPLICATION_DATA = "applicationData";
	/**
	 * Name of the property holding the topic of the message
	 */
	public static final String TOPIC = "topic";

	/**
	 * Logger from log4j
	 */
	Logger logger = Logger.getLogger(Connection.class.getName());
	/**
	 * {@link SecurityProtocol} used to protect and unprotect messages
	 */
	protected Map<VirtualAddressTuple, SecurityProtocol> securityProtocols = new ConcurrentHashMap<Connection.VirtualAddressTuple, SecurityProtocol>();
	/**
	 * The initiator of this communication
	 */
	private VirtualAddress clientVirtualAddress = null;
	/**
	 * The called entity of this communication
	 */
	private VirtualAddress serverVirtualAddress = null;
	/**
	 * CommunicationSecurityManager used to secure this communication.
	 */
	protected CommunicationSecurityManager comSecMgr;
	/**
	 * Session id identifying this connection.
	 */
	protected String sessionId;

	protected Connection(VirtualAddress serverVirtualAddress) {
		if (serverVirtualAddress == null) {
			throw new IllegalArgumentException(
					"Cannot set null for required fields.");
		}
		this.serverVirtualAddress = serverVirtualAddress;
	}

	public Connection(VirtualAddress clientVirtualAddress, VirtualAddress serverVirtualAddress) {
		if (clientVirtualAddress == null || serverVirtualAddress == null) {
			throw new IllegalArgumentException(
					"Cannot set null for required fields.");
		}
		this.clientVirtualAddress = clientVirtualAddress;
		this.serverVirtualAddress = serverVirtualAddress;
	}

	public VirtualAddress getClientVirtualAddress() {
		return clientVirtualAddress;
	}

	public VirtualAddress getServerVirtualAddress() {
		return serverVirtualAddress;
	}

	/**
	 * Set {@link CommunicationSecurityManager} for this connection. It has to 
	 * be ensured that no message is processed until CommunicationSecurityManager
	 * is set for connection.
	 * 
	 * @param comSecMgr
	 *            Reference for creator of {@link SecurityProtocol}
	 */	
	protected void setCommunicationSecMgr(CommunicationSecurityManager comSecMgr) {
		this.comSecMgr = comSecMgr;
	}

	/**
	 * Creates a Message object for received data.
	 * @param senderVirtualAddress logical endpoint of message
	 * @param receiverVirtualAddress logical endpoint of message
	 * @param data
	 *            Data received over network
	 * @return Message object for further processing
	 */
	public Message processData(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		SecurityProtocol securityProtocol = getSecurityProtocol(senderVirtualAddress, receiverVirtualAddress);
		Message message = null;
        //while opening message we have to know whether its a protected or an unprotected message
        boolean protectionActive = securityProtocol != null && securityProtocol.isInitialized();
        //if protection is active there are no properties to consider
        message = MessageSerializerUtiliy.unserializeMessage(
                data, !protectionActive, senderVirtualAddress, receiverVirtualAddress, true);
        //decide how to handle message
        if(message.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)) {
            //if this is the message that created this connection, we have to respond to it
            String usedSecurity = ConnectionManager.HANDSHAKE_ACCEPT + " ";
            usedSecurity = usedSecurity.concat(
                    (comSecMgr != null) ?
                            comSecMgr.getClass().getName() :
                            SecurityProperty.NoSecurity.name());
            Message msg = new Message(
                    Message.TOPIC_CONNECTION_HANDSHAKE,
                    receiverVirtualAddress,
                    senderVirtualAddress,
                    usedSecurity.getBytes());

            if(sessionId != null) {
                msg.setProperty(Message.SESSION_ID_KEY, sessionId);
            }
            return msg;
        } else if (protectionActive) {
            // if protocol is initialized than open message with it
            try {
                message = securityProtocol.unprotectMessage(message);
                //use data field of message to reconstruct original message
                if (!message.getTopic().
                        equals(CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
                    message = MessageSerializerUtiliy.unserializeMessage(
                            message.getData(), true, senderVirtualAddress, receiverVirtualAddress, true);
                }
            } catch (Exception e) {
                logger.debug("Cannot unprotect message from VirtualAddress: "
                        + senderVirtualAddress.toString());
                message = new ErrorMessage(ErrorMessage.ERROR,
                        message.getSenderVirtualAddress(),
                        message.getReceiverVirtualAddress(),
                        e.getMessage().getBytes());
            }
        } else if (securityProtocol != null) {
            // if protocol not initialized then pass it for processing
            // Process message by Security Protocol
            try {
                message = securityProtocol.processMessage(message);
            } catch (CryptoException e) {
                logger.error("Error during cryptographic operation", e);
                message = new ErrorMessage(ErrorMessage.ERROR,
                        message.getSenderVirtualAddress(),
                        message.getReceiverVirtualAddress(),
                        e.getMessage().getBytes());
            } catch (VerificationFailureException e) {
                logger.error("Error during cryptographic operation", e);
                message = new ErrorMessage(ErrorMessage.ERROR,
                        message.getSenderVirtualAddress(),
                        message.getReceiverVirtualAddress(),
                        e.getMessage().getBytes());
            } catch (IOException e) {
                logger.error("Error during cryptographic operation", e);
                message = new ErrorMessage(ErrorMessage.ERROR,
                        message.getSenderVirtualAddress(),
                        message.getReceiverVirtualAddress(),
                        e.getMessage().getBytes());
            }
        }
		return message;
	}

	/**
	 * Gets the {@link SecurityProtocol} object assigned to provided services from
	 * the HashMap. If no object is stored a new one is created.
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @return SecurityProtocol assigned to this service
	 */
	protected SecurityProtocol getSecurityProtocol(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress) {
		if(comSecMgr != null) {
			VirtualAddressTuple virtualAddressTuple = new VirtualAddressTuple(senderVirtualAddress, receiverVirtualAddress);
			if(securityProtocols.containsKey(virtualAddressTuple)) {
				return securityProtocols.get(virtualAddressTuple);
			} else {
				SecurityProtocol secProt = comSecMgr.getSecurityProtocol(senderVirtualAddress, receiverVirtualAddress);
				securityProtocols.put(virtualAddressTuple, secProt);
				return secProt;
			}
		} else {
			return null;
		}
	}

	/**
	 * Creates a serialized representation of the Message object which can be
	 * sent over network
	 * @param msg
	 *            Message to convert
	 * @return Serialized version of the message including all properties
	 * @throws Exception
	 *             When message cannot be processed for sending
	 */
	public byte[] processMessage(Message msg) throws Exception {
		//connection handshake messeages are passed through
		if(msg.getTopic().equals(Message.TOPIC_CONNECTION_HANDSHAKE)) {
			return MessageSerializerUtiliy.serializeMessage(msg, true, true);
		}
		SecurityProtocol securityProtocol = getSecurityProtocol(msg.getSenderVirtualAddress(), msg.getReceiverVirtualAddress());
		if (securityProtocol != null && !securityProtocol.isInitialized()) {
			// set the message to be sent by security protocol
			msg = securityProtocol.processMessage(msg);
			// the message has to be processed for sending now by the regular
			// code
		}

		//set session id into message
		if (sessionId != null)
		{
			msg.setProperty(Message.SESSION_ID_KEY, this.sessionId);
		}
		//serialize the message into one stream to protect it
		byte[] serializedCommand = MessageSerializerUtiliy.serializeMessage(msg, true, true);
		//protect the stream if should be
		if (securityProtocol != null
				&& securityProtocol.isInitialized()
				&& !msg.getTopic().contentEquals(
						CommunicationSecurityManager.SECURITY_PROTOCOL_TOPIC)) {
			/*
			 * this could also be a message which has been stored by the
			 * security protocol until now and is becoming sent at last
			 */
			// set all data of the message as data part and protect it
			msg.setData(serializedCommand);
			msg = securityProtocol.protectMessage(msg);
			//set session id into message as it was hidden by encryption
			msg.setProperty(Message.SESSION_ID_KEY, this.sessionId);
			//serialize the created protected dummy message
			return MessageSerializerUtiliy.serializeMessage(msg, false, true);
		} else {
			return serializedCommand;
		}
	}

	class VirtualAddressTuple {
		private VirtualAddress virtualAddress1 = null;
		private VirtualAddress virtualAddress2 = null;

		public VirtualAddressTuple(VirtualAddress one, VirtualAddress two) {
			virtualAddress1 = one;
			virtualAddress2 = two;
		}

		public VirtualAddress getVirtualAddress1() {
			return virtualAddress1;
		}

		public VirtualAddress getVirtualAddress2() {
			return virtualAddress2;
		}

		@Override
		public boolean equals(Object o)  {
			VirtualAddressTuple tuple = (VirtualAddressTuple)o;
			if((tuple.getVirtualAddress1().equals(virtualAddress1) && tuple.getVirtualAddress2().equals(virtualAddress2))
					|| (tuple.getVirtualAddress1().equals(virtualAddress2) && tuple.getVirtualAddress2().equals(virtualAddress1))) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			int virtualAddress1Hash = virtualAddress1.hashCode();
			int virtualAddress2Hash = virtualAddress2.hashCode();
			//returned hash must be indifferent for tuples with same two addresses
			return virtualAddress1Hash & virtualAddress2Hash;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(obj.getClass().equals(this.getClass())) {
			Connection c = (Connection)obj;
			if(c.getClientVirtualAddress() != null) {
				if((c.getClientVirtualAddress().equals(this.clientVirtualAddress) && c.getServerVirtualAddress().equals(this.serverVirtualAddress))
						|| (c.getClientVirtualAddress().equals(this.serverVirtualAddress) && c.getServerVirtualAddress().equals(this.clientVirtualAddress))) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.clientVirtualAddress.hashCode() ^ this.serverVirtualAddress.hashCode() ^ this.getClass().getName().hashCode();
	}
}
