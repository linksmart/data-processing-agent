package eu.linksmart.services.event.handler;

import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.utils.serialization.DefaultSerializerDeserializer;
import eu.linksmart.services.utils.serialization.Serializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 15.02.2019 a researcher of Fraunhofer FIT.
 */
public class EventAssuranceHandler extends BaseMapEventHandler implements ComplexEventPropagationHandler {

    private final Serializer serializer = new DefaultSerializerDeserializer();
    public EventAssuranceHandler(Statement statement) {
        super(statement);
    }


    @Override
    protected void processMessage(Map[] events) {


    }

    @Override
    protected void processLeavingMessage(Map[] events) {
        processMessage(events);
    }
}
