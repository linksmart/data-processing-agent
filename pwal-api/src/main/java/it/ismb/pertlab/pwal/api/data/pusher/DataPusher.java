package it.ismb.pertlab.pwal.api.data.pusher;

import java.util.Timer;
import java.util.TimerTask;

public abstract class DataPusher extends TimerTask {
	
	protected Timer dataTimer = new Timer();
	protected int seconds;
	
	public DataPusher(int seconds)
	{
		this.seconds = seconds;
		
	}
	
	protected Timer getTimer()
	{
		return this.dataTimer;
	}
	
	public void startTimer(int tickSeconds)
	{
		this.getTimer().cancel();
		this.getTimer().schedule(this, tickSeconds);
	}
	
	public void stopTimer()
	{
		this.getTimer().cancel();
	}
}
