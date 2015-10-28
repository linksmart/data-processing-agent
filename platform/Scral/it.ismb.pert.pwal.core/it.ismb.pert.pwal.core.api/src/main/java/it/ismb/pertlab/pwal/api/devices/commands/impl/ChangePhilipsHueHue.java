package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

public class ChangePhilipsHueHue extends AbstractCommand {

	private PhilipsHue device;
	private Integer hue;
	
	public ChangePhilipsHueHue(PhilipsHue device) {
		this.device = device;
		this.setCommandName("changeHue");
	}
	
	@Override
	public void execute() {
		if(this.hue != null)
			this.device.setHue(this.hue);
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
		Object saturation_obj = params.get("hue");
		if (saturation_obj != null) {
			if (saturation_obj instanceof Integer) {
				this.hue = (int) saturation_obj;
				if (this.hue < 0 || this.hue > 65536)
					throw new IllegalArgumentException(
							"Hue must be an Integer value between 0 and 65535");
			} else
				throw new IllegalArgumentException(
						"Hue must be an Integer value");
		} else
			throw new IllegalArgumentException("Hue key not found.");
	}
}
