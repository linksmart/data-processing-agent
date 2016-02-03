package eu.almanac.event.datafusion.utils.handler;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.ComplexEventMqttHandler;
import eu.linksmart.api.event.datafusion.StatementException;

import java.util.AbstractMap;

/**
 * Created by angel on 17/11/15.
 */
public abstract class FixForJava7Handler extends Component implements ComplexEventMqttHandler {


    public FixForJava7Handler(String implName, String desc, String... implOf) {
        super(implName, desc, implOf);
    }

    public static boolean addKnownLocations(String statement) throws StatementException {
        String[] nameURL = statement.toLowerCase().replace("add instance", "").trim().split("=");
        if (nameURL.length == 2) {
            String namePort[] = nameURL[1].split(":");

            ComplexEventMqttHandler.knownInstances.put(nameURL[0], new AbstractMap.SimpleImmutableEntry<>(namePort[0], namePort[1]));

        } else {
            return false;
        }
        return true;
    }
    public static boolean removeKnownLocations(String alias) throws StatementException {
        if( ComplexEventMqttHandler.knownInstances.containsKey(alias))
            ComplexEventMqttHandler.knownInstances.remove(alias);
        else
            return false;

        return true;
    }
}
