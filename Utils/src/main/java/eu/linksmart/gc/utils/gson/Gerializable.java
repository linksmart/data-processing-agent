package eu.linksmart.gc.utils.gson;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public interface Gerializable<T> extends Serializable {
    public Gson getGsonSerializer();

    public T deserialize(String json);

    public String serialize();
}
