package eu.linksmart.api.event.datafusion;



import java.util.Map;

/**
 * Interfaces which represents the API of the Complex Event Handler (CEH).
 * 
 * The CEH make an abstraction between the CEP-Engine/s and the action taken by the Handler.<p>
 * In this case the action of the fusion of events is a "complex event" which is broker in the same manner as the events which trigger
 *  the complex event.<p>
 *  
 * The CEH have two duties. One is the action taken after the event was triggered (the handling of the event).<p>
 * The second is the interaction of {@link CEPEngine} and the event handler. This interface is the API of this second responsibility.
 * 
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see CEPEngine
 * 
 * */

public interface ComplexEventHandler extends AnalyzerComponent{



    /**
     * This function is called after as a response of a Statement from an CEP engine.
     *
     * @param eventMap the result of a statement
     *
     * */
    public void update(Map eventMap);

    /***
     *
     * Terminate the Handler, releasing any resource us by it.
     *
     * */
    public void destroy();


}
