package it.ismb.pertlab.pwal.api;

public interface Thermometer extends Device{

	/**
	 * Provides the temperature measured by this thermometer
	 */
	Double getTemperature();
}
