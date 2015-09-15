package it.ismb.pertlab.pwal.api.devices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.ismb.pertlab.pwal.api.devices.interfaces.ControllableDevice;

/**
 * Interface for a meter
 * 
 */
public interface Meter extends ControllableDevice{
	/**
	 * Turns the meter on
	 */
	void turnOn();
	
	/**
	 * Turns the meter of
	 */
	void turnOff();
	
	/**
	 * Returns the value of power measured by the meter
	 * 
	 * @return power measured
	 */
	Double getPower();
	
	/**
	 * Returns true if is on, false instead
	 */
    @JsonProperty("isOn")
	Boolean isOn();
}
