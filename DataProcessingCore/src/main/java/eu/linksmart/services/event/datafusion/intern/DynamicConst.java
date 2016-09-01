package eu.linksmart.services.event.datafusion.intern;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 22.06.2016 a researcher of Fraunhofer FIT.
 */
public class DynamicConst implements Const {


    protected static String id = UUID.randomUUID().toString();

    private DynamicConst() {
    }

    public static void setIsSet(boolean isSet) {
        DynamicConst.isSet = isSet;
    }

    protected static boolean isSet = false;

    public static void setId(String id) {
        isSet =true;
        DynamicConst.id = id;
    }


    public static String getId() {
        return id;
    }




}
