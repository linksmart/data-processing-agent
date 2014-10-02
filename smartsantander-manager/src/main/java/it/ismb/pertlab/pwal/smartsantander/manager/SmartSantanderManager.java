package it.ismb.pertlab.pwal.smartsantander.manager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderSingleNodeJson;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.devices.SmartSantanderVehicleCounterDevice;
import it.ismb.pertlab.pwal.smartsantander.devices.SmartSantanderVehicleSpeedDevice;
import it.ismb.pertlab.pwal.smartsantander.devices.types.SmartSantaderDeviceTypes;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This component manages the SmartSantander sensors network supporting network
 * level polling
 * 
 * modified by <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class SmartSantanderManager extends PollingDevicesManager<SmartSantanderTrafficIntensityJson>
{
	// the minimum polling time safely supported by the network
	public static final int MINIMUM_POLLING_TIME = 100;
	
	// the default polling time for sensors handled by this manager
	public static final int DEFAULT_POLLING_TIME = 5000;
	
	// the percent tolerance on data delivery times
	public static final int TIME_TOLERANCE_PERCENT = 100;
	
	// the rest client need to gather sensor data
	SmartSantanderRestClient restClient = new SmartSantanderRestClient("http://data.smartsantander.eu/ISMB/", log);
	
	// the poller service
	private ScheduledExecutorService poller;
	
	// the polling task
	private SmartSantanderPollingTask pollingTask;
	
	// the future task execution promise that allows handling the polling
	// process
	private ScheduledFuture<?> futureRun;
	
	public SmartSantanderManager()
	{
		super();
		
		//build the poller
		this.poller = Executors.newSingleThreadScheduledExecutor();
		
		//build the polling task
		this.pollingTask = new SmartSantanderPollingTask(this, log);
	}
	
	@SuppressWarnings("unchecked")
    public void run()
	{
		while (!t.isInterrupted())
		{
			log.debug("Retrieving devices list from SmartSantander");
			List<SmartSantanderSingleNodeJson> availableNodes = restClient.getNodes();
			if (availableNodes != null && availableNodes.size() != 0)
			{
				log.debug("Devices list size: {}.", availableNodes.size());
				// Check for devices removed...comparing devices discovered with
				// the list returned by SmartSantander
				List<Device> toBeRemoved = new ArrayList<>();

				for (List<Device> ld : devicesDiscovered.values()) {
					for (Device d : ld) {
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

							log.info("Device {} seems to be removed. It is not into the devices list.", d);
							toBeRemoved.add(d);
							for (DeviceListener l : deviceListener) {
								l.notifyDeviceRemoved(d);
							}
						}
					}
				}
				// Removing devices no more available. Separate cycle to avoid
				// collection changing while ciclying on it
				log.debug("Removing no more present devices...");
				if (toBeRemoved.size() != 0)
				{
					for (Device device : toBeRemoved)
					{
						log.info("Removing {}.", device);
						this.devicesDiscovered.remove(device);
						//removing subscription
						this.removeSubscription((DataUpdateSubscription<SmartSantanderTrafficIntensityJson>) this.lowLevelDataSubscriptions.get(device.getId()));
					}
				}
				else
					log.debug("No device removed since last devices list request.");
				
				for (SmartSantanderSingleNodeJson smartSantanderSingleNodeJson : availableNodes)
				{
					if (!this.devicesDiscovered.containsKey(smartSantanderSingleNodeJson.getNodeId()))
					{
						switch (smartSantanderSingleNodeJson.getType()) {
						case SmartSantaderDeviceTypes.VEHICLE_COUNTER:
							SmartSantanderVehicleCounterDevice vehicleCounter = new SmartSantanderVehicleCounterDevice(this.restClient, this.getNetworkType());
							vehicleCounter.setId(smartSantanderSingleNodeJson.getNodeId());
							Location location=new Location();
							location.setLat(smartSantanderSingleNodeJson.getLatitude());
							location.setLon(smartSantanderSingleNodeJson.getLongitude());
							vehicleCounter.setLocation(location);
							if(!this.devicesDiscovered.containsKey(smartSantanderSingleNodeJson.getNodeId()))
							{
								List<Device> ld = new ArrayList<>();
								this.devicesDiscovered.put(smartSantanderSingleNodeJson.getNodeId(), ld);
log.debug("Adding subscription for:" + vehicleCounter.getId());
								
								// add device polling subscription
								// TODO: define sampling time at the device level
								this.addSubscription(new DataUpdateSubscription<SmartSantanderTrafficIntensityJson>(60000, vehicleCounter, vehicleCounter
										.getId()));
							}
							this.devicesDiscovered.get(smartSantanderSingleNodeJson.getNodeId()).add(vehicleCounter);
							for (DeviceListener l : deviceListener) {
								l.notifyDeviceAdded(vehicleCounter);
							}
							break;
						case SmartSantaderDeviceTypes.VEHICLE_SPEED:
							SmartSantanderVehicleSpeedDevice vehicleSpeed = new SmartSantanderVehicleSpeedDevice(this.restClient, this.getNetworkType());
							vehicleSpeed.setId(smartSantanderSingleNodeJson.getNodeId());
							Location location2=new Location();
							location2.setLat(smartSantanderSingleNodeJson.getLatitude());
							location2.setLon(smartSantanderSingleNodeJson.getLongitude());
							vehicleSpeed.setLocation(location2);
							if(!this.devicesDiscovered.containsKey(smartSantanderSingleNodeJson.getNodeId()))
							{
								List<Device> ld = new ArrayList<>();
								this.devicesDiscovered.put(smartSantanderSingleNodeJson.getNodeId(), ld);
								
log.debug("Adding subscription for:" + vehicleSpeed.getId());
								
								// add device polling subscription
								// TODO: define sampling time at the device level
								this.addSubscription(new DataUpdateSubscription<SmartSantanderTrafficIntensityJson>(60000, vehicleSpeed, vehicleSpeed
										.getId()));
							}
							this.devicesDiscovered.get(smartSantanderSingleNodeJson.getNodeId()).add(vehicleSpeed);
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
				log.warn("Something goes wrong retrieving devices from SmartSantander");
			}
			try
			{
				// Ask for devices list again in 1 minute
				log.debug("I'm going to ask for devices nodes again in 1 minute.");
				Thread.sleep(60000);
			}
			catch (InterruptedException e)
			{
				log.error("Exception: ", e);
				t.interrupt();
			}
		}
		
		// check if the polling task is running
		if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
		{
			//cancel the polling task
			this.futureRun.cancel(false);
			
		}
	}
	
	@Override
	public String getNetworkType()
	{
		return DeviceNetworkType.SMARTSANTANDER;
	}
	
	@Override
	protected void setBasePollingTimeMillis()
	{
		// set the minimum allowed polling time
		// TODO: set this value in the configuration...
		this.basePollingTimeMillis = SmartSantanderManager.DEFAULT_POLLING_TIME;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager#
	 * setMinimumPollingTimeMillis()
	 */
	@Override
	protected void setMinimumPollingTimeMillis()
	{
		this.minimumPollingTimeMillis = SmartSantanderManager.MINIMUM_POLLING_TIME;
		
	}
	
	@Override
	protected void setTimeTolerancePercentage()
	{
		this.timeTolerancePercentage = SmartSantanderManager.TIME_TOLERANCE_PERCENT;
	}
	
	@Override
	protected void updatePollingTime()
	{
		// check if the polling task is running
		if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
		{
			//stop the current polling task
			this.futureRun.cancel(false);
			
		}
		
		log.info("Updating polling time to: " + this.pollingTimeMillis);
		log.debug("Active subscriptions:" + this.nActiveSubscriptions);
		
		// starts the poller only if at least one subscription is available
		this.futureRun = this.poller.scheduleAtFixedRate(this.pollingTask, 0, this.pollingTimeMillis,
				TimeUnit.MILLISECONDS);
		
	}
	
}
