package eu.linksmart.api.event.datafusion;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public interface Deserializer<T> {
    T parse(String string);
    T deserialize(byte[] bytes);
    void close();
}
