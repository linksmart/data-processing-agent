package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.feeder.EventFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;
import eu.linksmart.testing.tooling.CounterTask;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {
    protected CounterTask eventReporter =  new CounterTask(loggerService);
    public EventMqttObserver(List<String> topics) {
        super(topics);
        loggerService.info("The Agent(ID:"+ DynamicConst.getId()+") with incoming events broker alias: "+conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH)+"  URL: " + (new BrokerConfiguration(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH)).getURL()));
        loggerService.info("The Agent(ID:"+DynamicConst.getId()+") waiting for events from the topic(s): " + topics.stream().collect(Collectors.joining(",")));

        (new Timer()).schedule(eventReporter, 0, 1000);

    }
    public EventMqttObserver(String topic)  {
        super(topic);
        loggerService.info("The Agent(ID:" + DynamicConst.getId() + ") with incoming events broker alias: " + conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH) + "  URL: " + (new BrokerConfiguration(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH)).getURL()));
        loggerService.info("The Agent(ID:"+DynamicConst.getId()+") waiting for events from the topic(s): " + topics.stream().collect(Collectors.joining(",")));
        (new Timer()).schedule(eventReporter, 0, 1000);


    }
    private long lastValue = 0, nMessages = 0;
    private synchronized void logMessagePerSecond(){
        nMessages++;
        if(lastValue ==0)
            lastValue = System.currentTimeMillis();
        if((  System.currentTimeMillis() - lastValue)/1000 >1.0) {
            loggerService.info("Messages per second are " + nMessages);
            nMessages =0;
            lastValue = System.currentTimeMillis();
        }


    }
    protected void mangeEvent(String topic,byte[] rawEvent) {
      //  logMessagePerSecond();
        try {

            EventFeeder.feed(topic,rawEvent);
            eventReporter.newEventInTopic(topic);

        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }


    }


}
