package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#SingleTemperatureSensor")
public interface Thermometer extends Device{

	/**
	 * Provides the temperature measured by this thermometer
	 */
	@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState")
	Double getTemperature();
}
