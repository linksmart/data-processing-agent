package it.ismb.pertlab.pwal.connectors.datamodel;

import java.util.ArrayList;
import java.util.List;

public class DeviceCommand {
	
	private String methodName;
	private List<String> params = new ArrayList<>();
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
}
