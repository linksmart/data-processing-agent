package it.ismb.pertlab.pwal.connectors.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import it.ismb.pertlab.pwal.api.devices.events.DeviceLogger;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Gui {

	@Autowired
	private Pwal pwal;
	
	@RequestMapping(value="gui", method=RequestMethod.GET)
	public String renderTheGui(Model model)
	{
		Collection<Device> devlist = pwal.getDevicesList();
		ArrayList<DeviceLogger> loglist = pwal.getDeviceLogList();
		
		for (Device d: devlist)
		{
		    //System.out.println(itr.next());
		    System.err.println("\n"+d.getId()+" "+d.getType()+ " "+d.getNetworkType()+"\n");
		}
		
		for (DeviceLogger listlog : loglist){
			System.err.println("\n Log "+listlog.Date+"Msg:"+listlog.LogMsg+"\n");
		}
		
		model.addAttribute("devlist", devlist);
		model.addAttribute("loglist", loglist);
		
		return "gui";
	}
	
	@RequestMapping(value="gui#sensor", method=RequestMethod.GET)
	@ResponseBody
	public String loadsensors(Model model)
	{
		Collection<Device> devlist = pwal.getDevicesList();
		
		model.addAttribute("devlist", devlist);
		
		return "gui#sensors";
	}
	
	@RequestMapping(value="logInfo", method=RequestMethod.GET)
	public @ResponseBody List<String> getLogInfo()
	{
		return Arrays.asList("gui","GUI","fdsfd");
	}
	
	
	
}
