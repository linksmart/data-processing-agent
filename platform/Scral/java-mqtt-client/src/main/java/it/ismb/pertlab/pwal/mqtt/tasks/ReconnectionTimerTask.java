/**
 * 
 */
package it.ismb.pertlab.pwal.mqtt.tasks;

import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

import java.util.TimerTask;

/**
 * @author bonino
 *
 */
public class ReconnectionTimerTask extends TimerTask
{
	
	//the dispatcher instance
	private MqttAsyncDispatcher dispatcher;
	
	//the sync connection flag
	private boolean sync = false;
	
	/**
	 * 
	 */
	public ReconnectionTimerTask(MqttAsyncDispatcher dispatcher, boolean sync)
	{
		// store a reference to the dispatcher
		this.dispatcher = dispatcher;
		
		//store the synchronous connection flag
		this.sync = sync;
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		if(this.sync)
		{
			//connect
			this.dispatcher.syncConnect();
		}
		else
		{
			this.dispatcher.connect();
		}
		
		
	}
	
}
