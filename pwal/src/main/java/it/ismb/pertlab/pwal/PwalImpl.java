package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.Device;
import it.ismb.pertlab.pwal.api.Driver;
import it.ismb.pertlab.pwal.api.Pwal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PwalImpl implements Pwal{

	private HashMap<String, Device> devices;
	
	public PwalImpl(List<Device> devices)
	{
		this.devices=new HashMap<String,Device>();
		for(Device d:devices)
		{
			d.setId(generateId());
			this.devices.put(d.getId(), d);
			
			Driver driver=(Driver) d;
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

}
