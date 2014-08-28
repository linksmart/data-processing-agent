package eu.linksmart.api.event;

public interface EventSubscriptionWrapper {
	public static final String DESCRIPTION = "EventSubscriber";
	public static final String SID = "EventSubscriber";
	/**
	 * Initiates a search for an EventManager
	 * @param serviceID service ID of calling component
	 * @param eventManagerPID name of EventManager to look for
	 */
	public void findEventManager(String serviceID, String eventManagerPID);
	/**
	 * Registers a subscriber as service at the NetworkManager so that this component can
	 * be notified by the EventManager
	 * @param subscriber Component which shall be notified
	 * @param serviceID service ID of calling component
	 */
	public void registerCallback(EventSubscriber subscriber, String serviceID);
	/**
	 * Deregisters service from NetworkManager
	 * @param serviceID service ID of calling component
	 */
	public void deregisterCallback(String serviceID);
	/**
	 * Subscribe to a certain topic at the EventManager which was triggered to be looked 
	 * for by a former call of findEventManager(String serviceID, String eventManagerPID)
	 * The EventManager notifies the component which was register by a former call of 
	 * registerCallback(EventSubscriber subscriber, String serviceID)
	 * @param serviceID service ID of calling component
	 * @param topic topic to subscribe to
	 */
	public void subscribeWithTopic(String serviceID, String topic);
	
	/**
	 * Un-Subscribe a certain topic at the EventManager which was subscribed  
	 * by a former call ofsubscribeWithTopic(String serviceID, String topic)
	 * The EventManager will delete the subscription and will not notify the serviceID
	 * @param serviceID service ID of subscriber
	 * @param topic topic to un-subscribe to
	 */
	public void unsubscribeTopic(String serviceID, String topic);
	
	/**
	 * Un-Subscribe all topics at the EventManager which was subscribed 
	 * by a former call ofsubscribeWithTopic(String serviceID, String topic) with the same serviceID
	 * The EventManager will delete all subscriptions and will not notify the serviceID	 
	 * @param serviceID service ID of calling component	
	 */
	public void unsubscribeAllTopics(String serviceID);

}