package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public interface Semaphore extends Device{

	public enum State { GREEN, YELLOW, RED}
	
	State getState();
	void setState(State state);
}
