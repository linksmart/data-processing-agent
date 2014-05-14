package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.enums.DeviceManagerStatus;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PwalImpl implements Pwal, DeviceListener {

	private List<Device> devicesList;
	private HashMap<String, DevicesManager> devicesManagers;
	private List<PWALDeviceListener> pwalDeviceListeners;

	private static final Logger log=LoggerFactory.getLogger(PwalImpl.class);

	
	public PwalImpl(List<DevicesManager> devicesManager)
	{
		this.devicesList= new ArrayList<>();
		this.pwalDeviceListeners = new ArrayList<>();
		this.devicesManagers = new HashMap<>();
		for(DevicesManager d : devicesManager)
		{
			d.setId(this.generateId());
			d.setStatus(DeviceManagerStatus.STOPPED);
			this.devicesManagers.put(d.getId(), d);
			d.addDeviceListener(this);
//			d.start();
//			driver.start();
			this.startDeviceManager(d.getId());
		}
	}
	
	public Device getDevice(String id) {
		for (Device d : this.devicesList) {
			if(d.getPwalId().equals(id))
				return d;
		}
		return null;
	}

	private String generateId()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	public void notifyDeviceAdded(Device newDevice) {
		String generatedId = this.generateId();
		newDevice.setPwalId(generatedId);
		log.info("New PWAL device added: generated id {} type {}.", generatedId, newDevice.getType());
		this.devicesList.add(newDevice);
	}

	@Override
	public void notifyDeviceRemoved(Device removedDevice) {
		int index = 0;
		for (Device d : this.devicesList) {
			if(d.getPwalId().equals(removedDevice.getPwalId()))
			{
					this.devicesList.remove(index);
					log.info("New PWAL device removed: id {} type {}.", removedDevice.getPwalId(), removedDevice.getType());
					break;
			}
			index++;
		}
	}

	@Override
	public Collection<Device> getDevicesByType(String type) {
		List<Device> result = new LinkedList<>();
		if(type==null || type.length()==0)
		{
			return result;
		}
				for(Device d:this.devicesList)
		{
			if(type.equals(d.getType()))
			{
				result.add(d);
			}
		}
		return result;
	}

	@Override
	public Collection<Device> getDevicesList() {
		return this.devicesList;
	}

	@Override
	public Collection<DevicesManager> getDevicesManagerList() {
		return this.devicesManagers.values();
	}

	@Override
	public Boolean startDeviceManager(String deviceManagerName) {
		this.devicesManagers.get(deviceManagerName).start();
		if(this.devicesManagers.get(deviceManagerName).getStatus().equals(DeviceManagerStatus.STARTED))
		{
			log.debug("Device manager {} successfully started", deviceManagerName);
			return true;
		}
		log.error("Device manager {} has not been started", deviceManagerName);
		return false;
	}

	@Override
	public Boolean stopDeviceManager(String deviceManagerName) {
		this.devicesManagers.get(deviceManagerName).stop();
		if(this.devicesManagers.get(deviceManagerName).getStatus().equals(DeviceManagerStatus.STOPPED))
		{
			log.debug("Device manager {} successfully stopped", deviceManagerName);
			return true;
		}
		log.error("Device manager {} has not been stopped", deviceManagerName);
		return false;
	}

	@Override
	public void addPwalDeviceListener(PWALDeviceListener listener) {
		this.pwalDeviceListeners.add(listener);
	}

	@Override
	public void removePwalDeviceListener(PWALDeviceListener listener) {
		this.pwalDeviceListeners.remove(listener);
	}
}
