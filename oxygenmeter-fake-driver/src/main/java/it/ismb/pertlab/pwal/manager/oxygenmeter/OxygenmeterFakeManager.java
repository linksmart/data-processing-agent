package it.ismb.pertlab.pwal.manager.oxygenmeter;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.manager.oxygenmeter.device.OxygenmeterFakeDevice;

public class OxygenmeterFakeManager extends DevicesManager {

	@Override
	public void run() {
		log.info("Oxygen meter manager started");
		while(!t.isInterrupted())
		{
			try {
				OxygenmeterFakeDevice device = new OxygenmeterFakeDevice();
				this.devicesDiscovered.put(this.generateId(), device);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceAdded(device);
				}
				Thread.sleep(5000);
				this.devicesDiscovered.remove(device);
				for (DeviceListener l : this.deviceListener) {
					l.notifyDeviceRemoved(device);
				}
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}
	}
}
