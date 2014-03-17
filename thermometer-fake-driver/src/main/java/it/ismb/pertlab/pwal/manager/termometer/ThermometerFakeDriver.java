package it.ismb.pertlab.pwal.manager.termometer;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;

public class ThermometerFakeDriver extends DevicesManager implements Thermometer {

	private String id;
	private final String type="pwal:Thermometer";

	@Override
	public void run() {
		log.info("Thermometer manager stared.");
		while(!t.isInterrupted())
		{
				try {
				ThermometerFakeDriver term = new ThermometerFakeDriver();
				this.devicesDiscovered.put(this.generateId(), term);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceAdded(term);
				}
				
				Thread.sleep(5000);
				this.devicesDiscovered.remove(term);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceRemoved(term);
				}
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

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Double getTemperature() {
		return ((25-16)*Math.random())+16;
	}
	
}
