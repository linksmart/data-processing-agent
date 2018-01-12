package eu.linksmart.services.event.intern;

import eu.linksmart.services.utils.serialization.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 22.06.2016 a researcher of Fraunhofer FIT.
 */
public class SharedSettings implements Const {


    protected static String id = UUID.randomUUID().toString(), will = null, willTopic = null;

    protected static boolean isFirstLoad = true;

    protected static String ls_code = "DPA";
    protected static Serializer serializer = new DefaultSerializer();
    protected static Deserializer deserializer = new DefaultDeserializer();

    protected static JWSSerializer jwsserializer;
    protected static JWSDeserializer jwsdeserializer;
    protected static ConcurrentMap<String, Object> extensionSharedObjects = new ConcurrentHashMap<>();
    static  {
        try {
            jwsserializer = new JWSSerializer(serializer);

            jwsdeserializer = new JWSDeserializer(jwsserializer.getPublicKeyInBase64String(), deserializer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected static boolean isSet = false;

    public static String getWillTopic() {
        return AgentUtils.topicReplace(willTopic, "");
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


    public static void addSharedObject(String key, Object object){
        extensionSharedObjects.put(key,object);

    }

    public static boolean isFirstLoad() {
        return isFirstLoad;
    }
    public static Object getSharedObject(String key){
        return extensionSharedObjects.get(key);
    }

    public static boolean existSharedObject(String key) {
        return extensionSharedObjects.containsKey(key);
    }

    public static void isIsFirstLoad(boolean isFirstLoad) {
        SharedSettings.isFirstLoad = isFirstLoad;
    }

    public static String getLs_code() {
        return ls_code;
    }

    public static void setLs_code(String ls_code) {
        SharedSettings.ls_code = ls_code;
    }
}
