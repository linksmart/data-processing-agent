package eu.linksmart.api.event.datafusion;

import java.util.Hashtable;
import java.util.Map;

/**
 * 
 * The DF wrapper is an Interface . The Interface provide API in which the several engines connect to the Almanac framework. 
 * This API can be accessed natively in a OSGi environment or by Web Services,
 *  allowing the a high decupled infrastructure in which any engine could be added so long implement a Wrapper which interact through Web Services. 
 * 
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see  DataFusionWrapper
 * 
 * */


public interface DataFusionWrapper {

    static final public Map<String,DataFusionWrapper> instancedEngines= new Hashtable<String, DataFusionWrapper>();
	/**
	 * Return the name of the CEP which implement the interface
	 * 
	 * @return Name of the CEP engine wrapped
	 * 
	 * */
	public String getName();
	/**
	 * Add send and event to the CEP engine 
	 *
	 * @param event is the event itself. A map of values and values names
	 * 
	 * @return <code>true</code> if the event was added to the CEP engine. <code>false</code> otherwise.
	 * */
	public boolean addEvent(String topic, Object event, Class type);
	/**
	 * Configure a particular type in the engine.
	 * 
	 *  This functionality is consider deprecated due to the unclearness of this concept as general concept for all DataFusionWrapper.<p>
	 *  More research must be done about this to discard this feature or not. 
	 * 
	 * @param nameType are the names of type added in the engine
	 * @param eventSchema are the names of the inner values of the event
	 * @param eventTypes are the types of the inner values of the event
	 * 
	 * @return <code>true</code> if the event was added to the CEP engine. <code>false</code> otherwise.
	 * */
	@Deprecated
	public boolean addEventType(String nameType, String[] eventSchema, Object[] eventTypes);
	public boolean addEventType(String nameType,  Object type);
	/**
	 * Configure a particular type to a particular topic.<p>
	 * More research must be done about this to discard this feature or not. 
	 * 
	 * @param topic to be associated whit a topic.
	 * @param eventType is the pre-configured in the CEP engine type which will be associated with a topic. 
	 * 
	 * @return <code>true</code> if the topic was configurated to the CEP engine. <code>false</code> otherwise.
	 * */
	@Deprecated
	public boolean setTopicToEventType(String topic, String eventType);
	/**
	 * Add a query to the CEP engine which would be later handler whith a handler.<p>
	 * 
	 * NOTE: currently there is no abstract data fusion language, therefore the query sent is a engine specific, this will change along with this function!
	 * 
	 * @param query to add in the engine
	 * 
	 * @return <code>true</code> if the query is successfully deployed in the CEP engine. <code>false</code> otherwise.
	 * */
	public boolean addStatement( Statement query) throws StatementException;

    public boolean removeStatement( String id) throws StatementException;
    /***
     *
     * Terminate the Wrapper, releasing any resource us by it.
     *
     * */
    public void destroy();

    public Map<String,Statement> getStatements();


}
