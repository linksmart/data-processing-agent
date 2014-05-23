package it.ismb.pertlab.pwal.connectors.rest;

import it.ismb.pertlab.pwal.PwalImpl;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PwalRestConnector implements PWALDeviceListener {

	@Autowired
	private PwalImpl pwal;
	private static final Logger log=LoggerFactory.getLogger(PwalRestConnector.class);

	/**
	 * This resource retrieves the detailed list of the devices adapted by the pwal
	 * @return a json containing all the devices details
	 */
	@RequestMapping(value="devices", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Collection<Device> getAllDevicesList()
	{
		log.info("Returing all devices list");
		return pwal.getDevicesList();
	}
	
	/**
	 * This resource retrieves details about a specific device
	 * @param deviceId is the device id (the one assigned by the pwal) 
	 * @return a json containing device detail
	 */
	@RequestMapping(value="devices/{deviceId}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Device getDevice(@PathVariable String deviceId)
	{
		log.info("Returing device info. Requested id: {}", deviceId);
		for (Device d : pwal.getDevicesList()) {
			if(d.getPwalId().equals(deviceId))
				return d;
		}
		return null;
	}
	
	/**
	 * This resource filters device basing on the network and the device type
	 * @param networkType the network type to which the device belong
	 * @param deviceType the device type to which the device belong
	 * @return
	 */
	@RequestMapping(value="filterdevices", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Collection<Device> filterDevices(@RequestParam(value="networkType", required=false) String networkType, 
											  @RequestParam(value="deviceType", required=false) String deviceType)
	{
		log.info("Required devices filtering. Query string is: {} {}", networkType, deviceType);
		List<Device> result = new LinkedList<>();
		
		if((networkType != null && networkType.length() != 0) && 
		   (deviceType != null && deviceType.length() != 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getNetworkType().equals(networkType) && d.getType().equals(deviceType))
					result.add(d);
			}
		}
		if((networkType == null || networkType.length() == 0) &&
		   (deviceType != null && deviceType.length() != 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getType().equals(deviceType))
					result.add(d);
			}
		}
		if((networkType != null && networkType.length() != 0) &&
		   (deviceType == null || deviceType.length() == 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getNetworkType().equals(networkType))
					result.add(d);
			}
		}
		return result;
		
	}
	
	/**
	 * This resource retrieves device managers list
	 * @return a json with the list of all device managers available
	 */
	@RequestMapping(value="getalldevicemanagers", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Collection<DevicesManager> getDeviceManagersList()
	{
		log.info("Returing device managers list");
		return pwal.getDevicesManagerList();
	}

	@RequestMapping(value="setpumpspeed", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ResponseEntity<?> changeValues(@RequestParam(value = "deviceId") String deviceId, 
																 @RequestParam(value = "speed") String speed)
	{
		log.info("Searching for water pump");
		for (Device device : pwal.getDevicesList()) {
			if(device.getPwalId().equals(deviceId))
			{
				log.info("Water pump found!");
				((WaterPump)device).setVelocity(Double.parseDouble(speed));
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * This resource retrieves device manager details
	 * @param deviceManagerId is the id of the required device manager
	 * @return a json containing details about the specific device manager
	 */
	@RequestMapping(value="getadevicemanagers/{deviceManagerId}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody DevicesManager getDeviceManager(@PathVariable String deviceManagerId)
	{
		log.info("Returing device manager {} details.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if(dm.getId().equals(deviceManagerId))
				return dm;
		}
		return null;
	}
	
	/**
	 * This resource stop a specific device manager
	 * @param deviceManagerId is the id of the specific device manager to be stopped
	 * @return 200 OK if everything is correct, 400 BAD REQUEST if stop operation fails and 404 NOT FOUND if no device manager is found
	 * @return
	 */
	@RequestMapping(value="stopdevicemanager/{deviceManagerId}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ResponseEntity<?> stopDeviceManager(@PathVariable String deviceManagerId)
	{
		log.info("Stopping device manager {} details.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if(dm.getId().equals(deviceManagerId))
			{
				if(pwal.stopDeviceManager(dm.getId()))
					return new ResponseEntity<>(HttpStatus.OK);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	/**
	 * This resource start a specific device manager
	 * @param deviceManagerId is the id of the specific device manager to be started
	 * @return 200 OK if everything is correct, 400 BAD REQUEST if start operation fails and 404 NOT FOUND if no device manager is found
	 * @return
	 */
	@RequestMapping(value="startdevicemanager/{deviceManagerId}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody ResponseEntity<?> startDeviceManager(@PathVariable String deviceManagerId)
	{
		log.info("Stopping device manager {} details.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if(dm.getId().equals(deviceManagerId))
			{
				if(pwal.startDeviceManager(dm.getId()))
					return new ResponseEntity<>(HttpStatus.OK);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Override
	public void notifyPWALDeviceAdded(Device newDevice) {
		log.info("New device added event from PWAL");
	}

	@Override
	public void notifyPWALDeviceRemoved(Device removedDevice) {
		log.info("New device removed event from PWAL");	
	}
}
