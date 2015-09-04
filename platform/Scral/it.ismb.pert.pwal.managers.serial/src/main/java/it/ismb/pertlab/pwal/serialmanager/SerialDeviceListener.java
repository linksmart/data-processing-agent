package it.ismb.pertlab.pwal.serialmanager;

public interface SerialDeviceListener {

	void messageReceived(String payload);
}
