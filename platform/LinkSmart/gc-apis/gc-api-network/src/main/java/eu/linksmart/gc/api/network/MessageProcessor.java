package eu.linksmart.gc.api.network;

/**
 * Receives and sends messages of specific topics
 * @author Vinkovits
 *
 */
public interface MessageProcessor {

	/**
	 * Processes the {@link Message} of topic this MessageObserver subscribed to.
	 * @param msg Message to process
	 * @return processed message or response message or null if it has been consumed
	 */
	Message processMessage(Message msg);
}
