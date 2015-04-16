package eu.linksmart.gc.api.security.communication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import eu.linksmart.gc.api.network.Message;

/**
 * Represents a protocol which is used to protect
 * messages sent over a backbone. After getting a reference
 * to a SecurityProtocol implementation the protocol has to be started.
 */
public interface SecurityProtocol {

	public static final short CONF_ENC=1;
	public static final short CONF_NULL=0;
	public static final short CONF_ENC_SIG_SPORADIC=2;
	public static final short CONF_ENC_SIG=3;
	public static final String INSIDE_SECURITY_NAMESPACE = "http://linksmart.eu/ns/security/inside";
	public static final String INSIDE_SIGNED_MESSAGE_NAMESPACE = "http://linksmart.eu/ns/security/inside_sig";
	public static final String INSIDE_PROTECTED_MESSAGE_NAME = "linksmart:InsideProtectedMessage";
	public static final String INSIDE_SIGNED_MESSAGE_NAME = "linksmart:InsideSignedProtectedMessage";
	public static final String INSIDE_COUNTER_ELEMENT = "linksmart:InsideCounter";
	public static final String INSIDE_CONTENT_ELEMENT = "linksmart:InsideContent";
	public static final String INSIDE_PROTECTED_ELEMENT = "linksmart:InsideProtected";
	public static final String CIPHER_TEXT = "CIPHERTEXT";
	
	/**
	 * Starts the initialization of the security
	 * protocol. This might involve handshake or
	 * key agreement protocols.
	 * 
	 * @throws CryptoException If cryptography specific exception occurred
	 * @return Message which has to be sent to other party; null if no message has to be sent
	 */
	Message startProtocol() throws CryptoException;
	
	/**
	 * @return Whether this object can already protect and unprotect messages or not.
	 */
	boolean isInitialized();
	
	/**
	 * Processes general messages (like handshake) of 
	 * the protocol. If the message is not identified 
	 * it is returned in its original form.
	 * 
	 * Until the object is not initialized messages should
	 * be passed to this method.
	 * 
	 * @param msg Message to be processed
	 * @throws CryptoException If cryptography specific exception occurred
	 * @throws VerificationFailureException If a received signature is not valid
	 * @throws IOException If received data cannot be interpreted
	 * @return original Message object if it is not identified or protocol message which has
	 * to be sent to other party.
	 */
	Message processMessage(Message msg) throws CryptoException, VerificationFailureException, IOException;
	
	/**
	 * Protects the data part of the message with 
	 * the specific protocol. Adds all necessary
	 * meta-information for opening with 
	 * {@link unprotectMessage()}.
	 * 
	 * @param msg Message to be protected
	 * @throws Exception If message cannot be processed
	 * @return Message with protected content
	 */
	Message protectMessage(Message msg) throws Exception;
	
	/**
	 * Opens a protected message body and removes all
	 * security protocol specific meta-information from it.
	 * 
	 * @param msg Message to be opened
	 * @throws Exception If message cannot be processed
	 * @return Message with unprotected body
	 */
	Message unprotectMessage(Message msg) throws Exception;
	
	/**
	 * Provides whether this security protocol implementation
	 * can protect broadcast messages
	 * @return True if broadcast methods can be called
	 */
	boolean canBroadcast();
	
	/**
	 * Protects the data part of the message with 
	 * the specific protocol aimed for broadcasting.
	 * Adds all necessary meta-information for opening with 
	 * {@link unprotectBroadcastMessage()}.
	 * 
	 * @param msg Message to be protected
	 * @throws Exception If message cannot be processed
	 * @return Message with protected content
	 */
	Message protectBroadcastMessage(Message msg) throws Exception;
	
	/**
	 * Opens a protected broadcast message body and removes all
	 * security protocol specific meta-information from it.
	 * 
	 * @param msg Message to be opened
	 * @throws Exception If message cannot be processed
	 * @return Message with unprotected body
	 */
	Message unprotectBroadcastMessage(Message msg) throws Exception;
}
