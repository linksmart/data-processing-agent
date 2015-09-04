package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

public class TurnOnPhilipsHue extends AbstractCommand {

	PhilipsHue device;
	 
	public TurnOnPhilipsHue(PhilipsHue device) {
		this.device = device;
		this.setCommandName("turnOn");
	}
	@Override
	public void execute() {
		this.device.turnOn();
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
//		throw new UnsupportedOperationException();
	}

}
