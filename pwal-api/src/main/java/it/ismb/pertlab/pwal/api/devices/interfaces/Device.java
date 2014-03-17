package it.ismb.pertlab.pwal.api.devices.interfaces;

public interface Device {
	
	/**
	 * The identification inside the PWAL
	 */
	String getId();
	/**
	 * The identification inside the PWAL
	 */
	void setId(String id);
	/**
	 * Points the device taxonomy (es. Thermometer)
	 */
	String getType();
}
