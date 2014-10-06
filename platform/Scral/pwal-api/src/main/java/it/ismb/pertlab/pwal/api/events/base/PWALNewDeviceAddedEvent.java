package it.ismb.pertlab.pwal.api.events.base;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

public class PWALNewDeviceAddedEvent extends PWALBaseEvent
{
    public PWALNewDeviceAddedEvent(String timeStamp, String senderId,
            Device device)
    {
        super(timeStamp, senderId, device);
    }
}
