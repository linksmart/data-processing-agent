package it.ismb.pertlab.pwal.wsn.driver;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.wsn.driver.api.IMessage;
import it.ismb.pertlab.pwal.wsn.driver.api.Response;
import it.ismb.pertlab.pwal.wsn.driver.customUDP.Definitions;
import it.ismb.pertlab.pwal.wsn.driver.impl.Queues;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Queue;

public class WSNManager extends DevicesManager{
	Queues queues = Queues.getInstance();
	Queue<IMessage> IQueue = queues.getIQueue();
	
	private int port = 7730;
	byte [] sendData 	= new byte [12];
	byte [] receiveData = new byte [12];
	DatagramSocket udplistener = null;
	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	
	@Override
	public void run() {
		
		try {
			udplistener = new DatagramSocket(port);
			log.info("UDP Listener Started at Port[ "+ port +" ]");
			
			while(!t.isInterrupted())
			{
				udplistener.receive(receivePacket);
				log.info("MSG received from "+receivePacket.getAddress()+" payload="+bytesToHex( receivePacket.getData() ));
				IMessage m_data = new Response(receivePacket.getAddress().getHostAddress());
				byte [] payload = Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				m_data.setPayload(payload);
				
				switch( payload[0] )
				{
				
					case Definitions.REQ_DISC:
						log.debug("Received a REQ_DISC from "+ m_data.getSrcAddress()+" payload: "+	bytesToHex( payload ) );
						//send back an ack
						sendMessage(Definitions.ACK_DISC_PACK, m_data.getSrcAddress());
						//I will receive an ACK_DISC_ACK
						log.debug("Sent ACT_DISC to "+m_data.getSrcAddress()+" payload: "+bytesToHex( Definitions.ACK_DISC_PACK ));
						break;
					case Definitions.ACK_DISC_ACK:
						log.debug("Received ACK_DISC_ACK from "+m_data.getSrcAddress()+ " payload: "+ bytesToHex( payload ));
						//IQueue.add(m_data);
						//send a request for a sensors type list
						sendMessage(new byte[]{Definitions.REQ_SENSORS,0x00,0x00,0x00}, m_data.getSrcAddress());
						log.debug("Sent REQ_SENSORS to "+m_data.getSrcAddress()+" payload "+bytesToHex(new byte[]{Definitions.REQ_SENSORS,0x00,0x00,0x00}));
						break;
					case Definitions.REQ_SENSORS:
						log.debug("Received REQ_SENSORS from "+m_data.getSrcAddress()+" payload: "+bytesToHex(payload));
						registerSensorsType(m_data);
						break;
					default:
						log.info("Trying to dispatch the message to the proper sensor");
						log.debug("Received payload: ["+	bytesToHex( payload ) +"]");
						dispatchMessage(m_data);
						break;
//					case Definitions.SENSOR_TEMP:
//						log.info("Received SENSOR_TEMP");
//						log.debug("payload ["+ bytesToHex( payload ) +"]");
//						int temperature =  getInt( receivePacket.getData(), 2, receivePacket.getLength() ); // big-endian by default
//						log.debug("Temperature is ["+temperature +"]");
//						break;
//					case Definitions.SENSOR_ACCEL:
//						log.info("Received SENSOR_ACCEL");
//						log.debug("Received Accelometer ["+	bytesToHex( payload ) +"]");
//						int accel =  getInt( receivePacket.getData(), 2, receivePacket.getLength() );
//						log.debug("Acceleration is  ["+accel+"]");
//						break;		
				}
			}
		} catch (Exception e) {
			log.error("Exception received: ",e);
		}
	}
	
	/**
	 * Process the response to a REQ_SENSORS from which get a list of the sensors
	 * configured in for a certain node of the WSN.
	 * This method is responsible to parse the response, create the pwal sensors
	 * and notify the discovery to the pwal. 
	 * 
	 * If a new type of sensor wants to be added to the WSN managed by this Manager,
	 * it is needed to create a new case in the main switch for parsing the new type.
	 */
	private void registerSensorsType(IMessage msg) throws Exception
	{
		byte[] response=msg.getPayload();
		//byte[0]->id richiesta
		//byte[1]->num sens presenti
		//byte[n>1]->sens
		for(int i=2; i<(int)response[1]+2;i++)
		{
			switch(response[i]){
				case Definitions.SENSOR_TEMP:
					WSNTemperatureSensor ts=new WSNTemperatureSensor();
					ts.setId(super.generateId());
					ts.setAddress(msg.getSrcAddress());
					ts.setManager(this);
					for(DeviceListener l:deviceListener)
					{
						l.notifyDeviceAdded(ts);
					}
					devicesDiscovered.put(ts.getId(), ts);
					break;
				case Definitions.SENSOR_ACCEL:
					WSNAccelerometerSensor as=new WSNAccelerometerSensor();
					as.setId(super.generateId());
					as.setAddress(msg.getSrcAddress());
					as.setManager(this);
					for(DeviceListener l:deviceListener)
					{
						l.notifyDeviceAdded(as);
					}					
					devicesDiscovered.put(as.getId(), as);
					break;
				case Definitions.SENSOR_LIGHT:
					break;
				case Definitions.SENSOR_HUMIDITY:
					break;
				default:
					break;
				
			}
		}
		
	}
	
	public void sendMessage(byte[] data, String address)
	{
		DatagramPacket req_sensors;
		try {
			req_sensors = new DatagramPacket(data, 4, InetAddress.getByName(address), 7731);
			udplistener.send(req_sensors);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void dispatchMessage(IMessage data)
	{
		//get the type of the sensor who has generated the response
		byte type=data.getPayload()[0];
		String pwalType=Definitions.getCorresponsidngDeviceType(type);

		//searching the correct sensor
		for(Device d:super.devicesDiscovered.values())
		{
			WSNBaseDevice bd=(WSNBaseDevice) d;
				
			log.debug("Searching the correct sensor for device "+bd.getAddress()+" and type "+pwalType);
			if(bd.getAddress().equals(data.getSrcAddress()) && d.getType().equals(pwalType))
			{
				log.debug("Device found id="+d.getId());
				bd.notifyMessage(data.getPayload());
			}
		}
	}
	
	private int getInt(byte [] payload, int start, int end){
		ByteBuffer buf =  ByteBuffer.wrap(Arrays.copyOfRange( payload,  start, end) ); // big-endian by default
		return buf.getInt();
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.SIXLOWPAN;
	}
}
