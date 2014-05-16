package it.ismb.pertlab.pwal.connectors.gui;

import java.util.Arrays;
import java.util.List;

import it.ismb.pertlab.pwal.api.internal.Pwal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Gui {

	@Autowired
	private Pwal pwal;
	
	@RequestMapping(value="gui", method=RequestMethod.GET)
	public String renderTheGui()
	{
		return "gui";
	}
	
	@RequestMapping(value="logInfo", method=RequestMethod.GET)
	public @ResponseBody List<String> getLogInfo()
	{
		return Arrays.asList("gui","GUI","fdsfd");
	}
	
	
	
}
