package it.ismb.pertlab.pwal.manager.thermometer;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.manager.thermometer.device.SerialPortProfileDevice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.intel.bluetooth.RemoteDeviceHelper;

/**
 * 
 * Devices manager for the TaiDoc thermometer
 *
 */
public class ThermometerManager extends DevicesManager implements DiscoveryListener {

	private final static String MAC_ADDRESS = "00123EFF29DB";
	
	private boolean running = false;

	private Object discoveryLock=new Object();
	//<mac,device>
	private HashMap<String,SerialPortProfileDevice> devices=new HashMap<>();
	private Map<String,String> prop;
	private Object servicesSearchLock=new Object();
	
	public ThermometerManager(Map<String,String> props){
		prop=props;
	}
	
	@Override
	public void run() {
		try {
			discover();
			searchServices();
		} catch (Exception e) {
			log.error("Error:",e);
		}
//		log.info("Thermometer manager started");
//		running = true;
//		while(!t.isInterrupted())
//		{
//			try {
//				StreamConnection conn = connectToDevice();
//				ThermometerDevice device = new ThermometerDevice(this, conn.openDataOutputStream(), conn.openDataInputStream());
//				this.devicesDiscovered.put(this.generateId(), device);
//				for (DeviceListener l : this.deviceListener) {
//					l.notifyDeviceAdded(device);
//				}
//				synchronized(this) {
//					this.wait();
//				}
//				try {
//					conn.close();
//				} catch (IOException e) {
//					log.error("Error closing the connection.", e);
//		    	}
//				this.devicesDiscovered.remove(device);
//				for (DeviceListener l : this.deviceListener) {
//					l.notifyDeviceRemoved(device);
//				}
//			} catch (InterruptedException e) {
//				t.interrupt();
//			} catch (IOException e) {
//				log.error("error opening the connected bluetooth device ", e);
//			}
//		}
//		running = false;
	}
	
	private void searchServices() throws IOException, InterruptedException {
		LocalDevice ld=LocalDevice.getLocalDevice();
		DiscoveryAgent da=ld.getDiscoveryAgent();
		String therm="1809";
		String generic="180A";
		String serial="1101";
		UUID serialPortUUID=new UUID(serial,true);
		for(SerialPortProfileDevice sppd:devices.values())
		{
			RemoteDevice d=sppd.getBtRemoteDevice();
			log.debug("Searching services for the device "+d.getBluetoothAddress());
			da.searchServices(null,new UUID[]{serialPortUUID}, d, this);
			synchronized (servicesSearchLock) {
				servicesSearchLock.wait();
			}
		}
		log.debug("Searching services completed");
	}

	private void discover() throws BluetoothStateException, InterruptedException
	{
		LocalDevice ld=LocalDevice.getLocalDevice();
		DiscoveryAgent da=ld.getDiscoveryAgent();
		da.startInquiry(DiscoveryAgent.GIAC, this);
		log.debug("Starting discovery...");
		synchronized (discoveryLock) {
			discoveryLock.wait();
		}
		log.debug("Discovery ends");
	}
	
	
	/**
	 * Method used to connect to the device
	 * 
	 * @return connection open;
	 */
	private StreamConnection connectToDevice() {
		boolean error;
		StreamConnection conn = null;
		do {
			error=false;
			try {
				// Connects to the thermometer
				conn = (StreamConnection)Connector.open("btspp://"+MAC_ADDRESS+":1;");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {	}
			} catch (IOException e) {
				// The device will not be always discoverable
				// it is available only when a measure is done
				error=true;
			}
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e1) {	}		
		} while (error && running);
		return conn;
	}

	@Override
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		try {
			log.debug("Device discovered: name="+btDevice.getFriendlyName(false)+" address="+btDevice.getBluetoothAddress()+" class="+cod.getServiceClasses());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		devices.put(btDevice.getBluetoothAddress(),new SerialPortProfileDevice(btDevice));
	}

	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for(ServiceRecord r:servRecord)
		{
			try {
				SerialPortProfileDevice device=devices.get(r.getHostDevice().getBluetoothAddress());
				device.setConnectionUrl(r.getConnectionURL(ServiceRecord.AUTHENTICATE_NOENCRYPT, false));
				if(!device.getBtRemoteDevice().isAuthenticated()){
					log.info("Device requires authentication");
//					RemoteDeviceHelper.authenticate(device.getBtRemoteDevice(), "111111");
//					while(!device.getBtRemoteDevice().isAuthenticated());
//					log.info("Device authenticated");
				}
				
				
				String className=prop.get(device.getBtRemoteDevice().getBluetoothAddress());
				//se non ho configurato il tipo di device nelle properties non fa nulla
				if(className==null || className.length()==0)
				{
					return;
				}
				
				Class<?> c=Class.forName(className);
				Constructor<?> constructor=c.getConstructor(String.class);
				Device d=(Device) constructor.newInstance(device.getConnectionUrl());
				
				devicesDiscovered.put(device.getBtRemoteDevice().getBluetoothAddress(), d);
				for(DeviceListener l:deviceListener)
				{
					l.notifyDeviceAdded(d);
				}
				
				log.debug("Services discovered: device="+r.getHostDevice().getBluetoothAddress()+" connectionUrl="+device.getConnectionUrl());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void serviceSearchCompleted(int transID, int respCode) {
		synchronized (servicesSearchLock) {
			servicesSearchLock.notify();
		}
	}

	@Override
	public void inquiryCompleted(int discType) {
		synchronized (discoveryLock) {
			discoveryLock.notify();
		}
	}
}
