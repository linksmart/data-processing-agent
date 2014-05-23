package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class FillLevelSensorFit extends BaseSerialDevice implements FillLevel {

	protected static final Logger log=LoggerFactory.getLogger(FillLevelSensorFit.class);
	private String id;
	private String pwalId;
	private SerialManager manager;
	private Integer level;
	private Integer depth;
	
	public FillLevelSensorFit(SerialManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void messageReceived(String payload) {
		log.debug("Received message: "+payload);
		Gson gson = new Gson();
		FillLevelSensorFit values = gson.fromJson(payload, FillLevelSensorFit.class);
		this.depth = values.depth;
		this.level = values.level;
		log.debug("Payload json parsed: {}", values.toString());
	}

	@Override
	public String getPwalId() {
		return this.pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId = pwalId;
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
		return DeviceType.FILL_LEVEL_SENSOR;
	}

	@Override
	public Double getLatitude() {
		return null;
	}

	@Override
	public void setLatitude(Double latitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getLongitude() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLongitude(Double longitude) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNetworkType() {
		return manager.getNetworkType();
	}

	@Override
	public Integer getDepth() {
		return this.depth;
	}

	@Override
	public Integer getLevel() {
		return this.level;
	}

}
