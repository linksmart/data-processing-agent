package eu.linksmart.services.event.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.linksmart.api.event.components.Serializer;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultSerializer implements Serializer<Object> {

    protected ObjectMapper parser = new ObjectMapper();

    public DefaultSerializer() {

        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        parser.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
    }

    @Override
    public byte[] serialize(Object object) throws JsonProcessingException {
        return parser.writeValueAsString(object).getBytes();
    }

    @Override
    public String toString(Object object) throws JsonProcessingException {
        return parser.writeValueAsString(object);
    }

    @Override
    public void close() {

    }
}
