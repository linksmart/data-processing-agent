package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#LightSensor")
public interface LightSensor extends Device{

	Double getLight();
}
