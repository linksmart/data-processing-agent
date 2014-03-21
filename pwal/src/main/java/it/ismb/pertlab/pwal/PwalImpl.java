package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PwalImpl implements Pwal, DeviceListener {

	private HashMap<String, Device> devices;
	private HashMap<String, DevicesManager> devicesManagers;

	private static final Logger log=LoggerFactory.getLogger(PwalImpl.class);

	
	public PwalImpl(List<DevicesManager> devices)
	{
		this.devices=new HashMap<>();
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
		return devices.get(id);
	}

	public Collection<Device> listDevices() {
		return devices.values();
	}
	
	private String generateId()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	public void notifyDeviceAdded(Device newDevice) {
//		newDevice.setId(this.generateId());
		log.info("New PWAL device added: id {} type {}.", newDevice.getId(), newDevice.getType());
		this.devices.put(newDevice.getId(), newDevice);
	}

	@Override
	public void notifyDeviceRemoved(Device removedDevice) {
		log.info("New PWAL device removed: id {} type {}.", removedDevice.getId(), removedDevice.getType());
		this.devices.remove(removedDevice);
	}

	@Override
	public Collection<Device> getDevicesByType(String type) {
		LinkedList<Device> res=new LinkedList<Device>();
		if(type==null || type.length()==0)
		{
			return res;
		}
		
		for(Device d:devices.values())
		{
			if(type.equals(d.getType()))
			{
				res.add(d);
			}
		}
		return res;
	}

}
