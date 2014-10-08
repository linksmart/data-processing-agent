package it.ismb.pertlab.pwal.api.devices.events;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

/***
 * This interface can be used to register events coming from the PWAL
 * @author Giampiero
 *
 */

public interface PWALDeviceListener {

	void notifyPWALDeviceAdded(Device newDevice);
	void notifyPWALDeviceRemoved(Device removedDevice);
}
