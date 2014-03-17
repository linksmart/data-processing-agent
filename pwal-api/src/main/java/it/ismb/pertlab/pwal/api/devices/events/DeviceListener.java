package it.ismb.pertlab.pwal.api.devices.events;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface DeviceListener {
	
	void notifyDeviceAdded(Device newDevice);
	void notifyDeviceRemoved(Device removedDevice);
}
