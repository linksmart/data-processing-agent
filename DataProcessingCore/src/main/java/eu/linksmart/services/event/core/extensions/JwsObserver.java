package eu.linksmart.services.event.core.extensions;

import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.JWSDeserializer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 16.10.2017 a researcher of Fraunhofer FIT.
 */
public class JwsObserver extends EventMqttObserver {



    public JwsObserver(List<String> topics) {
        super(topics);
    }

    public JwsObserver(String topic) {
        super(topic);
    }

    @Override
    protected void mangeEvent(String topic, byte[] rawEvent) {
        try {
            boolean keyFound =false;
            if(!SharedSettings.existSharedObject(topic))
                for( String id :((Map<String,String>)SharedSettings.getSharedObject(JWS.publicKeys)).keySet())
                    if(topic.contains(id)) {
                        ((JWSDeserializer) SharedSettings.getSharedObject(JWS.deserializerName)).defineClassToInterface(topic, ((Map<String, String>) SharedSettings.getSharedObject(JWS.publicKeys)).get(id));
                        keyFound =true;
                        break;
                    }
            if(keyFound) {
                EventFeeder.feed(topic, ((JWSDeserializer) SharedSettings.getSharedObject(JWS.deserializerName)).unpack(rawEvent, topic));

                if (eventReporter != null)
                    eventReporter.newEventInTopic(topic);
            }
            else
                loggerService.warn("Message in topic "+topic+" arrived but no key is know to verify it!");
        } catch (TraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        }catch ( UntraceableException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(e);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
            publishFeedback(new InternalException(topic,SharedSettings.getId(),e));
        }
    }
}
