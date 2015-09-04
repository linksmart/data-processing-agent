package it.ismb.pertlab.pwal.connectors.rest;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.ControllableDevice;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.FakeControllableThermometer;
import it.ismb.pertlab.pwal.api.devices.model.Meter;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.connectors.datamodel.DeviceCommand;
import it.ismb.pertlab.pwal.connectors.datamodel.DevicesManagerStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.apache.commons.logging.impl.AvalonLogger;
//import javolution.lang.Reusable;
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

import scpdhelper.ScpdHelper;

@Controller
public class PwalRestConnector implements PWALDeviceListener {

	private Pwal pwal;

	private static final Logger log = LoggerFactory
			.getLogger(PwalRestConnector.class);

	private ScpdHelper scpdHelper;

	/**
	 * 
	 */
	public PwalRestConnector()
	{
		//TODO: Remove this HARDCODED param
		this.scpdHelper = new ScpdHelper("scral.trn.federation1.almanac-project.eu");
	}

	/**
	 * This resource retrieves the detailed list of the devices adapted by the
	 * pwal. This resource can be also used to filter device basing on the
	 * network and the device type
	 * 
	 * @param networkType
	 *            the network type to which the device belong
	 * @param deviceType
	 *            the device type to which the device belong
	 * @return
	 * @return a json containing all the devices details
	 */
	@RequestMapping(value = "devices", method = RequestMethod.GET)
	// , produces="application/json")
	public @ResponseBody Map<String, List<String>> getAllDevicesList(
			@RequestParam(value = "networkType", required = false) String networkType,
			@RequestParam(value = "deviceType", required = false) String deviceType) {
		log.info("Returning all devices list");
		Map<String, List<String>> result = new HashMap<>();

		List<String> dev = new ArrayList<>();

		String id = "pwalId";

		if (networkType == null && deviceType == null) {
			for (Device d : pwal.getDevicesList()) {
				dev.add(d.getPwalId());
			}
		}

		else if ((networkType != null && networkType.length() != 0)
				&& (deviceType != null && deviceType.length() != 0)) {
			for (Device d : pwal.getDevicesList()) {
				if (d.getNetworkType().equals(networkType)
						&& d.getType().equals(deviceType))
					dev.add(d.getPwalId());
			}
		}

		else if ((networkType == null || networkType.isEmpty())
				&& (deviceType != null && !deviceType.isEmpty())) {
			for (Device d : pwal.getDevicesList()) {
				if (d.getType().equals(deviceType))
					dev.add(d.getPwalId());
			}
		} else if ((networkType != null && !networkType.isEmpty())
				&& (deviceType == null || deviceType.isEmpty())) {
			for (Device d : pwal.getDevicesList()) {
				if (d.getNetworkType().equals(networkType))
					dev.add(d.getPwalId());
			}
		}
		result.put(id, dev);
		return result;
	}

	/**
	 * This resource returns a devices collections containing all the details
	 * about each device
	 * 
	 * @return a json containing the collection of devices
	 */
	@RequestMapping(value = "detaileddevices", method = RequestMethod.GET)
	public @ResponseBody Collection<Device> getAllDevicesDetailedList() {
		return this.pwal.getDevicesList();
	}

	@RequestMapping(value = "detaileddevices2", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, List<List<String>>> getAllDevicesDetailedList2() {
		List<List<String>> result = new ArrayList<>();
		for (Device d : this.pwal.getDevicesList()) {
			List<String> toAdd = new ArrayList<>();
			toAdd.add(d.getPwalId());
			toAdd.add(d.getId());
			toAdd.add(d.getType());
			toAdd.add(d.getNetworkType());
			if (d.getLocation() != null)
				toAdd.add(String.format("%s %s", d.getLocation().getLon(), d
						.getLocation().getLat()));
			result.add(toAdd);
		}
		HashMap<String, List<List<String>>> map = new HashMap<>();
		map.put("data", result);
		return map;
	}

	/**
	 * This resource retrieves details about a specific device
	 * 
	 * @param deviceId
	 *            is the device id (the one assigned by the pwal)
	 * @return a json containing device detail
	 */
	@RequestMapping(value = "countDevice", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Integer getNumberOfDevices() {
		return pwal.getDevicesList().size();
	}

	/**
	 * This resource retrieves details about a specific device
	 * 
	 * @param deviceId
	 *            is the device id (the one assigned by the pwal)
	 * @return a json containing device detail
	 */
	@RequestMapping(value = "devices/{deviceId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Device getDevice(@PathVariable String deviceId) {
		log.info("Returning device info. Requested id: {}", deviceId);
		for (Device d : pwal.getDevicesList()) {
			if (d.getPwalId().equals(deviceId))
				return d;
		}
		return null;
	}

	/**
	 * 
	 * @param deviceId
	 * @param speed
	 * @return
	 */
	@RequestMapping(value = "devices/{deviceId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changeValues(
			@PathVariable String deviceId, @RequestBody DeviceCommand command) {
//		log.info("Searching for device {}", deviceId);
		for (Device device : pwal.getDevicesList()) {
//			log.info("Comparing deviceid {} with {}", deviceId, device.getPwalId());
			if (device.getPwalId().equals(deviceId)) {
				try {
					if(((ControllableDevice)device).getSupportedCommand() != null)
					{
					AbstractCommand abstractCommand = ((ControllableDevice)device).getSupportedCommand().get(command.getCommandName());
					if(abstractCommand!= null)
					{
						abstractCommand.setParams(command.getParams());
						abstractCommand.execute();
						return new ResponseEntity<>(HttpStatus.OK);
					}
					else
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					}
					else 
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					//					switch (device.getType()) {
//					case DeviceType.WATER_PUMP:
//						return null;
//					case DeviceType.PHILIPS_HUE:
////						log.info("Requested device type is: {}",
////								device.getType());
//						AbstractCommand philipsHueCommand = ((PhilipsHue)device).getSupportedCommand().get(command.getCommandName());
//						if(philipsHueCommand != null)
//						{
//							philipsHueCommand.setParams(command.getParams());
//							philipsHueCommand.execute();
//							return new ResponseEntity<>(HttpStatus.OK);
//						}
//						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//					case DeviceType.METER:
//						AbstractCommand meterCommand = ((Meter)device).getSupportedCommand().get(command.getCommandName());
//						if(meterCommand != null)
//						{
//							meterCommand.setParams(command.getParams());
//							meterCommand.execute();
//							return new ResponseEntity<>(HttpStatus.OK);
//						}
//						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//					case "FakeThermometer":
//						AbstractCommand fakeCommnad = ((FakeControllableThermometer)device).getSupportedCommand().get(command.getCommandName());
//						if(fakeCommnad!=null)
//						{
//							fakeCommnad.setParams(command.getParams());
//							fakeCommnad.execute();
//							return new ResponseEntity<>(HttpStatus.OK);
//						}
//						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//					default:
//						log.info("Requested device type is not controllable");
//						return new ResponseEntity<>(
//								HttpStatus.SERVICE_UNAVAILABLE);
//					}
				} catch (SecurityException e) {
					log.error(e.getLocalizedMessage());
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
		}
		log.info("Device {} not found.", deviceId);
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	/**
	 * This resource retrieves device managers list
	 * 
	 * @return a json with the list of all device managers available
	 */
	@RequestMapping(value = "devicesmanagers", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Collection<DevicesManager> getDeviceManagersList() {
		log.info("Returing device managers list");
		return pwal.getDevicesManagerList();
	}

	/**
	 * This resource retrieves device manager details
	 * 
	 * @param deviceManagerId
	 *            is the id of the required device manager
	 * @return a json containing details about the specific device manager
	 */
	@RequestMapping(value = "devicesmanagers/{deviceManagerId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody DevicesManager getDeviceManager(
			@PathVariable String deviceManagerId) {
		log.info("Returning device manager {} details.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if (dm.getId().equals(deviceManagerId))
				return dm;
		}
		return null;
	}

	/**
	 * This resource change status of a specific device manager
	 * 
	 * @param deviceManagerId
	 *            is the id of the specific device manager to be stopped
	 * @return 200 OK if everything is correct, 503 SERVICE UNAVAILABLE if stop
	 *         operation fails and 404 NOT FOUND if no device manager is found
	 * @return
	 */
	@RequestMapping(value = "devicesmanagers/{deviceManagerId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changeDevicesManagerStatus(
			@PathVariable String deviceManagerId,
			@RequestBody DevicesManagerStatus status) {
		log.info("Changing devices manager {} status.", deviceManagerId);
		for (DevicesManager dm : pwal.getDevicesManagerList()) {
			if (dm.getId().equals(deviceManagerId)) {
				switch (status.getStatus()) {
				case "STARTED":
					if (pwal.startDeviceManager(dm.getId()))
						return new ResponseEntity<>(HttpStatus.OK);
					return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
				case "STOPPED":
					if (pwal.stopDeviceManager(dm.getId()))
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

	/**
	 * This resource retrieves details about a specific device
	 * 
	 * @param deviceId
	 *            is the device id (the one assigned by the pwal)
	 * @return a json containing device detail
	 */
	// @RequestMapping(value = "devices/{deviceId}/scpd", method =
	// RequestMethod.GET, produces = "application/xml")
	// public @ResponseBody String getDeviceScpd(@PathVariable String deviceId,
	// HttpServletRequest request)
	// {
	// String scpd = null;
	//
	// log.info("Returning SCPD device info. Requested id: {}", deviceId);
	// Device d = pwal.getDevice(deviceId);
	// if (d != null)
	// scpd = this.scpdHelper.getSCPD(d, "http://" + request.getServerName() +
	// ":" + request.getServerPort() + "/"
	// + request.getServletPath(), request.getServerName() + ":" +
	// request.getServerPort());
	//
	// if (scpd == null || scpd.isEmpty())
	// log.info("NULL SCPD, device is missing....");
	//
	// return scpd;
	// }
	//
	@Override
	public void notifyPWALDeviceAdded(Device newDevice) {
		log.info("New device added event from PWAL");
	}

	@Override
	public void notifyPWALDeviceRemoved(Device removedDevice) {
		log.info("New device removed event from PWAL");
	}

	public Pwal getPwal() {
		return pwal;
	}

	@Autowired
	public void setPwal(Pwal pwal) {
		this.pwal = pwal;
	}

}
