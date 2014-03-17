package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PwalImpl implements Pwal, DeviceListener {

	private HashMap<String, Device> devicesDrivers;
	private HashMap<String, DevicesManager> devicesManagers;

	private static final Logger log=LoggerFactory.getLogger(PwalImpl.class);

	
	public PwalImpl(List<DevicesManager> devices)
	{
		this.devicesDrivers=new HashMap<>();
		this.devicesManagers = new HashMap<>();
		for(DevicesManager d : devices)
		{
			d.setId(generateId());
			this.devicesManagers.put(d.getId(), d);
			d.addDeviceListener(this);
			DevicesManager driver=(DevicesManager) d;
			driver.start();
		}
	}
	
	public Device getDevice(String id) {
		return devicesDrivers.get(id);
	}

	public Collection<Device> listDevices() {
		return devicesDrivers.values();
	}
	
	private String generateId()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	public void notifyDeviceAdded(Device newDevice) {
		newDevice.setId(this.generateId());
		log.info("New PWAL device added: id {} type {}.", newDevice.getId(), newDevice.getType());
		this.devicesDrivers.put(newDevice.getId(), newDevice);
	}

	@Override
	public void notifyDeviceRemoved(Device removedDevice) {
		log.info("New PWAL device removed: id {} type {}.", removedDevice.getId(), removedDevice.getType());
		this.devicesDrivers.remove(removedDevice);
	}

}
