/**
 * 
 */
package it.ismb.pertlab.pwal.mqtt.sample;

import it.ismb.pertlab.pwal.mqtt.MqttAsyncDispatcher;

/**
 * @author bonino
 *
 */
public class SampleMqttDispatcher
{
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException
	{
		//create the mqtt asynch dispatcher object
		MqttAsyncDispatcher dispatcher = new MqttAsyncDispatcher("tcp://test.mosquitto.org:1883", "pwal", "", "");
		
		//synchronously connect
		dispatcher.syncConnect();
		
		//the initial temperature
		double fakeTemperature = Math.random()*70.0 - 20.0;
		
		//start sending messages
		for(int i=0; i<25000; i++)
		{
			fakeTemperature = Math.random()*((Math.random() > 0.5)?1:-1)+fakeTemperature;
			
			dispatcher.publish("temp/random", (""+fakeTemperature).getBytes());
			
			Thread.sleep(100);
		}
		
		//disconnect
		dispatcher.syncDisconnect();
	}
	
}
