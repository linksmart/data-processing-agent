/**
 * 
 */
package it.ismb.pertlab.pwal.api.devices.commands.impl;

import java.util.HashMap;

import it.ismb.pertlab.pwal.api.devices.commands.internal.AbstractCommand;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;

/**
 * @author bonino
 *
 */
public class SetSpeed extends AbstractCommand
{
	// the device to which this command is associated
	private WaterPump theDevice;
	
	// the speed to set
	private double speed;
	
	public SetSpeed(WaterPump device)
	{
		// store a pointer to the device offering this command
		this.theDevice = device;
		
		// set the speed as non valid
		this.speed = -1;
		
		// store the command name
		this.setCommandName("setSpeed");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.commands.internal.Command#execute()
	 */
	@Override
	public void execute()
	{
		if (speed >= 0)
			// call the device method
			this.theDevice.setSpeed(this.speed);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.pwal.api.devices.commands.internal.Command#setParams(
	 * java.util.HashMap)
	 */
	@Override
	public void setParams(HashMap<String, Object> params) throws IllegalArgumentException
	{
		try
		{
			// get the speed param
			double speed = (Double) params.get("value");
			
			// store the speed
			this.speed = speed;
		}
		catch (Exception e)
		{
			//non-valid speed
			this.speed = -1;
			
			// log the error
			throw new IllegalArgumentException("Required value must be a double precision number");
		}
	}
	
}
