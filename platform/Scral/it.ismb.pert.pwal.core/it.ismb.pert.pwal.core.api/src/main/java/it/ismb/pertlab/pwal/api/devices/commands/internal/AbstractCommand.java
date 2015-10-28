package it.ismb.pertlab.pwal.api.devices.commands.internal;

public abstract class AbstractCommand implements Command {

	String commandName;
		
	public String getCommandName()
	{
		return this.commandName;
	}
	
	public void setCommandName(String commandName)
	{
		this.commandName = commandName;
	}
}
