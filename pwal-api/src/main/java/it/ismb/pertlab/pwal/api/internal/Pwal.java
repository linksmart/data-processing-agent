package it.ismb.pertlab.pwal.api.internal;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

import java.util.Collection;
import java.util.HashMap;

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
	HashMap<String, Device> getDevicesMap();
	
	/**
	 * Provides devices of a given type (e.g. pwal:Temperature)
	 * @param type
	 * @return
	 */
	Collection<Device> getDevicesByType(String type);
}
