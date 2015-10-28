package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

/**
 * 
 * Interface for the sensors that measure the pressure
 *
 */
@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#PressureSensor")
public interface PressureSensor extends Device{

	/**
	 * Returns the last level of pressure measured by the device
	 * 
	 * @return the last value measured
	 * 
	 */
	Double getPressure();
}
