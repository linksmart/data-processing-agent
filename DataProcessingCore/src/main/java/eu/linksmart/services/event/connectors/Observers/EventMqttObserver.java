package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.feeder.EventFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {


    public EventMqttObserver() {
        super();
        loggerService.info("The Agent(ID:"+ DynamicConst.getId()+") with incoming events broker alias: "+conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH)+"  URL: " + (new BrokerConfiguration(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH)).getURL()));
        loggerService.info("The Agent(ID:"+DynamicConst.getId()+") waiting for events from the topic: " + conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH) );

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
