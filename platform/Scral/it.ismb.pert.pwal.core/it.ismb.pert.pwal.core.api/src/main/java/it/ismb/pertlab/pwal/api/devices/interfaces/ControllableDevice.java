package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface ControllableDevice extends Device {

	 //getCommandMap
	@JsonIgnore
    HashMap<String, AbstractCommand> getSupportedCommand();
}
