package it.ismb.pertlab.pwal.wsn.driver;

public abstract class WSNBaseDevice {
	
	private WSNManager manager;
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public abstract void notifyMessage(byte[] payload);

	public WSNManager getManager() {
		return manager;
	}

	public void setManager(WSNManager manager) {
		this.manager = manager;
	}

}
