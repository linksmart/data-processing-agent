package it.ismb.pertlab.pwal.serialmanager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

	private SerialPort port=null;
	private ArrayBlockingQueue<Byte> queue=new ArrayBlockingQueue<Byte>(2048);
	private String portString;
	private Map<String,String> configuredDevices;
	
	public SerialManager(String portString, Map<String,String> props)
	{
		this.portString=portString;
		configuredDevices=props;
	}
	
	@Override
	public void run() {
		discoverDevice();
		try {
			port= new SerialPort(portString);
			port.openPort();
	    	port.setParams(115200,
	    		    SerialPort.DATABITS_8,
	    		    SerialPort.STOPBITS_1,
	    		    SerialPort.PARITY_NONE);
	    	port.addEventListener(this);
	    	
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	StringBuffer ret=new StringBuffer();
    	
    	while(true)
    	{
    		try {
    			Byte b=queue.take();
				ret.append((char)b.byteValue());
				if(b.byteValue()=='\n')
				{
					dispatchMessage(ret.toString());
					ret=new StringBuffer();
				}
				
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
				String justread=port.readString();
				if(justread==null)
				{
					return;
				}
				byte[] bl=justread.getBytes();
				for(int i=0; i<bl.length; i++)
				{
					//log.debug("put message in the queue: "+(char)bl[i]);
					queue.put(bl[i]);
				}
            }
            catch (SerialPortException | InterruptedException ex) {
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
	
	private void dispatchMessage(String cs)
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

	public void sendCommand(String string) {
		try {
			port.writeString(string);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
