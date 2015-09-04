package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.enums.DeviceManagerStatus;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.events.DeviceLogger;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.events.base.PWALDeviceRemovedEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.api.utils.UIDGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PwalImpl implements Pwal, DeviceListener
{
	
	private Hashtable<String, Device> devicesList;
	private HashMap<String, DevicesManager> devicesManagers;
	private List<PWALDeviceListener> pwalDeviceListeners;
	private static SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss");
	private ArrayList<DeviceLogger> pwalDeviceLoggerList;
	private static int maxlogsize = 20;
	private PWALEventPublisher eventPublisher;
	
	private static final Logger log = LoggerFactory.getLogger(PwalImpl.class);
	
	private ExecutorService notificationDeliveryService;
	
	public PwalImpl(List<DevicesManager> devicesManager)
	{
	
		// build the executor services
		this.notificationDeliveryService = Executors.newFixedThreadPool(4);
		
		this.devicesList = new Hashtable<>();
		this.pwalDeviceListeners = new ArrayList<>();
		this.devicesManagers = new HashMap<>();
		
		this.pwalDeviceLoggerList = new ArrayList<>();
		
		for (DevicesManager d : devicesManager)
		{
			d.setId(UUID.randomUUID().toString());
			d.setStatus(DeviceManagerStatus.STOPPED);
			this.devicesManagers.put(d.getId(), d);
			d.addDeviceListener(this);
			this.startDeviceManager(d.getId());
		}
		this.eventPublisher = new PWALEventPublisher();
	}
	
	public Device getDevice(String id)
	{
		return this.devicesList.get(id);
	}
	
	/**
	 * Generate the ID of a device
	 * 
	 * @param newDevice
	 *            The device for which the ID shall be generated
	 * @return The generated id.
	 */
	private String generateId(Device newDevice)
	{
		return UUID.nameUUIDFromBytes(
				String.format("%s-%s-%s", newDevice.getNetworkType(), newDevice.getType(), newDevice.getId())
						.getBytes()).toString();
	}
	
	@Override
	public synchronized void notifyDeviceAdded(final Device newDevice)
	{
		// generate the unique device id
		String generatedId = this.generateId(newDevice);
		
		// set the device id
		newDevice.setPwalId(generatedId);
		
		// debug
		log.debug("New PWAL device added: generated id {} type {}.", generatedId, newDevice.getType());
		
		// attach a logger to the device
		// TODO: why this is needed????
		String LogMsg = "New device added: generated Id:" + generatedId + "; type:" + newDevice.getType();
		pwalDeviceLoggerList.add(0, new DeviceLogger(sdf.format(System.currentTimeMillis()), LogMsg));
		
		if (pwalDeviceLoggerList.size() > maxlogsize)
		{
			pwalDeviceLoggerList.remove(pwalDeviceLoggerList.size() - 1);
		}
		
		// store the new device
		this.devicesList.put(newDevice.getPwalId(), newDevice);
		
		this.notificationDeliveryService.submit(new Runnable() {
			
			@Override
			public void run()
			{
				// notify devie listeners
				for (PWALDeviceListener listener : pwalDeviceListeners)
				{
					listener.notifyPWALDeviceAdded(newDevice);
				}
				
				// publish the event
				publishNewDeviceAdded(newDevice);
			}
		});
	}
	
	@Override
	public synchronized void notifyDeviceRemoved(final Device removedDevice)
	{
		// remove the device
		this.devicesList.remove(removedDevice.getPwalId());
		
		log.info("New device removed: id {} type {}.", removedDevice.getPwalId(), removedDevice.getType());
		
		// TODO: is this really needed???
		String LogMsg = "Device Removed: Id: " + removedDevice.getPwalId() + ";type:" + removedDevice.getType();
		pwalDeviceLoggerList.add(0, new DeviceLogger(sdf.format(System.currentTimeMillis()), LogMsg));
		if (pwalDeviceLoggerList.size() > maxlogsize)
		{
			pwalDeviceLoggerList.remove(pwalDeviceLoggerList.size() - 1);
		}
		
		this.notificationDeliveryService.submit(new Runnable() {
			
			@Override
			public void run()
			{
				// notify listeners
				for (PWALDeviceListener listener : pwalDeviceListeners)
				{
					listener.notifyPWALDeviceRemoved(removedDevice);
				}
				publishDeviceRemoved(removedDevice);
			}
		});
		
	}
	
	@Override
	public Collection<Device> getDevicesByType(String type)
	{
		Set<Device> result = new HashSet<>();
		if (type == null || type.length() == 0)
		{
			return result;
		}
		for (Device d : this.devicesList.values())
		{
			if (type.equals(d.getType()))
			{
				result.add(d);
			}
		}
		return result;
	}
	
	@Override
	public Collection<Device> getDevicesList()
	{
		return this.devicesList.values();
	}
	
	@Override
	public Collection<DevicesManager> getDevicesManagerList()
	{
		return this.devicesManagers.values();
	}
	
	@Override
	public Boolean startDeviceManager(String deviceManagerName)
	{
		this.devicesManagers.get(deviceManagerName).start();
		if (this.devicesManagers.get(deviceManagerName).getStatus().equals(DeviceManagerStatus.STARTED))
		{
			log.debug("Device manager {} successfully started", deviceManagerName);
			return true;
		}
		log.error("Device manager {} has not been started", deviceManagerName);
		return false;
	}
	
	@Override
	public Boolean stopDeviceManager(String deviceManagerName)
	{
		this.devicesManagers.get(deviceManagerName).stop();
		if (this.devicesManagers.get(deviceManagerName).getStatus().equals(DeviceManagerStatus.STOPPED))
		{
			log.debug("Device manager {} successfully stopped", deviceManagerName);
			return true;
		}
		log.error("Device manager {} has not been stopped", deviceManagerName);
		return false;
	}
	
	@Override
	public void addPwalDeviceListener(PWALDeviceListener listener)
	{
		this.pwalDeviceListeners.add(listener);
	}
	
	@Override
	public void removePwalDeviceListener(PWALDeviceListener listener)
	{
		this.pwalDeviceListeners.remove(listener);
	}
	
	@Override
	public ArrayList<DeviceLogger> getDeviceLogList()
	{
		return this.pwalDeviceLoggerList;
	}
	
	private void publishNewDeviceAdded(Device newDevice)
	{
		
		// create the device added event
		PWALNewDeviceAddedEvent event = new PWALNewDeviceAddedEvent(DateTime.now(DateTimeZone.UTC).toString(), "PWAL",
				newDevice);
		
		// set the event topics
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createNewDeviceAddedTopic(newDevice
				.getNetworkType()) });
		
		// publish the event
		this.eventPublisher.publish(event);
		log.debug("NEW DEVICE ADDED EVENT PUBLISHED USING TOPIC {}",
				PWALTopicsUtility.createNewDeviceAddedTopic(newDevice.getNetworkType()));
	}
	
	private void publishDeviceRemoved(Device removedDevice)
	{
		PWALDeviceRemovedEvent event = new PWALDeviceRemovedEvent(DateTime.now(DateTimeZone.UTC).toString(), "PWAL",
				removedDevice);
		
		this.eventPublisher.setTopics(new String[] { PWALTopicsUtility.createDeviceRemovedTopic(removedDevice
				.getNetworkType()) });
		this.eventPublisher.publish(event);
		log.debug("DEVICE REMOVED EVENT PUBLISHED USING TOPIC {}",
				PWALTopicsUtility.createNewDeviceAddedTopic(removedDevice.getNetworkType()));
	}
}
