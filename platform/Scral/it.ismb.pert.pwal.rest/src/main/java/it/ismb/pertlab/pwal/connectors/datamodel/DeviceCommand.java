package it.ismb.pertlab.pwal.connectors.datamodel;

import java.util.HashMap;


public class DeviceCommand {
	
	private String commandName;
	private HashMap<String, Object> params = new HashMap<String, Object>();
	
	public String getCommandName() {
		return commandName;
	}
	public void setCommandName(String methodName) {
		this.commandName = methodName;
	}
	public HashMap<String, Object> getParams() {
		return params;
	}
	public void setParams(HashMap<String, Object> params) {
		this.params = params;
	}
}
