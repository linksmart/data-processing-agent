package it.ismb.pertlab.pwal.manager.thermometer;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.manager.thermometer.device.ThermometerFakeDevice;

public class ThermometerFakeManager extends DevicesManager{

	private String id;

	@Override
	public void run() {
		log.info("Thermometer manager stared.");
		while(!t.isInterrupted())
		{	
			try {
				ThermometerFakeDevice term = new ThermometerFakeDevice();
				this.devicesDiscovered.put(this.generateId(), term);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceAdded(term);
				}
				
				Thread.sleep(5000);
				this.devicesDiscovered.remove(term);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceRemoved(term);
				}
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				t.interrupt();
			}
		}
	}

	public void setId(String id)
	{
		this.id=id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
}
