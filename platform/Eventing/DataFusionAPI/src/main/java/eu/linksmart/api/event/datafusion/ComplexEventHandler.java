package eu.linksmart.api.event.datafusion;


/**
 * One of the two Interfaces which represents the API of the Complex Event Handler (CEH).
 * 
 * The CEH make an abstraction between the CEP-Engine/s and the action taken by the Handler.<p>
 * In this case the action of the fusion of events is a "complex event" which is publish in the same manner as the events which trigger
 *  the complex event.<p>
 *  
 * The CEH have two duties. One is the action taken after the event was triggered (the handling of the event).<p>
 * The second is the interaction of {@link DataFusionWrapper} and the event handler. This interface is the API of this second responsibility.   
 * 
 * @author Jos� �ngel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see DataFusionWrapper
 * 
 * */

public interface ComplexEventHandler {

	/**
	 * The CEH do not have an awareness of which engines are available. <p>
	 * For the Handler is enable to interact with a Data Fusion Engine,
	 * the wrapper of the engine has to explicitly subscribe to the handler as a Data Fusion engine.<p>
	 * Doing so through this function
	 * @param  dfw is the {@link DataFusionWrapper} which what to be subscribed.
	 * 
	 * @return <code>true</code> in a successful subscription, <code>false</code> otherwise.
	 * */
	public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw);
	/**
	 * If a subscribed want to not be handled any more then have to indicated explicitly. 
	 * @param dfw is the {@link DataFusionWrapper} which what to be unsubscribed.
	 * 
	 * @return <code>true</code> in a successful unsubscription, <code>false</code> otherwise.
	 * */
	public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw);
	/**
	 * Function for send back the complex event to the complex event handler
	 * 
	 * @return <code>true</code> if the event was handled successfully , <code>false</code> otherwise.
	 * */
	public boolean callerback(ResponseSet answer);
}
