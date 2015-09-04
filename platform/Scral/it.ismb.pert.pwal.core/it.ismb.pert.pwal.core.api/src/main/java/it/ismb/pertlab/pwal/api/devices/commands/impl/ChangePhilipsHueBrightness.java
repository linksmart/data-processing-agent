package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

public class ChangePhilipsHueBrightness extends AbstractCommand {

	PhilipsHue device;
	int brightness;

	public ChangePhilipsHueBrightness(PhilipsHue device) {
		this.device = device;
		this.setCommandName("changeBrightness");
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	@Override
	public void execute() {
		this.device.setBrightness(this.brightness);
	}

	@Override
	public void setParams(HashMap<String, Object> params) {
		Object brightness_obj = params.get("brightness");
		if (brightness_obj != null)
		{
			if(brightness_obj instanceof Integer)
			{
				this.brightness = (int) brightness_obj;
				if(this.brightness <0 || this.brightness > 254)
					throw new IllegalArgumentException(
							"Brightness must be an Integer value between 1 and 254");
			}
			else
				throw new IllegalArgumentException(
						"Brightness must be an Integer value");
		}
		else
			throw new IllegalArgumentException("Brightness key not found.");
	}
}
