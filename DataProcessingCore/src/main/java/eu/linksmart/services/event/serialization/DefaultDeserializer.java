package eu.linksmart.services.event.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.components.Deserializer;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultDeserializer implements Deserializer{
    private ObjectMapper mapper = new ObjectMapper();
    public DefaultDeserializer(){
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
    }

    @Override
    public <T> T parse(String string, Class<T> tClass) throws IOException {
        return mapper.readValue(string, tClass);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        return mapper.readValue(bytes, tClass);
    }

    @Override
    public void close() {

    }
}
