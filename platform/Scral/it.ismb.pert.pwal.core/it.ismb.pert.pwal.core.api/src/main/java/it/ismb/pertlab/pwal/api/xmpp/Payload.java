package it.ismb.pertlab.pwal.api.xmpp;

import java.util.ArrayList;
import java.util.List;

/*
 * POJO used to receive the indication of new devices
 * 
 */
public class Payload {
	private List<DevicePOJO> devices = null;
	private List<FunctionPOJO> functions = null;
	
	/**
	 * Returns the info of the new devices
	 * 
	 * @return the list of the new devices as {@link Device[]}
	 */
	public List<DevicePOJO> getDevices() {
		if(devices==null) {
			devices = new ArrayList<DevicePOJO>();
		}
		return devices;
	}

	
	/**
	 * Returns the functions of the new devices
	 * 
	 * @return the list of the the functions new devices as {@link Function[]}
	 */
	public List<FunctionPOJO> getFunctions() {
		if(functions==null) {
			functions = new ArrayList<>();
		}
		return functions;
	}
}
