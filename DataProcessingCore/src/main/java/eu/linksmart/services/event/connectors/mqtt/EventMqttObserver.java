package eu.linksmart.services.event.connectors.mqtt;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.testing.tooling.CounterTask;

import java.util.List;
import java.util.Timer;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {
    protected CounterTask eventReporter =  null;
    public EventMqttObserver(List<String> topics) {
        super(topics);

        if(conf.containsKeyAnywhere(Const.MONITOR_EVENTS))
            (new Timer()).schedule(eventReporter= new CounterTask(loggerService,conf.containsKeyAnywhere(Const.MONITOR_TOPICS) && conf.getBoolean(Const.MONITOR_TOPICS)), 0, conf.getInt(Const.MONITOR_EVENTS) *1000);

    }
    public EventMqttObserver(String topic)  {
        super(topic);
        if(conf.containsKeyAnywhere(Const.MONITOR_EVENTS))
            (new Timer()).schedule(eventReporter = new CounterTask(loggerService, conf.containsKeyAnywhere(Const.MONITOR_TOPICS) && conf.getBoolean(Const.MONITOR_TOPICS)), 0, conf.getInt(Const.MONITOR_EVENTS) *1000);


    }
    private long lastValue = 0, nMessages = 0;

    protected void mangeEvent(String topic,byte[] rawEvent) {
      //  logMessagePerSecond();
        try {

            EventFeeder.feed(topic,rawEvent);
            if(eventReporter!=null)
                eventReporter.newEventInTopic(topic);

        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            //publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }


    }


}
