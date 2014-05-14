package it.ismb.pertlab.pwal.manager.serial.device;

import it.ismb.pertlab.pwal.api.devices.model.Semaphore;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.serialmanager.BaseSerialDevice;
import it.ismb.pertlab.pwal.serialmanager.SerialManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArduinoSemaphore extends BaseSerialDevice implements Semaphore{
	
	protected static final Logger log=LoggerFactory.getLogger(ArduinoSemaphore.class);
	private String id;
	private Semaphore.State state;
	private SerialManager manager;
	
	public ArduinoSemaphore(SerialManager manager)
	{
		this.manager=manager;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id=id;
	}

	@Override
	public String getType() {
		return DeviceType.SEMAPHORE;
	}

	@Override
	public void messageReceived(String payload) {
		log.debug("Received message: "+payload);
		switch(payload.trim())
		{
			case "GREEN":
				this.state=Semaphore.State.GREEN;
				break;
			case "YELLOW":
				this.state=Semaphore.State.YELLOW;
				break;
			case "RED":
				this.state=Semaphore.State.RED;
				break;
			default:
				log.warn("Unable to understand the payload "+payload);
		}
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		manager.sendCommand(this.id,state.name());
	}

	@Override
	public Double getLatitude() {
		// TODO Auto-generated method stub
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

}
