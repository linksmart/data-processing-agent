package it.ismb.pertlab.pwal.api;

import java.util.LinkedList;
import java.util.List;

public abstract class Driver implements Runnable{
	
	private List<DeviceListener> deviceListener=new LinkedList<>();
	protected Thread t;
	
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

}
