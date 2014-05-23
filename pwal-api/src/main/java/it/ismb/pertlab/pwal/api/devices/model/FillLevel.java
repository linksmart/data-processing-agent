package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface FillLevel extends Device {

	Integer getDepth();
	Integer getLevel();
}
