package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface Accelerometer extends Device{
	
	Double getXAcceleration();
	Double getYAcceleration();
	Double getZAcceleration();
}
