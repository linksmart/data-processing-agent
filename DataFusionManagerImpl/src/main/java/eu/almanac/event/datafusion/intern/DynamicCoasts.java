package eu.almanac.event.datafusion.intern;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 22.06.2016 a researcher of Fraunhofer FIT.
 */
public class DynamicCoasts implements Const {


    protected static String id = UUID.randomUUID().toString();

    public static void setIsSet(boolean isSet) {
        DynamicCoasts.isSet = isSet;
    }

    protected static boolean isSet = false;

    public static void setId(String id) {
        isSet =true;
        DynamicCoasts.id = id;
    }


    public static String getId() {
        return id;
    }




}
