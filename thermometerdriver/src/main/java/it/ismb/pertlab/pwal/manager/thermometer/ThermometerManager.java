package it.ismb.pertlab.pwal.manager.thermometer;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.manager.thermometer.device.ThermometerDevice;

/**
 * 
 * Devices manager for the TaiDoc thermometer
 *
 */
public class ThermometerManager extends DevicesManager {

	private final static String MAC_ADDRESS = "";
	
	private boolean running = false;

	
	@Override
	public void run() {
		log.info("Themometer meter manager started");
		running = true;
		while(!t.isInterrupted())
		{
			try {
				StreamConnection conn = connectToDevice();
				ThermometerDevice device = new ThermometerDevice(this, conn.openDataOutputStream(), conn.openDataInputStream());
				this.devicesDiscovered.put(this.generateId(), device);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceAdded(device);
				}
				synchronized(this) {
					this.wait();
				}
				try {
					conn.close();
				} catch (IOException e) {
					log.error("Error closing the connection.", e);
		    	}
				this.devicesDiscovered.remove(device);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceRemoved(device);
				}
			} catch (InterruptedException e) {
				t.interrupt();
			} catch (IOException e) {
				log.error("error opening the connected bluetooth device ", e);
			}
		}
		running = false;
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
}
