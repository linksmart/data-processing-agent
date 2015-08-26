package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 19.08.2015.
 */
public enum Priority {

    MINOR, MAJOR, CRITICAL        ;
    public static Priority fromInteger(int x) {
        switch(x) {
            case 0:
                return MINOR;
            case 1:
                return MAJOR;
            case 2:
                return CRITICAL;
        }
        return null;
    }


    public static String readableString(Priority priority) {
        switch(priority) {
            case MINOR:
                return "minor";
            case MAJOR:
                return "major";
            case CRITICAL:
                return "critical";

        }
        return null;
    }
}
