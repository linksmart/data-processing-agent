/**
 * 
 */
package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

/**
 * @author bonino
 *
 */
public interface WasteBin extends Device
{
	public double getTemperature();
	public int getFillLevel();	
}
