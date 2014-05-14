package it.ismb.pertlab.pwal.api.devices.interfaces;

public interface Device {
	
	/**
	 * The identification given by the PWAL
	 */
	String getPwalId();
	/**
	 * The identification given by the PWAL
	 */
	void setPwalId(String pwalId);
	/**
	 * The identification inside the PWAL manager
	 */
	String getId();
	/**
	 * The identification inside the PWAL manager
	 */
	void setId(String id);
	/**
	 * Points the device taxonomy (es. Thermometer)
	 */
	String getType();
	/***
	 * Sensors position details
	 */
	Double getLatitude();
	void setLatitude(Double latitude);
	
	Double getLongitude();
	void setLongitude(Double longitude);
	/***
	 * Information about sensor's network
	 */
	String getNetworkType();
}
