package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DevicesManager implements Runnable{
	
	protected List<DeviceListener> deviceListener=new LinkedList<>();
	protected Thread t;
	protected String id;
	protected static final Logger log=LoggerFactory.getLogger(DevicesManager.class);
	protected HashMap<String, Device> devicesDiscovered = new HashMap<>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void start(){
		t=new Thread(this);
		t.start();
	}
	
	public void stop(){
		t.interrupt();
	}
	
	public void addDeviceListener(DeviceListener l) {
		deviceListener.add(l);
	}
	
	public void removeDeviceListener(DeviceListener l) {
		deviceListener.remove(l);
	}
	
	protected String generateId()
	{
		return UUID.randomUUID().toString();
	}
	
	public abstract String getNetworkType();
}
