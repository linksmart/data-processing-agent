package eu.linksmart.api.event.components;

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
 * @see  CEPEngine
 * 
 * */

public interface Feeder extends AnalyzerComponent {


}
