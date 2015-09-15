package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface DistanceSensor extends Device{

	Double getDistanceCm();
	Double getDistanceInch();
	
}
