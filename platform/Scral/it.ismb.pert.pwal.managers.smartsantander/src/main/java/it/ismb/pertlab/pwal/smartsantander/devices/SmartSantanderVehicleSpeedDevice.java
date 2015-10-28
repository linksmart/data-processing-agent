package it.ismb.pertlab.pwal.smartsantander.devices;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscriber;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.pubsub.publisher.PWALEventPublisher;
import it.ismb.pertlab.pwal.api.events.pubsub.topics.PWALTopicsUtility;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;
import it.ismb.pertlab.pwal.smartsantander.restclient.SmartSantanderRestClient;

public class SmartSantanderVehicleSpeedDevice implements VehicleSpeed,
        DataUpdateSubscriber<SmartSantanderTrafficIntensityJson>
{

    String id;
    String pwalId;
    String type = DeviceType.VEHICLE_SPEED;
    String networkType = DeviceNetworkType.SMARTSANTANDER;
    Location location;
    String dateLastMeasurement;
    SmartSantanderRestClient restClient;

    private Double occupancy = 0.0;
    private Double count = 0.0;
    private Double medianSpeed = 0.0;
    private Double averageSpeed = 0.0;
    private String expiresAt;
    private String updatedAt;
    private PWALEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(SmartSantanderVehicleSpeedDevice.class);

    public SmartSantanderVehicleSpeedDevice(
            SmartSantanderRestClient restClient, String networkType)
    {
        this.restClient = restClient;
        this.networkType = networkType;
        this.eventPublisher = new PWALEventPublisher();
    }

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return this.type;
    }

    public Double getOccupancy()
    {

        /*
         * SmartSantanderTrafficIntensityJson measure =
         * this.restClient.getLastMeasures(this.id); if (measure != null) return
         * measure.getOccupancy(); else return -1.0;
         */
        return this.occupancy;
    }

    public Double getCount()
    {
        /*
         * SmartSantanderTrafficIntensityJson measure =
         * this.restClient.getLastMeasures(this.id); if (measure != null) return
         * measure.getCount(); else return -1.0;
         */

        return this.count;
    }

    public Double getMedianSpeed()
    {
        /*
         * SmartSantanderTrafficIntensityJson measure =
         * this.restClient.getLastMeasures(this.id); if (measure != null) return
         * measure.getMedian_speed(); else return -1.0;
         */
        return this.medianSpeed;
    }

    public Double getAverageSpeed()
    {
        /*
         * SmartSantanderTrafficIntensityJson measure =
         * this.restClient.getLastMeasures(this.id); if (measure != null) return
         * measure.getAverage_speed(); else return -1.0;
         */
        return this.averageSpeed;
    }

    public String getNetworkType()
    {
        return this.networkType;
    }

    public String getDateLastMeasurement()
    {
        /*
         * SmartSantanderTrafficIntensityJson measure =
         * this.restClient.getLastMeasures(this.id); if (measure != null) return
         * measure.getDate(); else return null;
         */
        return this.dateLastMeasurement;
    }

    @Override
    public String getPwalId()
    {
        return this.pwalId;
    }

    @Override
    public void setPwalId(String pwalId)
    {
        this.pwalId = pwalId;
        this.eventPublisher.setTopics(new String[]
                { PWALTopicsUtility.createNewDataFromDeviceTopic(DeviceNetworkType.SMARTSANTANDER,
                        this.getPwalId()) });
    }

    @Override
    public String getUpdatedAt()
    {
        return this.updatedAt;
    }

    @Override
    public void setUpdatedAt(String updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    @Override
    public String getExpiresAt()
    {
        return this.expiresAt;
    }

    @Override
    public void setExpiresAt(String expiresAt)
    {
        this.expiresAt = expiresAt;
    }

    @Override
    public Location getLocation()
    {
        return location;
    }

    @Override
    public void setLocation(Location location)
    {
        this.location = location;
    }

    @Override
    public Unit getUnit()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUnit(Unit unit)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleUpdate(SmartSantanderTrafficIntensityJson updatedData)
    {
        // cast the received data
        SmartSantanderTrafficIntensityJson updatedJson = (SmartSantanderTrafficIntensityJson) updatedData;

        // get the measures
        if(updatedJson.getCount() != null)
            this.count = updatedJson.getCount();
        if(updatedJson.getOccupancy() != null)
            this.occupancy = updatedJson.getOccupancy();
        if(updatedJson.getMedian_speed() != null)
            this.medianSpeed = updatedJson.getMedian_speed();
        if(updatedJson.getAverage_speed() != null)
            this.averageSpeed = updatedJson.getAverage_speed();
        if(updatedJson.getDate() != null)
            this.dateLastMeasurement = updatedJson.getDate();
        HashMap<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("getCount", this.getCount());
        valuesMap.put("getOccupancy", this.getOccupancy());
        valuesMap.put("getAverageSpeed", this.getAverageSpeed());
        valuesMap.put("getMedianSpeed", this.getMedianSpeed());
        PWALNewDataAvailableEvent event = new PWALNewDataAvailableEvent(
                this.updatedAt, this.getPwalId(), this.getExpiresAt(),
                valuesMap, this);
        log.debug("Device {} is publishing new data available event on topic: {}", this.getPwalId(), this.eventPublisher.getTopics());
        this.eventPublisher.publish(event);
    }

    @Override
    public String getNetworkLevelId()
    {
        // TODO Auto-generated method stub
        return this.id;
    }
}
