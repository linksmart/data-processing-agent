package it.ismb.pertlab.pwal;

import it.ismb.pertlab.pwal.api.devices.enums.DeviceManagerStatus;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.events.DeviceLogger;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.events.base.PWALBaseEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.PWALEventDispatcher;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.subscriber.PWALEventSubsciber;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycila.event.Event;
import com.mycila.event.Topic;
import com.mycila.event.Topics;
import com.mycila.event.annotation.Subscribe;

public class PwalImpl implements Pwal, DeviceListener {

	private List<Device> devicesList;
	private HashMap<String, DevicesManager> devicesManagers;
	private List<PWALDeviceListener> pwalDeviceListeners;
	private static SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss");
	private ArrayList<DeviceLogger> pwalDeviceLoggerList;
	private static int maxlogsize =20;
	private PWALEventPublisher eventPublisher;
	
	private static final Logger log=LoggerFactory.getLogger(PwalImpl.class);

	public PwalImpl(List<DevicesManager> devicesManager)
	{
		this.devicesList= new ArrayList<>();
		this.pwalDeviceListeners = new ArrayList<>();
		this.devicesManagers = new HashMap<>();

		this.pwalDeviceLoggerList = new ArrayList<>();
		
		for(DevicesManager d : devicesManager)
		{
			d.setId(UUID.randomUUID().toString());
			d.setStatus(DeviceManagerStatus.STOPPED);
			this.devicesManagers.put(d.getId(), d);
			d.addDeviceListener(this);
			this.startDeviceManager(d.getId());
		}
		this.eventPublisher = new PWALEventPublisher();
//		PWALEventDispatcher.getInstance().getDispatcher().subscribe(Topics.any(), PWALNewDataAvailableEvent.class, new PWALEventSubsciber<PWALNewDataAvailableEvent>()
//                {
//                    @Override
//                    public void onEvent(Event<PWALNewDataAvailableEvent> arg0)
//                            throws Exception
//                    {
//                        log.info("Received NewDataAvailable event from {}.", arg0.getSource().getSenderId());
//                        log.info("Event topic is: {}", arg0.getTopic());
//                        log.info("Mausurement will be valid from {} to {}", arg0.getSource().getTimeStamp(), arg0.getSource().getExpirationTime());
//                        log.info("New values received: ");
//                        for (String k : arg0.getSource().getValues().keySet())
//                        {
//                            log.info("Key: {}, Value: {}",k, arg0.getSource().getValues().get(k));
//                        }
//                    }
//                });
//		
//		PWALEventDispatcher.getInstance().getDispatcher().subscribe(Topic.match("newdata/devices/**"), PWALNewDataAvailableEvent.class, new PWALEventSubsciber<PWALNewDataAvailableEvent>()
//                {
//
//                    @Override
//                    public void onEvent(Event<PWALNewDataAvailableEvent> arg0)
//                            throws Exception
//                    {
//                        log.info("############### Received NewData event ###############");
//                    }
//                });
	}
			
	public Device getDevice(String id) {
		for (Device d : this.devicesList) {
			if(d.getPwalId().equals(id))
				return d;
		}
		return null;
	}

	private String generateId(Device newDevice)
	{
	    return UUID.nameUUIDFromBytes(String.format("%s-%s-%s", newDevice.getNetworkType(),newDevice.getType(), newDevice.getId()).getBytes()).toString();
	}

	@Override
	public void notifyDeviceAdded(Device newDevice) {
		String generatedId = this.generateId(newDevice);
		//this is just a WORKAROUND for the IoT demo. fixed id to solve db sync
		switch (newDevice.getId()) {
		case "idFlowSensor":
			newDevice.setPwalId("1fa71b84-f0c8-4bd6-91f8-4e69e073ece7");
			break;
		case "idFillLevelSensor":
			newDevice.setPwalId("41fbcb5a-8e0e-460a-bcc0-ffd067793dbb");
			break;
		case "idWaterPump":
			newDevice.setPwalId("2d66307c-6d7a-4687-a4fa-dc0893648bcd");
			break;
		default:
			newDevice.setPwalId(generatedId);
			break;
		}
		log.info("New PWAL device added: generated id {} type {}.", generatedId, newDevice.getType());
		PWALNewDeviceAddedEvent event = new PWALNewDeviceAddedEvent(DateTime.now(DateTimeZone.UTC).toString(), "PWAL", newDevice);
		this.eventPublisher.setTopics(new String[]{PWALTopicsUtility.newDeviceAddedTopic(newDevice.getNetworkType())});
		this.eventPublisher.publish(event);
		log.info("NEW DEVICE ADDED EVENT PUBLISHED USING TOPIC {}", PWALTopicsUtility.newDeviceAddedTopic(newDevice.getNetworkType()));
		
		
		String LogMsg="New device added: generated Id:"+generatedId+"; type:"+ newDevice.getType();
		pwalDeviceLoggerList.add(0, new DeviceLogger(sdf.format(System.currentTimeMillis()), LogMsg ));
		if(pwalDeviceLoggerList.size()>maxlogsize) {
			pwalDeviceLoggerList.remove(pwalDeviceLoggerList.size() -1);
		}
		synchronized (this.devicesList) {
			this.devicesList.add(newDevice);
		}

		for (PWALDeviceListener listener : this.pwalDeviceListeners) {
			listener.notifyPWALDeviceAdded(newDevice);
		}
	}

	@Override
	public void notifyDeviceRemoved(Device removedDevice) {
		int index = 0;
		for (Device d : this.devicesList) {
			if(d.getPwalId().equals(removedDevice.getPwalId()))
			{
					this.devicesList.remove(index);
					log.info("New device removed: id {} type {}.", removedDevice.getPwalId(), removedDevice.getType());
					
					String LogMsg="Device Removed: Id: "+removedDevice.getPwalId()+";type:"+ removedDevice.getType();
					pwalDeviceLoggerList.add(0, new DeviceLogger(sdf.format(System.currentTimeMillis()), LogMsg ));
					if(pwalDeviceLoggerList.size()>maxlogsize) {
						pwalDeviceLoggerList.remove(pwalDeviceLoggerList.size() -1);
					}
					break;
			}
			index++;
		}
		
		for (PWALDeviceListener listener : this.pwalDeviceListeners) {
			listener.notifyPWALDeviceRemoved(removedDevice);
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

	@Override
	public ArrayList<DeviceLogger> getDeviceLogList() {
		return this.pwalDeviceLoggerList;
	}
}
