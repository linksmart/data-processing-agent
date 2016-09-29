package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.feeder.EventFeeder;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {


    public EventMqttObserver() {
        super();
    }

    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {
            EventFeeder.feed(topic,rawEvent);
        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }


    }


}
