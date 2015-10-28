package it.ismb.pertlab.pwal.api.devices.commands.impl;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.FakeControllableThermometer;

import java.util.HashMap;

public class ChangeFakeThermometerTemperature extends AbstractCommand {

	private FakeControllableThermometer device;
	private Double temperature;

	public ChangeFakeThermometerTemperature(FakeControllableThermometer device) {
		this.device = device;
		this.setCommandName("changeFakeTemperature");
	}

	@Override
	public void execute() {
		this.device.setTemperature(this.temperature);
	}

	@Override
	public void setParams(HashMap<String, Object> params)
			throws IllegalArgumentException {
		Object temp = params.get("temperature");

		if (temp instanceof Double) {
			this.temperature = (Double) temp;
		}
	}
}
