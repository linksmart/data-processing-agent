package eu.linksmart.api.event.datafusion.core;


/**
 * One of the two Interfaces which represents the API of the Complex Event Handler (CEH).
 *
 * The CEH make an abstraction between the CEP-Engine/s and the action taken by the Handler.<p>
 * In this case the action of the fusion of events is a "complex event" which is broker in the same manner as the events which trigger
 *  the complex event.<p>
 *
 * The CEH have two duties. One is the action taken after the event was triggered (the handling of the event).<p>
 * The second is the interaction of  {@link eu.linksmart.api.event.datafusion.DataFusionWrapper} and the event handler. This interface is the API of the first responsibility.
 *
 * @author José Ángel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see  eu.linksmart.api.event.datafusion.DataFusionWrapper
 *
 * */

public interface ComplexEventHandlerLogic {
    /**
     * Add a handler to the requested query.<p>
     *
     * NOTE: currently there is no abstract data fusion language, therefore the query sent is a engine specific and represented by a raw string, this will change along with this function!
     *
     * @param name of the query. This is how the query will be addressed. <p> Also name indicate where is going to be published when the query is triggered e.g. my.event.query the event will be broker in /my/event/query/
     * @param query as String for the specific CEP engine (temporal)
     * @param topic where the query will be deployed
     *
     * @return <code>true</code> if the query was deployed successfully. <code>false</code> otherwise.
     *
     * */
    public boolean  addHandler(String name, String query, String[] topic);

    /// Temporal
    public boolean pauseQuery(String name);
    /// Temporal
    public boolean removeQuery(String name);
    /// Temporal
    public boolean startQuery(String name);

}