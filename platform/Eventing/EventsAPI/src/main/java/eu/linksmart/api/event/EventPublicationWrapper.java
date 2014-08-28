package eu.linksmart.api.event;

import java.rmi.RemoteException;
import eu.linksmart.utils.Part;

public interface EventPublicationWrapper {
	public static final String DESCRIPTION = "EventPublisher";
	public static final String SID = "EventPublisher";
	/**
	 * Initiates a search for an EventManager
	 * @param serviceID service ID of calling component
	 * @param eventManagerPID name of EventManager to look for
	 */
	public void findEventManager(String serviceID, String eventManagerPID);
	/**
	 * Adds a timestamp and publishes an event via the EventManager that the calling component
	 * has triggered a search before
	 * @param serviceID service ID of calling component
	 * @param topic Topic of event
	 * @param valueParts content of event without timestamp
	 * @return success of publishing
	 * @throws RemoteException EventManager could not be found (>LS1.3: only return is used. throws is only kept for 
	 * compatibility reasons)
	 */
	public boolean publishEvent(String serviceID, String topic, Part[] valueParts) throws RemoteException;
	/**
	 * Indicates if a particular EventManager was found
	 * @param serviceID service ID of calling component
	 * @return true if the EventManager which was triggered to be looked for by a former call of
	 * findEventManager(String serviceID, String eventManagerPID) using serviceID was found
	 * false otherwise
	 */
	public boolean isEventManagerLocated(String serviceID);
}