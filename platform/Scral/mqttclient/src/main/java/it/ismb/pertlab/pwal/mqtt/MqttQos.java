/**
 * 
 */
package it.ismb.pertlab.pwal.mqtt;

/**
 * An enumeration representing valid QoS values as defined by the MQTT standard.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public enum MqttQos
{
	AT_MOST_ONCE ((int)0),
	AT_LEAST_ONCE ((int)1),
	EXACTLY_ONCE ((int)2);
	
	
	private int qos;
	
	private MqttQos(int qos)
	{
		this.qos = qos;
	}
	
	public int getQoS()
	{
		return this.qos;
	}
	
}
