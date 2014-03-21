package it.ismb.pertlab.pwal.manager.thermometer.device;

import javax.bluetooth.RemoteDevice;

public class SerialPortProfileDevice {

	private RemoteDevice btRemoteDevice;
	private String connectionUrl;
	
	public SerialPortProfileDevice(RemoteDevice btDevice) {
		this.btRemoteDevice=btDevice;
	}
	public RemoteDevice getBtRemoteDevice() {
		return btRemoteDevice;
	}
	public void setBtRemoteDevice(RemoteDevice btRemoteDevice) {
		this.btRemoteDevice = btRemoteDevice;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
}
