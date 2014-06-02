package it.ismb.pertlab.pwal.connectors.rest;

import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.connectors.datamodel.DeviceCommand;
import it.ismb.pertlab.pwal.connectors.datamodel.DevicesManagerStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PwalRestConnector implements PWALDeviceListener {

	@Autowired
	private Pwal pwal;
	
	private static final Logger log=LoggerFactory.getLogger(PwalRestConnector.class);

	/**
	 * This resource retrieves the detailed list of the devices adapted by the pwal.
	 * This resource can be also used to filter device basing on the network and the device type
	 * @param networkType the network type to which the device belong
	 * @param deviceType the device type to which the device belong
	 * @return
	 * @return a json containing all the devices details
	 */
	@RequestMapping(value="devices", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Map<String, List<String>> getAllDevicesList(@RequestParam(value="networkType", required=false) String networkType, 
			  @RequestParam(value="deviceType", required=false)String deviceType)
	{
		log.info("Returing all devices list");
		Map<String, List<String>> result = new HashMap<>();
		
		List<String> dev = new ArrayList<>();
		
		String id = "pwalId";
		
		if(networkType == null && deviceType == null)
		{
			for (Device d : pwal.getDevicesList()) {
				dev.add(d.getPwalId());
			}
		}
		
		else if((networkType != null && networkType.length() != 0) && 
		   (deviceType != null && deviceType.length() != 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getNetworkType().equals(networkType) && d.getType().equals(deviceType))
					dev.add(d.getPwalId());
			}
		}
		
		else if((networkType == null || networkType.length() == 0) &&
		   (deviceType != null && deviceType.length() != 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getType().equals(deviceType))
					dev.add(d.getPwalId());
			}
		}
		else if((networkType != null && networkType.length() != 0) &&
		   (deviceType == null || deviceType.length() == 0))
		{
			for (Device d : pwal.getDevicesList()) 
			{
				if(d.getNetworkType().equals(networkType))
					dev.add(d.getPwalId());
			}
		}
		result.put(id, dev);
		return result;
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
	 * Just a test! should be done with a POST instead.
	 * @param deviceId
	 * @param speed
	 * @return
	 */
	@RequestMapping(value="devices/{deviceId}", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changeValues(@PathVariable String deviceId, @RequestBody DeviceCommand command)
	{
		log.info("Searching for device");
		for (Device device : pwal.getDevicesList()) {
			if(device.getPwalId().equals(deviceId))
			{
				switch (device.getType()) {
				case DeviceType.WATER_PUMP:
					log.info("Requested device type is: {}", device.getType());
					try {
						Method setVelocityMethod = ((WaterPump)device).getClass().getMethod(command.getMethodName(), Double.class);
						setVelocityMethod.invoke(((WaterPump)device).getClass(), command.getParams().toArray()[0]);
						return new ResponseEntity<>(HttpStatus.OK);
					} catch (NoSuchMethodException | SecurityException e) {
						log.error("Exception: ", e);
					} catch (IllegalAccessException e) {
						log.error("Exception: ", e);
					} catch (IllegalArgumentException e) {
						log.error("Exception: ", e);
					} catch (InvocationTargetException e) {
						log.error("Exception: ", e);
					}
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
				default:
					log.info("Requested device type is not controllable");
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);				}
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
		
	/**
	 * This resource retrieves device managers list
	 * @return a json with the list of all device managers available
	 */
	@RequestMapping(value="devicesmanagers", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Collection<DevicesManager> getDeviceManagersList()
	{
		log.info("Returing device managers list");
		return pwal.getDevicesManagerList();
	}

	
	/**
	 * This resource retrieves device manager details
	 * @param deviceManagerId is the id of the required device manager
	 * @return a json containing details about the specific device manager
	 */
	@RequestMapping(value="devicesmanagers/{deviceManagerId}", method=RequestMethod.GET, produces="application/json")
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
	 * This resource change status of a specific device manager
	 * @param deviceManagerId is the id of the specific device manager to be stopped
	 * @return 200 OK if everything is correct, 503 SERVICE UNAVAILABLE if stop operation fails and 404 NOT FOUND if no device manager is found
	 * @return
	 */
	@RequestMapping(value="devicesmanagers/{deviceManagerId}", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changeDevicesManagerStatus(@PathVariable String deviceManagerId, @RequestBody DevicesManagerStatus status)
	{
		log.info("Changing devices manager {} status.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if(dm.getId().equals(deviceManagerId))
			{
				switch (status.getStatus()) {
				case "STARTED":
					if(pwal.startDeviceManager(dm.getId()))
						return new ResponseEntity<>(HttpStatus.OK);
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
				case "STOPPED":
					if(pwal.stopDeviceManager(dm.getId()))
						return new ResponseEntity<>(HttpStatus.OK);
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
				default:
					log.error("Recevied unknown status: {}", status.getStatus());
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
				}
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
