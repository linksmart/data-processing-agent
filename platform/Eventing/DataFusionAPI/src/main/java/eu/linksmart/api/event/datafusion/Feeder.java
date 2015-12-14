package eu.linksmart.api.event.datafusion;

/**
 * This is the part of the API offered by EventFeeder. The EventsFeeder make an abstraction layer between the event provider/s and the CEP-engines.<p>
 * This allows a separation of the CEP-Wrappers and the event brokers so one or the other can be exchange.<p>
 * Also this component create a single program logic in the management of several streams and topics regardless of a particular CEP-Engine implementation<p>
 * 
 * This is the API offered to the CEP engine wrapper/s.
 * 
 * 
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see  DataFusionWrapper
 * 
 * */

public interface Feeder extends AnalyzerComponent {


	/**
	 * The feeder do not have an awareness of which engines are available. <p>
	 * For the feeder is enable to interact with a Data Fusion Engine,
	 * the wrapper of the engine has to explicitly subscribe to the feeder as a Data Fusion engine.<p>
	 * Doing so through this function
	 * @param dfw is the {@link DataFusionWrapper} which what to be subscribed.
	 * 
	 * @return <code>true</code> in a successful subscription, <code>false</code> otherwise.
	 * */
	public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw);
	/**
	 * If a subscribed want to not be fed of events any more then have to indicated explicitly. 
	 * @param dfw is the {@link DataFusionWrapper} which what to be unsubscribed.
	 * 
	 * @return <code>true</code> in a successful unsubscription, <code>false</code> otherwise.
	 * */
	public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw);
    /***
     *
     * Indicate if a Feeder is active or not
     *
     * @return <code>true</code> if the feeder is active, <code>false</code> otherwise.
     *
     * */
    public boolean isDown();

}
