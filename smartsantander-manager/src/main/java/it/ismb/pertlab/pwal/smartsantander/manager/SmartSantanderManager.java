package it.ismb.pertlab.pwal.smartsantander.manager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderSingleNodeJson;
import it.ismb.pertlab.pwal.smartsantander.devices.SmartSantanderVehicleCounterDevice;
import it.ismb.pertlab.pwal.smartsantander.devices.SmartSantanderVehicleSpeedDevice;
import it.ismb.pertlab.pwal.smartsantander.devices.types.SmartSantaderDeviceTypes;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This component manages the SmartSantander sensors network
 *
 */
public class SmartSantanderManager extends DevicesManager
{
	SmartSantanderRestClient restClient = new SmartSantanderRestClient("http://data.smartsantander.eu/ISMB/", log);
	
	public void run() {
		while (!t.isInterrupted()) {
			log.info("Retrieving devices list from SmartSantander");
			List<SmartSantanderSingleNodeJson> availableNodes = restClient.getNodes();
			if(availableNodes != null && availableNodes.size() != 0)
			{
				log.info("Devices list size: {}.",availableNodes.size());
				//Check for devices removed...comparing devices discovered with the list returned by SmartSantander
				List<Device> toBeRemoved = new ArrayList<>();
				for (Device d : devicesDiscovered.values()) {
					Boolean found = false;
					for (SmartSantanderSingleNodeJson smartSantanderSingleNodeJson : availableNodes) {
						
						if(d.getId().equals(smartSantanderSingleNodeJson.getNodeId()))
						{
							found = true;
							continue;
						}
					}	
					if(!found)
					{
						log.info("Device {} seems to be removed. It is no more present in the retrieved devices list.", d);
						toBeRemoved.add(d);
						for (DeviceListener l : deviceListener) {
							l.notifyDeviceRemoved(d);
						}
					}
				}
				//Removing devices no more available. Separate cycle to avoid collection changing while ciclying on it
				log.info("Removing no more present devices...");
				if(toBeRemoved.size() != 0)
				{
					for (Device device : toBeRemoved) {
						log.debug("Removing {}.",device);
						this.devicesDiscovered.remove(device);
					}
				}
				else
					log.info("No device removed since last devices list request.");
				
				for (SmartSantanderSingleNodeJson smartSantanderSingleNodeJson : availableNodes) {
					if(!this.devicesDiscovered.containsKey(smartSantanderSingleNodeJson.getNodeId()))
					{
						switch (smartSantanderSingleNodeJson.getType()) {
						case SmartSantaderDeviceTypes.VEHICLE_COUNTER:
							SmartSantanderVehicleCounterDevice vehicleCounter = new SmartSantanderVehicleCounterDevice(this.restClient);
							vehicleCounter.setId(smartSantanderSingleNodeJson.getNodeId());
							vehicleCounter.setLatitude(smartSantanderSingleNodeJson.getLatitude());
							vehicleCounter.setLongitude(smartSantanderSingleNodeJson.getLongitude());
							this.devicesDiscovered.put(smartSantanderSingleNodeJson.getNodeId(), vehicleCounter);
							for (DeviceListener l : deviceListener) {
								l.notifyDeviceAdded(vehicleCounter);
							}
							break;
						case SmartSantaderDeviceTypes.VEHICLE_SPEED:
							SmartSantanderVehicleSpeedDevice vehicleSpeed = new SmartSantanderVehicleSpeedDevice(this.restClient);
							vehicleSpeed.setId(smartSantanderSingleNodeJson.getNodeId());
							vehicleSpeed.setLatitude(smartSantanderSingleNodeJson.getLatitude());
							vehicleSpeed.setLongitude(smartSantanderSingleNodeJson.getLongitude());
							devicesDiscovered.put(smartSantanderSingleNodeJson.getNodeId(), vehicleSpeed);
							for (DeviceListener l : deviceListener) {
								l.notifyDeviceAdded(vehicleSpeed);
							}
							break;
						default:
							log.error("I don't know the hell is {}. Unknown type, sorry.", smartSantanderSingleNodeJson.getType());
							break;
						}
					}
				}
			}
			else
			{
				log.info("Something goes wrong retrieving devices from SmartSantander");
			}
			try {
				//Ask for devices list again in 1 minute
				log.debug("I'm going to ask for devices nodes again in 1 minute.");
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				log.error("Exception: ", e);
			}
		}
	}
}
