package eu.linksmart.api.event.datafusion;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public interface ComplexEventMqttHandler extends ComplexEventHandler {
    /***
     * Location are the brokers knwon with an alias by the Handlers
     * */
    public static Map<String,Map.Entry<String,String>> knownInstances= new Hashtable<String,Map.Entry<String,String>>();

}
