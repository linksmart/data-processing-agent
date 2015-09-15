package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.ControllableDevice;

public interface FakeControllableThermometer extends ControllableDevice, Thermometer {

	void setTemperature(Double temperature);
}
