package eu.linksmart.gc.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;

/**
 * Created by José Ángel Carvajal on 18.12.2015 a researcher of Fraunhofer FIT.
 */
public  abstract class GsonSerializable<T> implements Gerializable<T> {

    protected Gson gson;

    protected final Class<? extends GsonSerializable> typeParameterClass;
    public GsonSerializable() {
        typeParameterClass = this.getClass();
        gson = getGsonSerializer();

    }

    public static Gson GsonSerializer() throws NotImplementedException{
        return GsonBuilder().create();
    }
    public static GsonBuilder GsonBuilder() throws NotImplementedException{
        return new GsonBuilder();
    }

    public static GsonBuilder GsonBuilder(GsonBuilder gsonBuilder) throws NotImplementedException{
        return gsonBuilder;
    }
    public static String Serializer(Object object) throws NotImplementedException{
        return GsonSerializer().toJson(object);
    }
    public static Object Deserializer(String object) throws NotImplementedException{
        return GsonSerializer().fromJson(object,object.getClass());
    }
    public abstract Gson getGsonSerializer();

    public T deserialize(String json){
        return (T)gson.fromJson(json,typeParameterClass);
    }

    public String serialize(){
        return gson.toJson(this);
    }

}
