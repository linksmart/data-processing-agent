package eu.linksmart.services.event.intern;

import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 22.06.2016 a researcher of Fraunhofer FIT.
 */
public class SharedSettings implements Const {


    protected static String id = UUID.randomUUID().toString();

    protected static String will = null;
    protected static String willTopic = null;
    protected static Serializer serializer = new DefaultSerializer();
    protected static Deserializer deserializer = new DefaultDeserializer();
    protected static boolean isSet = false;

    public static String getWillTopic() {
        return willTopic;
    }

    public static void setWillTopic(String willTopic) {
        SharedSettings.willTopic = willTopic;
    }

    public static String getWill() {
        return will;
    }

    public static void setWill(String will) {
        SharedSettings.will = will;
    }

    private SharedSettings() {
    }

    public static void setIsSet(boolean isSet) {
        SharedSettings.isSet = isSet;
    }

    public static void setId(String id) {
        isSet =true;
        SharedSettings.id = id;
    }


    public static String getId() {
        return id;
    }


    public static Serializer getSerializer() {
        return serializer;
    }

    public static void setSerializer(Serializer serializer) {
        SharedSettings.serializer = serializer;
    }

    public static Deserializer getDeserializer() {
        return deserializer;
    }

    public static void setDeserializer(Deserializer deserializer) {
        SharedSettings.deserializer = deserializer;
    }
}
