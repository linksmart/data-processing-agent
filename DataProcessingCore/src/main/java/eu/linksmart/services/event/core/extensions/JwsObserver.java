package eu.linksmart.services.event.core.extensions;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.connectors.observers.EventMqttObserver;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.intern.SharedSettings;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 16.10.2017 a researcher of Fraunhofer FIT.
 */
public class JwsObserver extends EventMqttObserver {
    private Set<String> knownTopic = new HashSet<>();
    private final KeyRetriever retriever = KeyRetriever.getDefaultRetriever();

    public JwsObserver(List<String> topics) {
        super(topics);
    }

    public JwsObserver(String topic) {
        super(topic);
    }

    @Override
    protected void mangeEvent(String topic, byte[] rawEvent) {
        try {
            if(!knownTopic.contains(topic)) {
                KeyRetriever.getDefaultRetriever().retrieveKey();
                for (String id : KeyRetriever.getDefaultRetriever().idsKey.keySet())
                    if (topic.contains(id)) {
                        JWS.getDeserializer().defineClassToInterface(topic, retriever.idsKey.get(id));
                        knownTopic.add(topic);
                        break;
                    }
            }
            if(knownTopic.contains(topic)) {
                EventFeeder.feed(topic, JWS.getDeserializer().unpack(rawEvent, topic));

                if (eventReporter != null)
                    eventReporter.newEventInTopic(topic);
            }else
                loggerService.warn("Message in topic "+topic+" arrived but no key is know to verify it!");
        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            //publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(new UntraceableException(SharedSettings.getId(),e));
        }
    }
}
