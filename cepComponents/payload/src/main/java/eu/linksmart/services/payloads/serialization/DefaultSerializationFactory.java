package eu.linksmart.services.payloads.serialization;

import eu.linksmart.api.event.types.SerializationFactory;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;

/**
 * Created by José Ángel Carvajal on 21.06.2017 a researcher of Fraunhofer FIT.
 */
public class DefaultSerializationFactory implements SerializationFactory {
    @Override
    public Serializer getSerializer() {
        return new DefaultSerializer();
    }

    @Override
    public Deserializer getDeserializer() {
        return new DefaultDeserializer();
    }
}
