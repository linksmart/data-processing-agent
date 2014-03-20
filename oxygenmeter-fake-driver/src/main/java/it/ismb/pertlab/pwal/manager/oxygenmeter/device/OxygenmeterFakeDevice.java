package it.ismb.pertlab.pwal.manager.oxygenmeter.device;

import it.ismb.pertlab.pwal.api.devices.model.OxyMeter;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;

public class OxygenmeterFakeDevice implements OxyMeter {

	private String id;
	private final String type=DeviceType.OXYGEN_METER;
	
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
	public Integer getSaturation() {
		return (int) (((100-95)*Math.random())+95);
	}

}
