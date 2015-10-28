package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.PhilipsHue;

public class TurnOffPhilipsHue extends AbstractCommand {

	PhilipsHue device;
	 
	public TurnOffPhilipsHue(PhilipsHue device) {
		this.device = device;
		this.setCommandName("turnOff");
	}
	@Override
	public void execute() {
		this.device.turnOff();
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
//		throw new UnsupportedOperationException();
	}

}
