package it.ismb.pertlab.pwal.xivelymanager.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xively.client.XivelyService;
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.model.Datastream;

import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.xivelymanager.utils.Utils;

/**
 * Class used to drive an humidity sensor via xively
 *
 */
public class HumidityDevice implements HumiditySensor {

	private static final Logger LOG = LoggerFactory.getLogger(HumidityDevice.class);
	
	private String id;
	private String updatedAt;
	private Location location;
	private Unit unit;
	private final String type=DeviceType.HUMIDITY_SENSOR;
	private DatastreamRequester req;
	private String streamId;
	
	/**
	 * Constructor of the temperature sensor controlled using xively
	 * 
	 * @param feedID
	 *           id of the feed containing the datastream 
	 * 
	 * @param streamID
	 *           id of the datastream of the sensor
	 */
	public HumidityDevice(Integer feedID, String streamID) {
		req = XivelyService.instance().datastream(feedID);
		this.streamId = streamID;
		id = feedID+streamID;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return this.type;
	}
	
	@Override
	public String getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public Unit getUnit() {
		return unit;
	}


	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	@Override
	public Double getHumidity() {
		Datastream stream = req.get(streamId);
		if(stream.getValue()!=null) {
			if(stream.getUpdatedAt()!=null) {
				this.setUpdatedAt(stream.getUpdatedAt());
			}
			if(stream.getUnit()!=null) {
				this.setUnit(Utils.convertUnit(stream.getUnit()));
			}
			LOG.info("Humidity value for "+stream.getId()+
					" "+stream.getValue()+
					((stream.getUnit()!=null) ? " "+stream.getUnit().getSymbol() : "") +
					" updated at: "+stream.getUpdatedAt());
			return Double.valueOf(stream.getValue());
		} else {
			LOG.error("Current value for "+stream.getId()+" not available");
			return 0.0;
		}
	}	
}
