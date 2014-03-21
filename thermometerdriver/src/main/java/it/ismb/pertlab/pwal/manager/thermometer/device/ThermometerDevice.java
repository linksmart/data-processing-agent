package it.ismb.pertlab.pwal.manager.thermometer.device;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;

/**
 * Class used to drive a Taidoc td-1261
 *
 */
public class ThermometerDevice implements Thermometer {

	private static final Logger log = LoggerFactory.getLogger(ThermometerDevice.class);
	
	// max timeout (msec) to consider as "new" a measure
	private static final int MAX_TIME_DIFFERENCE = 180000; 
	private static final String HEXES = "0123456789ABCDEF";
	
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private String id;
	private final String type="pwal:Thermometer";
	private DevicesManager parent;

	private String connectionUrl;
	
	/**
	 * Constructor of the Device
	 * 
	 * @param out
	 *          output stream to be used to write data to the thermometer
	 *          
	 * @param in
	 *          input stream to be used to read data from the thermometer
	 */
	public ThermometerDevice(DevicesManager parent, DataOutputStream out, DataInputStream in) {
		this.parent = parent;
		this.out = out;
		this.in = in;
	}
	
	public ThermometerDevice(String connectionUrl)
	{
		this.connectionUrl=connectionUrl;
	}
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	private StreamConnection connectToDevice() throws IOException {
		return (StreamConnection)Connector.open(connectionUrl);
	}
	
	@Override
	public Double getTemperature() {
		try {
			connectToDevice();
			boolean error = false;
			String timestamp = "";
			// This first call, activates the thermometer
			byte [] activate = {0x51, 0x25, 0x0, 0x0, 0x0, 0x0, (byte) 0xA3, 0x19 };
	    	log.debug("going to activate the thermometer, send: "+getHex(activate));
			out.write (activate);
	    	Thread.sleep(1500);
	    	out.flush();
	    	int bytesRead = 0, totalBytesRead = 0;
	    	// This second call is used to retrieve the timestamp
			byte [] getTimestamp = {0x51, 0x25, 0x0, 0x0, 0x0, 0x0, (byte) 0xA3, 0x19 };
			byte[] timestampBytes = null;
	    	do {
	    		log.debug("going to get the timestamp, send: "+getHex(getTimestamp));
	        	out.write (getTimestamp);
	        	Thread.sleep(1500);
	        	out.flush();
	        	timestampBytes= new byte[1024];
				bytesRead = in.read(timestampBytes);
				log.debug("...receiving "+getHex(timestampBytes));
				if(bytesRead>0 && bytesRead<=8) {
					totalBytesRead+=bytesRead;
				} else if(bytesRead<0) {
					error=true;
				}
				else
					totalBytesRead = bytesRead;
	    	} while(!error && totalBytesRead<8);
			if(totalBytesRead==8) {
				if(timestampBytes[0]==0x51 &&
					timestampBytes[1]==0x25 &&
					timestampBytes[6]==(byte)0xA5 &&
					timestampBytes[7]==checksum(timestampBytes)) {
					timestamp=decodeTimestamp(timestampBytes);
				} else {
					log.warn("DriverTaiDoc: Received "+bytesRead+" bytes, but I don't know what they are.");
					error=true;
				}
			} else {
				log.warn("DriverTaiDoc: I was waiting for 8 bytes, but I received "+bytesRead+" bytes");
				error = true;
			}
			if(!error) {
	   			if(Math.abs(Long.parseLong(timestamp)-Long.parseLong(getTimestamp()))>MAX_TIME_DIFFERENCE) {
	   				log.warn("DriverTaidoc: Measure just received but refused for: internal clock disaligned.");
					// Try to set the clock of the instrument
					byte [] timeToSet = getTimeToSet();
					byte [] setTime = new byte [8]; 
					setTime[0]=0x51;
					setTime[1]=0x33;
					setTime[2]=timeToSet[0];
					setTime[3]=timeToSet[1];
					setTime[4]=timeToSet[2];
					setTime[5]=timeToSet[3];
					setTime[6]=(byte) 0xA3;
					setTime[7]=checksum(setTime);
	            	out.write (setTime);
	            	Thread.sleep(1500);
	            	out.flush();
	            	bytesRead = 0;
	            	byte[] timestampACKBytes = new byte[1024];
					bytesRead = in.read(timestampACKBytes);
					// Verifies to receive the correct ACK
					// and that contains the timestamp set
					if(bytesRead==8) {
						if(checksum(timestampACKBytes)==timestampACKBytes[7]){
							if(setTime[2]==timestampACKBytes[2] &&
								setTime[3]==timestampACKBytes[3] &&
								setTime[4]==timestampACKBytes[4] &&
								setTime[5]==timestampACKBytes[5]) {
									error=false;
							} else {
								error=true;
							}
						} else {
							error=true;
						}
					} else {
						error = true;
					}
	   			} else {
	       			// With this call, it retrieves the last value measured
					byte[]getMeasure = {0x51, 0x26, 0x0, 0x0, 0x0, 0x0, (byte) 0xA3, 0x1A};
					out.write(getMeasure);
	            	Thread.sleep(1500);
					out.flush();
					byte[] measuresBytes = new byte[8];
					in.read(measuresBytes);
					String values = getHex(measuresBytes);
					String hexValue=values.charAt(4)+""+values.charAt(5);
					int intThermoValue = Integer.valueOf(hexValue, 16).intValue();
	       			hexValue=values.charAt(6)+""+values.charAt(7);
	       			int intValue = Integer.valueOf(hexValue, 16).intValue();
	       			String control=""+intValue;
	       			if(!control.equals("0"))
	       				intThermoValue+=256;
	       			String stringThermoValue=""+intThermoValue;
	       			// The value of temperature is obtained inserting a '.' before
	       			// the last value: e.g. 361 is equal to a temperature of 36.1 degrees
	       			StringBuilder builder = new StringBuilder();
	       			builder.append(stringThermoValue);
	       			if(builder.length()>2) {
	       				builder.insert(2,".");
	       			} else {
	       				// In case of errors return 0.0 as temperature
//	       				synchronized(parent) {
//	       					parent.notify();
//	       				}
	       				return Double.valueOf(0.0);
	       			}
	       			stringThermoValue = builder.toString();
//       				synchronized(parent) {
//       					parent.notify();
//       				}
	       			return Double.valueOf(stringThermoValue);
	        	}
	    	}
		} catch (Exception e) {
			log.error("Error reading the temperature", e);
		}
//		synchronized(parent) {
//			parent.notify();
//		}
		return Double.valueOf(0.0);
	}
	
	
	
	/**
	 * Retrieves the checksum from the byte array passed as parameter
	 * 
	 * @param block: 
	 *           array of bytes with data
	 *           
	 * @return checksum of the array
	 * 
	 */
	private byte checksum(byte [] block) {
	   	int checksum=0;
	   	for(int i=0;i<7;i++) checksum=checksum+block[i];
	   	return (byte)(checksum & 0xFF);
	}

	/**
	 * Retrieves the timestamp from the array of bytes passed as paramter
	 * 
	 * @param block
	 *         array of bytes with data
	 *         
	 * @return the timestamp as String
	 */
	private String decodeTimestamp(byte [] block) {
		int month=(block[3]&0x01)*8+((block[2]&0xE0)>>5);
		// sometime the data is 13
		if(month==13) {
			return null; 
		}
		int year=((block[3]&0xFE)>>1);
		int day=block[2]&0x1F;
		int minute = block[4];
		int hour = block[5];
		Calendar cal = new GregorianCalendar(year+2000,month-1,day,hour,minute);
		return ""+cal.getTime().getTime();
	}

	
	/**
	 * Method that returns a timestamp of the current time
	 * 
	 * @return timestamp as String
	 */
	public static String getTimestamp() {
		Date d = new Date();
		return ""+d.getTime();
	}
	
	/**
	 * Starting from the current date, it creates the bytes' array useful
	 * to set the clock of the device
	 */
	public static byte [] getTimeToSet() {
		Calendar cal = Calendar.getInstance();
		byte [] result = new byte [4];
		result[3]=(byte) cal.get(Calendar.HOUR_OF_DAY);
		result[2]=(byte) cal.get(Calendar.MINUTE);
		result[1]=(byte) ((byte) (cal.get(Calendar.YEAR)-2000)<<1);
		int month = cal.get(Calendar.MONTH)+1;
		if(month>7)
			result[1]++;
		result[0]=(byte) (month<<5);
		result[0]+= cal.get(Calendar.DAY_OF_MONTH);
		return result;
	}
	
	
	/**
	 * Method that returns a string of hex values from a byteArray
	 * 
	 * @param raw 
	 *           byte array
	 *           
	 * @return hex String (null in case of errors
	 * 
	 */
	private String getHex( byte [] raw ) {
	    if ( raw == null ) {
	      return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * raw.length );
	    for ( final byte b : raw ) {
	      hex.append(HEXES.charAt((b & 0xF0) >> 4))
	         .append(HEXES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	}
}
