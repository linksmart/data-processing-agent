package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

/**
 * 
 * Interface for the sensors that measure the pressure
 *
 */
public interface PressureSensor extends Device{

	/**
	 * Returns the last level of pressure measured by the device
	 * 
	 * @return the last value measured
	 * 
	 */
	Double getPressure();
}
