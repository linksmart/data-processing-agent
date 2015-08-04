package eu.linksmart.api.event.datafusion.core;

/**
 * This interface represent the inner logic needed by the core of the API of the feeders. <p>
 * The EventsFeeder component make an abstraction layer between the event provider/s and the CEP-engines. <p>
 * This allows a separation of the CEP-Wrappers and the event brokers so one or the other can be exchange. <p>
 * This means if the event provider is change the event feeders should be change but not the different wrappers. <p>
 * <p>
 * Also this component create a single program logic in the management of several streams and topics regardless of a particular CEP-Engine implementation. <p>

 * 
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see DataFusionWrapper
 * 
 * */
public interface EventFeederLogic {
	/**
	 * Indicate to the Feeder to start feeding the CEP engine/s with events coming from a topic.
	 * 
	 * @param topic which the feeders must feed the CEP engine/s
	 * 
	 * @return <code>true</code> if the feeding is possible. <code>false</code> otherwise.
	 *  
	 * */
	public boolean subscribeToTopic(String topic);

}
