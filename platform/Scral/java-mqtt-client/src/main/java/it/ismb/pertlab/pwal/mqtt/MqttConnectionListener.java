/**
 * 
 */
package it.ismb.pertlab.pwal.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * @author bonino
 *
 */
public class MqttConnectionListener implements IMqttActionListener
{
	private MqttAsyncDispatcher theDispatcher;
	
	public MqttConnectionListener(MqttAsyncDispatcher dispatcher)
	{
		this.theDispatcher = dispatcher;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttActionListener#onFailure(org.eclipse
	 * .paho.client.mqttv3.IMqttToken, java.lang.Throwable)
	 */
	@Override
	public void onFailure(IMqttToken arg0, Throwable arg1)
	{
		this.theDispatcher.setConnected(false);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttActionListener#onSuccess(org.eclipse
	 * .paho.client.mqttv3.IMqttToken)
	 */
	@Override
	public void onSuccess(IMqttToken arg0)
	{
		this.theDispatcher.setConnected(true);
	}
	
}
