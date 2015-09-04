package it.ismb.pertlab.pwal.api.devices.commands.internal;

import java.util.HashMap;

public interface Command {
	void execute();
	void setParams(HashMap<String, Object> params) throws IllegalArgumentException;
}
