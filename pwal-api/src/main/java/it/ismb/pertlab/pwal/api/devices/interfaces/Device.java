package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;

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
	/**
	 * Returns the time when the current value has been
	 * updated the last time
	 * 
	 * @return last update
	 */
	String getUpdatedAt();
	/**
	 * Sets the last time when the current value has been updated
	 * 
	 * @param updatedAt
	 *             time to set
	 */
	void setUpdatedAt(String updatedAt);
	/**
	 * Returns the location of the device
	 * 
	 * @return the location of the device
	 */
	Location getLocation();
	/**
	 * Sets the location of the device
	 * 
	 * @param location
	 *             location to set
	 */
	void setLocation(Location location);
	/**
	 * Returns the unit of the measure done by the device
	 * 
	 * @return the unit of the measure
	 */
	Unit getUnit();
	/**
	 * Sets the unit of the measure done by the device
	 * 
	 * @param 
	 * 				the unit to set
	 */
	void setUnit(Unit unit);
}