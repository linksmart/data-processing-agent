package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.ControllableDevice;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#Pump")
public interface WaterPump extends ControllableDevice{
	void setSpeed(Double value);
	Double getSpeed();
}
