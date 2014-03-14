package it.ismb.pertlab.pwal.api;

public interface DeviceListener {
	
	void notifyDeviceAdded(Device newDevice);
	void notifyDeviceRemoved(Device removedDevice);
}
