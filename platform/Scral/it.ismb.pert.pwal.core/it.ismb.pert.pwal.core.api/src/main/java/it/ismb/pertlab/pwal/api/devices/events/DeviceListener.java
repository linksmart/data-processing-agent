package it.ismb.pertlab.pwal.api.devices.events;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

/***
 * This interface includes methods to listen to events coming from devices networks
 * @author Giampiero
 *
 */
public interface DeviceListener {
	
	void notifyDeviceAdded(Device newDevice);
	void notifyDeviceRemoved(Device removedDevice);
}
