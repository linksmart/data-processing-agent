package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.enums.DeviceManagerStatus;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DevicesManager implements Runnable {
	
	protected List<DeviceListener> deviceListener=new LinkedList<>();
	protected Thread t;
	protected String id;
	protected static final Logger log=LoggerFactory.getLogger(DevicesManager.class);
	protected HashMap<String, List<Device>> devicesDiscovered = new HashMap<>();
	protected DeviceManagerStatus status = DeviceManagerStatus.STOPPED;
	
	public DeviceManagerStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceManagerStatus status)
	{
		this.status = status;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = this.generateId(id);
	}

	public void start(){
		switch (this.status) {
		case STARTED:
			log.info("Trying to start device manager {}, but it is already STARTED.", this.getId());
			break;
		case STOPPED:
			log.info("Starting device manager: {}", this.id);
			t = new Thread(this);
			this.status = DeviceManagerStatus.STARTED;
			t.start();
			break;
		default:
			log.error("Error! Trying to start device manager {}, but it is in an unknown state.");
			break;
		}
	}
	
	public void stop(){
		switch (this.status) {
		case STARTED:
			log.info("Stopping device manager: {}", this.id);
			this.status = DeviceManagerStatus.STOPPED;
			for (List<Device> ld : this.devicesDiscovered.values()) {
				for(Device d : ld)
				{
					for (DeviceListener l : this.deviceListener) {
						l.notifyDeviceRemoved(d);
					}
				}
			}
			this.devicesDiscovered.clear();
			t.interrupt();
			break;
		case STOPPED:
			log.info("Trying to stop device manager {}, but it is already STOPPED.", this.getId());;
			break;
		default:
			log.error("Error! Trying to stop device manager {}, but it is in an unknown state.");
			break;
		}
	}
	
	public void addDeviceListener(DeviceListener l) {
		deviceListener.add(l);
	}
	
	public void removeDeviceListener(DeviceListener l) {
		deviceListener.remove(l);
	}
	
	@Deprecated
	protected String generateId(String deviceID)
	{
		return UUID.randomUUID().toString();
	}
	
//	protected abstract void setNetworkType(DeviceNetworkType networkType);
	
	public abstract String getNetworkType();
}
