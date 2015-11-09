package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#OccupancySensor")
public interface VehicleSpeed extends Device {
	
	Double getOccupancy();
	Double getCount();
	Double getMedianSpeed();
	Double getAverageSpeed();
}