package it.ismb.pertlab.pwal.api.devices.commands.impl;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.Meter;

import java.util.HashMap;

public class TurnOnMeter extends AbstractCommand {

	Meter device;
	 
	public TurnOnMeter(Meter device) {
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
	}
}
