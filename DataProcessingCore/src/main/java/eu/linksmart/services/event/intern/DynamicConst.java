package eu.linksmart.services.event.intern;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 22.06.2016 a researcher of Fraunhofer FIT.
 */
public class DynamicConst implements Const {


    protected static String id = UUID.randomUUID().toString();

    protected static String will = null;
    protected static String willTopic = null;

    protected static boolean isSet = false;

    public static String getWillTopic() {
        return willTopic;
    }

    public static void setWillTopic(String willTopic) {
        DynamicConst.willTopic = willTopic;
    }

    public static String getWill() {
        return will;
    }

    public static void setWill(String will) {
        DynamicConst.will = will;
    }

    private DynamicConst() {
    }

    public static void setIsSet(boolean isSet) {
        DynamicConst.isSet = isSet;
    }

    public static void setId(String id) {
        isSet =true;
        DynamicConst.id = id;
    }


    public static String getId() {
        return id;
    }




}
