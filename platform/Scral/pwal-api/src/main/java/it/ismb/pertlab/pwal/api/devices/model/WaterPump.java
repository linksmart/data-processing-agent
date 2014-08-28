package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface WaterPump extends Device{
	void setVelocity(Double value);
}
