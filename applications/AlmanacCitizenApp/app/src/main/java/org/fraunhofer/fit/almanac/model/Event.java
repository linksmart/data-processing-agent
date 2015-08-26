package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 19.08.2015.
 */
public enum Event {
    // Notifies of creation of a ticket
    CREATED,
    // Notifies of a property update, contains single "property" and "value" members
    UPDATED,
    // Notifies of ticket deletion
    DELETED;
//    public static Event fromString(String x){
//        switch(x) {
//            case "CREATED":
//                return CREATED;
//            case "UPDATED":
//                return UPDATED;
//            case "DELETED":
//                return DELETED;
//
//        }
//        return null;
//    }
}
