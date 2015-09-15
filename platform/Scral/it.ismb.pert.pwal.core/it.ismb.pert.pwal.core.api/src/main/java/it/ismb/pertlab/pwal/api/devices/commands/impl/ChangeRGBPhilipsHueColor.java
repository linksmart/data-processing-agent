package it.ismb.pertlab.pwal.api.devices.commands.impl;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

import java.util.HashMap;

public class ChangeRGBPhilipsHueColor extends AbstractCommand {

	private PhilipsHue device;
	private int r;
	private int g;
	private int b;

	public ChangeRGBPhilipsHueColor(PhilipsHue device) {
		this.device = device;
		this.setCommandName("changeRGBColor");
	}

	@Override
	public void execute() {
		this.device.setRGBColor(this.r, this.g, this.b);
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
		Object r_obj = params.get("R");
		Object g_obj = params.get("G");
		Object b_obj = params.get("B");
		
		if (r_obj instanceof Integer && g_obj instanceof Integer
				&& b_obj instanceof Integer) {
			this.r = (int) r_obj;
			this.g = (int) g_obj;
			this.b = (int) b_obj;
			
			if(!(this.r >= 0 && this.r < 256 && this.g >= 0 && this.g < 256 && this.b >= 0 && this.b < 256))
				throw new IllegalArgumentException("R, G and B must be Integer values between 0 and 255");
		}
		else
			throw new IllegalArgumentException("R, G and B must be Integer values");
	}
}
