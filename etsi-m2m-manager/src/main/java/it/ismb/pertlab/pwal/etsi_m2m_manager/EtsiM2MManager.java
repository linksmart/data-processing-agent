package it.ismb.pertlab.pwal.etsi_m2m_manager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.M2MUtility;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.events.M2MDeviceListener;

import java.util.ArrayList;
import java.util.List;

public class EtsiM2MManager extends DevicesManager implements M2MDeviceListener
{
	private M2MUtility m2mUtility;
	
	public EtsiM2MManager(String baseUrl)
	{
		this.m2mUtility = new M2MUtility(baseUrl, null);
		this.m2mUtility.addM2MEventListener(this);
	}
	
	public void run() {
		log.info("M2M manager started.");
		this.m2mUtility.exploreM2MResourcesTree();
		while(!t.isInterrupted())
		{
			
		}
		for(List<Device> ld : this.devicesDiscovered.values()){
			for (Device d : ld) {
				for (DeviceListener dl : this.deviceListener) {
					dl.notifyDeviceRemoved(d);
				}
			}
		}
		this.devicesDiscovered.clear();
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.M2M;
	}

	@Override
	public void notifyM2MDeviceAdded(Device newDevice) {
		if(this.devicesDiscovered.containsKey(newDevice.getId()))
			this.devicesDiscovered.get(newDevice.getId()).add(newDevice);
		else
		{
			List<Device> ld = new ArrayList<>();
			ld.add(newDevice);
			this.devicesDiscovered.put(newDevice.getId(), ld);
		}
		for(DeviceListener l:deviceListener)
		{
			log.info("New M2M device discovered. Generating event.");
			l.notifyDeviceAdded(newDevice);
		}
	}

	@Override
	public void notifyM2MDeviceRemoved(Device removedDevice) {
		this.devicesDiscovered.remove(removedDevice);		
	}
}