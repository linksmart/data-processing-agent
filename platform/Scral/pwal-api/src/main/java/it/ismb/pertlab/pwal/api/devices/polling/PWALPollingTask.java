package it.ismb.pertlab.pwal.api.devices.polling;

import it.ismb.pertlab.pwal.api.devices.interfaces.PollingDevicesManager;

import org.slf4j.Logger;

public abstract class PWALPollingTask<T> implements Runnable
{
    /**
     * Reference to the polling devices manager
     */
    protected PollingDevicesManager<T> manager;
    /**
     * Reference to the logger object
     */
    protected Logger log;
    
    /**
     * Constructor to create a polling task
     * @param manager, reference to the polling devices manager
     * @param log, reference to the logger
     */
    public PWALPollingTask(PollingDevicesManager<T> manager, Logger log)
    {
        this.manager = manager;
        this.log = log;
    }
}
