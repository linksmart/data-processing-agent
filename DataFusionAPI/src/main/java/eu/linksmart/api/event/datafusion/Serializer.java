package eu.linksmart.api.event.datafusion;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public interface Serializer<T> {
    public byte[] serialize(T object) throws JsonProcessingException;
    public String toString(T object) throws JsonProcessingException;
    void close();
}
