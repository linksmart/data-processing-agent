package eu.linksmart.api.event.datafusion;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Jose Angel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public interface ComplexEventMqttHandler extends ComplexEventHandler {
    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */
    public static Map<String,Map.Entry<String,String>> knownInstances= new Hashtable<String,Map.Entry<String,String>>();


    /*
    TODO: something with this
    code for JAVA 8
     public static boolean addKnownLocations(String statement) throws StatementException; {
        String[] nameURL = statement.toLowerCase().replace("add instance", "").trim().split("=");
        if (nameURL.length == 2) {
            String namePort[] = nameURL[1].split(":");

            ComplexEventMqttHandler.knownInstances.put(nameURL[0], new AbstractMap.SimpleImmutableEntry<>(namePort[0], namePort[1]));

        } else {
            return false;
        }
        return true;
    }
    public static boolean removeKnownLocations(String alias) throws StatementException; {
        if( ComplexEventMqttHandler.knownInstances.containsKey(alias))
            ComplexEventMqttHandler.knownInstances.remove(alias);
        else
            return false;

        return true;
    }*/

}
