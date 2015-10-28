package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface VehicleCounter extends Device {
	
	Double getOccupancy();
	Double getCount();
}
