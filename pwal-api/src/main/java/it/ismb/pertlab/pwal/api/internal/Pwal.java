package it.ismb.pertlab.pwal.api.internal;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

import java.util.Collection;

public interface Pwal {

	/**
	 * Provides a subclass of Device (see the device taxonomy) for a given pwal ID
	 * 
	 * @param id - the pwal id of the device
	 * @return a subclass of Device (see the device taxonomy)
	 */
	Device getDevice(String id);
	/**
	 * Provides a collection containing all device configured.
	 * 
	 * @return a list of subclass of Device (see the device taxonomy) 
	 */
	Collection<Device> listDevices();
}
