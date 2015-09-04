package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

public class ChangePhilipsHueSaturation extends AbstractCommand {

	private PhilipsHue device;
	private Integer saturation;
	
	public ChangePhilipsHueSaturation(PhilipsHue device) {
		this.device = device;
		this.setCommandName("changeSaturation");
	}
	
	@Override
	public void execute() {
		if(saturation != null)
			this.device.setSaturation(this.saturation);
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
		Object saturation_obj = params.get("saturation");
		if (saturation_obj != null) {
			if (saturation_obj instanceof Integer) {
				this.saturation = (int) saturation_obj;
				if (this.saturation < 0 || this.saturation > 254)
					throw new IllegalArgumentException(
							"Saturation must be an Integer value between 1 and 254");
			} else
				throw new IllegalArgumentException(
						"Saturation must be an Integer value");
		} else
			throw new IllegalArgumentException("Saturation key not found.");
	}
}
