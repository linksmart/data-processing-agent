package eu.linksmart.gc.api.network;

/**
 * Holds references of {@link MessageProcessor} objects
 * and passes them received {@link Message} of topic
 * their subscribed to. 
 * @author Vinkovits
 *
 */
public interface MessageDistributor {

	/**
	 * Method to subscribe to {@link Message} of specific topic
	 * @param topic String name of topic to listen to
	 */
	void subscribe(String topic, MessageProcessor observer);
	
	/**
	 * Removes observer from {@link MessageDistributor}'s list.
	 * @param observer
	 */
	void unsubscribe(String topic, MessageProcessor observer);
}
