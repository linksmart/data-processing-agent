/**
 * 
 */
package it.ismb.pertlab.pwal.api.events.base;

import java.util.Date;

/**
 * @author bonino
 *
 */
public abstract class PWALEvent<T>
{
	// the event payload, may be a different class depending on the actual
	// implementation.
	@SuppressWarnings("unused")
	private T eventPayload;
	
	// the sender unique id (as a string, TODO: check if it would be better to
	// use a UUID)
	@SuppressWarnings("unused")
	private String senderId;
	
	// the event topic, may be used to filter undesired events
	// it should assume a hierarchical form based on the standard java inverse
	// package naming. For instance: it/ismb/pwal/event/real/temperature
	@SuppressWarnings("unused")
	private String topic;
	
	// the timestamp storing the instant at which the event has been created
	@SuppressWarnings("unused")
	private Date timestamp;

	/**
	 * 
	 * @param eventPayload
	 * @param senderId
	 * @param topic
	 * @param timestamp
	 */
	public PWALEvent(T eventPayload, String senderId, String topic, Date timestamp)
	{
		super();
		this.eventPayload = eventPayload;
		this.senderId = senderId;
		this.topic = topic;
		this.timestamp = timestamp;
	}
	
	
}
