package eu.linksmart.gc.api.types.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;

import com.google.gson.Gson;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.utils.Base64;

public class SerializationUtil {
	
/*
	
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = new ObjectInputStream(bis);
		Object o = in.readObject();
		bis.close();
		in.close();
		return o;
	}
	public static byte[] serialize(Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(o);
		byte[] encoded = bos.toByteArray();
		out.close();
		bos.close();
		return encoded;
     }
*/
    public static byte[] serialize(Object o) throws IOException {

       return  new Gson().toJson(o).getBytes("utf-8");

	}
    public static Object deserialize(byte[] bytes,Type type) throws IOException, ClassNotFoundException {

        String serializedObject = new String(bytes,"utf-8");
        return new Gson().fromJson(serializedObject,type);
    }

	public static Message deserializeMessage(byte[] payload, VirtualAddress senderVirtualAddress) throws Exception {
		
		//
		// reading XML data document
		//
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new ByteArrayInputStream(payload));
		} catch (InvalidPropertiesFormatException e) {
			throw new Exception("deserializeMessage: unable to load properties from XML data. Data is not valid XML: " + new String(payload));
		} catch (IOException e) {
			throw new Exception("deserializeMessage: unable to load properties from XML data: " + new String(payload));
		}
		
		//
		// extracting message
		//
		Message ls_message = new Message((String) properties.remove("topic"), senderVirtualAddress, null, (Base64.decode((String) properties.remove("applicationData"))));
		
		//
		// go through the properties and add them to the message
		//
		boolean includeProps = true; 
		if(includeProps) {
			Iterator<Object> i = properties.keySet().iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				ls_message.setProperty(key, properties.getProperty(key));
			}
		}
					
		return ls_message;
	}
	
	public static byte[] serializeMessage(Message msg, boolean includeProperties) throws Exception {
		
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
		props.put("applicationData", Base64.encodeBytes(msg.getData()));
		props.put("topic", msg.getTopic());
		//convert properties to xml and put it into stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] serializedCommand = null;
		props.storeToXML(bos, null);
		serializedCommand = bos.toByteArray();
		bos.close();
		
		return serializedCommand;
	}

}
