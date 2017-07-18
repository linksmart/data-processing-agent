package eu.linksmart.api.event.types;

import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;

/**
 * Created by José Ángel Carvajal on 21.06.2017 a researcher of Fraunhofer FIT.
 */
public interface SerializationFactory {
    Serializer getSerializer();
    Deserializer getDeserializer();
}
