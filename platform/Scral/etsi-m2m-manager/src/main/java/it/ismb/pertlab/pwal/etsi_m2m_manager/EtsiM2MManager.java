package it.ismb.pertlab.pwal.etsi_m2m_manager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstances;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.M2MUtility;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.events.M2MDeviceListener;
import it.ismb.pertlab.pwal.etsi_m2m_manager.utility.polling.EtsiM2MPollingTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class EtsiM2MManager extends PollingDevicesManager<ContentInstances>
        implements M2MDeviceListener
{
    private M2MUtility m2mUtility;
    // the minimum polling time safely supported by the network
    public static final int MINIMUM_POLLING_TIME = 100;

    // the default polling time for sensors handled by this manager
    public static final int DEFAULT_POLLING_TIME = 15000;

    // the percent tolerance on data delivery times
    public static final int TIME_TOLERANCE_PERCENT = 100;

    // private HashMap<String, String> contentInstancesMap = new HashMap<>();

    public EtsiM2MManager(String baseUrl)
    {
        this.m2mUtility = new M2MUtility(baseUrl, null);
        this.m2mUtility.addM2MEventListener(this);
        this.pollingTask = new EtsiM2MPollingTask(this, log);
    }

    public void run()
    {
        log.info("M2M manager started.");
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(this.m2mUtility, 0, 600 * 1000);
        while (!t.isInterrupted())
        {
            try
            {
                Thread.sleep(20000);
            }
            catch (InterruptedException e)
            {
                log.error("EtsiM2MManager: {}", e.getLocalizedMessage());
            }
        }
        for (List<Device> ld : this.devicesDiscovered.values())
        {
            for (Device d : ld)
            {
                for (DeviceListener dl : this.deviceListener)
                {
                    dl.notifyDeviceRemoved(d);
                }
            }
        }
        this.devicesDiscovered.clear();
    }

    @Override
    public String getNetworkType()
    {
        return DeviceNetworkType.M2M;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyM2MDeviceAdded(Device newDevice)
    {
        if (this.devicesDiscovered.containsKey(newDevice.getId()))
            this.devicesDiscovered.get(newDevice.getId()).add(newDevice);
        else
        {
            List<Device> ld = new ArrayList<>();
            ld.add(newDevice);
            this.devicesDiscovered.put(newDevice.getId(), ld);
        }
        for (DeviceListener l : deviceListener)
        {
            log.info("New M2M device discovered. Generating event.");
            l.notifyDeviceAdded(newDevice);
        }
//        this.addSubscription(new DataUpdateSubscription<ContentInstances>(60000,
//                (DataUpdateSubscriber<ContentInstances>) newDevice, newDevice
//                        .getId()));
        this.addSubscription(new DataUpdateSubscription<ContentInstances>(10000,
                (DataUpdateSubscriber<ContentInstances>) newDevice, newDevice
                        .getId()));
    }

    public List<String> getDevicesListNames()
    {
        return new ArrayList<>(this.devicesDiscovered.keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyM2MDeviceRemoved(Device removedDevice)
    {
        this.devicesDiscovered.remove(removedDevice);
        this.removeSubscription((DataUpdateSubscription<ContentInstances>) this.lowLevelDataSubscriptions
                .get(removedDevice.getId()));
    }

    @Override
    protected void setBasePollingTimeMillis()
    {
        this.basePollingTimeMillis = EtsiM2MManager.DEFAULT_POLLING_TIME;
    }

    @Override
    protected void setMinimumPollingTimeMillis()
    {
        this.minimumPollingTimeMillis = EtsiM2MManager.MINIMUM_POLLING_TIME;
    }

    @Override
    protected void setTimeTolerancePercentage()
    {
        this.timeTolerancePercentage = EtsiM2MManager.TIME_TOLERANCE_PERCENT;
    }

    @Override
    protected void updatePollingTime()
    {
        // check if the polling task is running
        if ((this.futureRun != null) && (!this.futureRun.isCancelled()))
        {
            // stop the current polling task
            this.futureRun.cancel(false);

        }

        log.info("Updating polling time to: " + this.pollingTimeMillis);
        log.debug("Active subscriptions:" + this.nActiveSubscriptions);

        // starts the poller only if at least one subscription is available
        this.futureRun = this.poller.scheduleAtFixedRate(this.pollingTask, 0,
                this.pollingTimeMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * As documented into the M2MUtility class this method return an hash map
     * contaning all the content instances urls
     * 
     * @return an hash map with all the content instances url
     */
    public HashMap<String, String> getContentInstancesList()
    {
        return this.m2mUtility.getContentInstancesUrl();
    }

}