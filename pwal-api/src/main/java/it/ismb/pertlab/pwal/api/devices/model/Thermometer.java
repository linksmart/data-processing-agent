package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface Thermometer extends Device{

	/**
	 * Provides the temperature measured by this thermometer
	 */
	Double getTemperature();
}
