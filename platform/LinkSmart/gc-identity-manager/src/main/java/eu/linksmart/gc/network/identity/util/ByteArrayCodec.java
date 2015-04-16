package eu.linksmart.gc.network.identity.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/** Converts serializable objects to and from bytearrays
 * see http://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
 * 
 * It has to be local to the invoking class because 
 * else it may not now the classes it is serializing.
 * 
 * @author fit-ee
 * @version $Id$
 */
public class ByteArrayCodec {
	
	/** Encodes a Serializable object to a byte array
	 * Will throw an exception if the object is not Serializable
	 * @param o - the object to encode to a byte[]
	 * @return a byte-array representation of the *serialized* object
	 * @throws IOException
	 */
	public static final byte[] encodeObjectToBytes(Object o) throws IOException {	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(o);
		byte[] encoded = bos.toByteArray(); 
		out.close();
		bos.close();
		return encoded;
	}
	
	/** decodes a byte array to a Serializable object
	 *  the object class definition must be already accessible
	 *  otherwise an exception will be thrown
	 *  
	 * @param bytes - the bytearray to decode into an object
	 * @return the decoded object. Cast it to the needed class before use
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object decodeByteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = new ObjectInputStream(bis);
		Object o = in.readObject();
		bis.close();
		in.close();
		return o;
	}
}
