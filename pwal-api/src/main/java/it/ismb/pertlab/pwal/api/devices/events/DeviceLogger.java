package it.ismb.pertlab.pwal.api.devices.events;

/***
 * A class that can be used to register Log events coming from the PWAL
 * @author Prabhakaran Kasinathan
 *
 */
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
