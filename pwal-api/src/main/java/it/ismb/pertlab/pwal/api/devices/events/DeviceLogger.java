package it.ismb.pertlab.pwal.api.devices.events;

public class DeviceLogger {
	
	public String Date;
	public String LogMsg;
	
	public DeviceLogger (String Date, String LogMsg){
		this.Date = Date;
		this.LogMsg= LogMsg;
	}
	
	public String getDate(){
		return this.Date;
	}
	
	public String getLogMsg(){
		return this.LogMsg;
	}
	
}
