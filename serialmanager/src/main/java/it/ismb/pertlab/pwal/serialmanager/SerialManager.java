package it.ismb.pertlab.pwal.serialmanager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Each sensor using this manager must send a message 
 *
 */
public class SerialManager extends DevicesManager implements SerialPortEventListener{

	//Map< port string, port object>
	private Map<String,SerialPort> ports=new HashMap<>();
	//Map< port string, queue>
	private Map<String, ArrayBlockingQueue<Byte>> queue=new HashMap<>();
	//Map< port string, queue thread>
	private Map<String, MessageQueue> queueThreads=new HashMap<>();
	//Map< port, lista di id >
	private Map<String, List<String>> idsPort;
	//Map< device id, object class name>
	private Map<String,String> configuredDevices;
	
	//portString is a comma separated string containing the list of ports to be opened
	public SerialManager(String portStrings, Map<String,String> props)
	{
		String[] ports=portStrings.split(",");
		idsPort=new HashMap<>();
		for(String port:ports)
		{
//			idsPort.put(port.trim(), new LinkedList<String>());
			idsPort.put(port.trim(), Arrays.asList(props.keySet().toArray(new String[]{})));

			queue.put(port, new ArrayBlockingQueue<Byte>(2048));
			queueThreads.put(port, new MessageQueue(queue.get(port), this));
			queueThreads.get(port).start();
		}
		configuredDevices=props;
		
	}
	
	@Override
	public void run() {
		discoverDevice();
		for(String port:idsPort.keySet())
		{
			try {
				ports.put(port, new SerialPort(port));
				SerialPort portObject=ports.get(port);
				portObject.openPort();
		    	portObject.setParams(9600,
		    		    SerialPort.DATABITS_8,
		    		    SerialPort.STOPBITS_1,
		    		    SerialPort.PARITY_NONE);
		    	portObject.addEventListener(this);
		    	
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		synchronized (this) {
	    	try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
	}
	
	private void discoverDevice()
	{
    	for(String deviceId:configuredDevices.keySet())
    	{
    		try {
	    		String className=configuredDevices.get(deviceId);
				//se non ho configurato il tipo di device nelle properties non fa nulla
				if(className==null || className.length()==0)
				{
					return;
				}
				
				Class<?> c = Class.forName(className);
			
				Constructor<?> constructor=c.getConstructor(SerialManager.class);
				Device d=(Device) constructor.newInstance(this);
				d.setId(deviceId);
				devicesDiscovered.put(deviceId, d);
				for(DeviceListener l:deviceListener)
				{
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						log.error("Exception: ", e);
					}
					l.notifyDeviceAdded(d);
				}
				log.debug("Device discovered: id="+deviceId);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
	}
	
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		
		if(arg0.isRXCHAR()){//If data is available
			try {
				String justread=ports.get(arg0.getPortName()).readString();
				if(justread==null)
				{
					return;
				}
				byte[] bl=justread.getBytes();
				for(int i=0; i<bl.length; i++)
				{
					//log.debug("put message in the queue: "+(char)bl[i]);
					queueThreads.get(arg0.getPortName()).put(bl[i]);
				}
            }
            catch (SerialPortException ex) {
            	System.out.println(ex);
            }
        }else if(arg0.isCTS()){//If CTS line has changed state
            if(arg0.getEventValue() == 1){//If line is ON
                System.out.println("CTS - ON");
            }
            else {
                System.out.println("CTS - OFF");
            }
        }
        else if(arg0.isDSR()){///If DSR line has changed state
            if(arg0.getEventValue() == 1){//If line is ON
                System.out.println("DSR - ON");
            }
            else {
                System.out.println("DSR - OFF");
            }
        }
	}
	
	public void dispatchMessage(String cs)
	{
		try{
			log.debug("going to dispatch message: "+cs);
			String[] data=cs.split(" ");
			if(data.length<2)
			{
				return;
			}
			BaseSerialDevice d=(BaseSerialDevice) devicesDiscovered.get(data[0]);
			if(d==null)
			{
				return;
			}
			String payload="";
			for(int i=1; i<data.length; i++)
				payload+=data[i];
			d.messageReceived(payload);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void sendCommand(String deviceId, String string) {
		try {
			//get all the port
			for(String port:idsPort.keySet())
			{
				List<String> ids=idsPort.get(port);
				for(String id:ids)
				{
					if(id.equals(deviceId))
					{
						ports.get(port).writeString(string);
						break;
					}
				}
			}
			
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.SERIAL;
	}

}
