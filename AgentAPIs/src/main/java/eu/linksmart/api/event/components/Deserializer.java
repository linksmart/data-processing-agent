package eu.linksmart.api.event.components;

import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public interface Deserializer {
    <T> T parse(String string, Class<T> tClass) throws IOException;
    <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException;
    void close();
}
