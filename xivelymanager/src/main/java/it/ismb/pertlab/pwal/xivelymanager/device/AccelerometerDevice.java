package it.ismb.pertlab.pwal.xivelymanager.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xively.client.XivelyService;
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.model.Datastream;

import it.ismb.pertlab.pwal.api.devices.model.Accelerometer;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.xivelymanager.utils.AccelerationAxis;
import it.ismb.pertlab.pwal.xivelymanager.utils.Utils;

/**
 * Class used to drive an acceleremoter via xively
 *
 */
public class AccelerometerDevice implements Accelerometer {

	private static final Logger LOG = LoggerFactory.getLogger(AccelerometerDevice.class);
	
	private String id;
	private String updatedAt;
	private Location location;
	private Unit unit;
	private final String type = DeviceType.ACCELEROMETER;
	private DatastreamRequester req;
	private String [] streamIds;
	
	/**
	 * Constructor of the accelerometer controlled using xively
	 * 
	 * @param feedID
	 *           id of the feed containing the datastream 
	 * 
	 */
	public AccelerometerDevice(Integer feedID) {
		req = XivelyService.instance().datastream(feedID);
		id = feedID+streamIds[0];
	}

	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return type;
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

	
	/**
	 * Sets the stream ID for the datastream with the acceleration on the X axis
	 * 
	 * @param streamId
	 *              stream ID to set
	 */
	public void setStreamIDForAxisX(String streamId) {
		streamIds[AccelerationAxis.X.ordinal()] = streamId;
	}

	/**
	 * Sets the stream ID for the datastream with the acceleration on the Y axis
	 * 
	 * @param streamId
	 *              stream ID to set
	 */
	public void setStreamIDForAxisY(String streamId) {
		streamIds[AccelerationAxis.Y.ordinal()] = streamId;
	}

	/**
	 * Sets the stream ID for the datastream with the acceleration on the Y axis
	 * 
	 * @param streamId
	 *              stream ID to set
	 */
	public void setStreamIDForAxisZ(String streamId) {
		streamIds[AccelerationAxis.Z.ordinal()] = streamId;
	}

	
	@Override
	public Double getXAcceleration() {
		return getAcceleration(AccelerationAxis.X);
	}

	@Override
	public Double getYAcceleration() {
		return getAcceleration(AccelerationAxis.Y);
	}

	@Override
	public Double getZAcceleration() {
		return getAcceleration(AccelerationAxis.Z);
	}
	
	/**
	 * Method used to get the current value of acceleration on an axis
	 * 
	 * @param axis
	 *            the axis (X, Y or Z)
	 *            
	 * @return the last value measured
	 */
	private Double getAcceleration(AccelerationAxis axis) {
		Datastream stream = req.get(streamIds[axis.ordinal()]);
		if(stream.getValue()!=null) {
			if(stream.getUpdatedAt()!=null) {
				this.setUpdatedAt(stream.getUpdatedAt());
			}
			if(stream.getUnit()!=null) {
				this.setUnit(Utils.convertUnit(stream.getUnit()));
			}
			LOG.info("value of acceleration on axis "+axis.name()+" for "+stream.getId()+
					" "+stream.getValue()+
					((stream.getUnit()!=null) ? " "+stream.getUnit().getSymbol() : "") +
					" updated at: "+stream.getUpdatedAt());
			return Double.valueOf(stream.getValue());
		} else {
			LOG.error("Current value of acceleration on axis "+axis.name()+" for "+stream.getId()+" not available");
			return 0.0;
		}
	}


	@Override
	public Unit getUnit() {
		return unit;
	}


	@Override
	public void setUnit(Unit unit) {
		this.unit = unit;
	}	
}
