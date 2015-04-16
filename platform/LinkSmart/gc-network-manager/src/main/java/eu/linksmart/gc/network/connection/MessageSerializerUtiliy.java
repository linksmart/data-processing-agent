package eu.linksmart.gc.network.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.linksmart.gc.api.network.ErrorMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.utils.Base64;

/**
 * Provides methods to serialize and unserialize messages
 * @author Vinkovits
 *
 */
public class MessageSerializerUtiliy {
	/**
	 * Logger from log4j
	 */
	static Logger logger = Logger.getLogger(MessageSerializerUtiliy.class.getName());
	
	/**
	 * Creates a stream from the provided message
	 * @param msg to be serialized
	 * @param includeProperties
	 * @return
	 */
	public static byte[] serializeMessage(Message msg, boolean includeProperties,
			boolean showException) {
		Properties props = new Properties();
		if(includeProperties) {
			// read the properties of the message and put it into the properties
			Iterator<String> i = msg.getKeySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				props.put(key, msg.getProperty(key));
			}
		}
		// put application data into properties
		props.put(Connection.APPLICATION_DATA, Base64.encodeBytes(msg.getData()));
		props.put(Connection.TOPIC, msg.getTopic());
		//convert properties to xml and put it into stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedCommand = null;
		try {
			props.storeToXML(bos, null);
			serializedCommand = bos.toByteArray();
		} catch (IOException e) {
			if(showException)logger.warn("Message to be sent cannot be parsed!");
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				logger.error("Error closing stream", e);
			}
		}
		return serializedCommand;
	}
	
	/**
	 * Creates message from received byte stream.
	 * @param serializedMsg Stream to read from
	 * @param includeProps Fill properties fields of msg
	 * @param senderVirtualAddress
	 * @param receiverVirtualAddress
	 * @return
	 */
	public static Message unserializeMessage(byte[] serializedMsg,
			boolean includeProps, VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			boolean showException) {
		// open data and divide it into properties of the message and
		// application data
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new ByteArrayInputStream(serializedMsg));
		} catch (InvalidPropertiesFormatException e) {
			if(showException)logger.warn(
					"Unable to load properties from XML data. Data is not valid XML: "
					+ new String(serializedMsg));
			return new ErrorMessage(ErrorMessage.RECEPTION_ERROR,
					senderVirtualAddress, receiverVirtualAddress, e.getMessage().getBytes());
		} catch (IOException e) {
			if(showException)logger.warn("Unable to load properties from XML data: "
					+ new String(serializedMsg));
			return new ErrorMessage(ErrorMessage.RECEPTION_ERROR,
					senderVirtualAddress, receiverVirtualAddress, e.getMessage().getBytes());
		}

		// create real message
		Message message = new Message((String) properties.remove(Connection.TOPIC),
				senderVirtualAddress, receiverVirtualAddress, (Base64.decode((String) properties
						.remove(Connection.APPLICATION_DATA))));
		if(includeProps) {
			// go through the properties and add them to the message
			Iterator<Object> i = properties.keySet().iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				message.setProperty(key, properties.getProperty(key));
			}
		}
		return message;
	}

}
