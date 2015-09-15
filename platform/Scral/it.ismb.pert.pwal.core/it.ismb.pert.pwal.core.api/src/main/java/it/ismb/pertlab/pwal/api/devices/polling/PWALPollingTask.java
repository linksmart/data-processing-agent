package it.ismb.pertlab.pwal.api.devices.polling;

import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;

import org.slf4j.Logger;

public abstract class PWALPollingTask<T,K> implements Runnable
{
    /**
     * Reference to the polling devices manager
     */
    protected PollingDevicesManager<K> manager;
    /*
     * Reference to the logger object
     */
    protected Logger logger;
    
    /**
     * Constructor to create a polling task
     * @param manager, reference to the polling devices manager
     * @param log, reference to the logger
     */
    public PWALPollingTask(PollingDevicesManager<K> manager, Logger logger)
    {
        this.manager = manager;
        this.logger = logger;
    }
}
