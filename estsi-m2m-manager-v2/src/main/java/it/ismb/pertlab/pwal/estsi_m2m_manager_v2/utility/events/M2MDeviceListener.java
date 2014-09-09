package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.utility.events;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface M2MDeviceListener {

	void notifyM2MDeviceAdded(Device newDevice);
	void notifyM2MDeviceRemoved(Device removedDevice);
}
