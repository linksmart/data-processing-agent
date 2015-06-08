package eu.linksmart.gc.network.backbone.protocol.mqtt;

import java.util.regex.Pattern;

/**
 * Created by Caravajal on 06.05.2015.
 */
public class Utils {
    private static final Pattern ipPattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
    public static String getBrokerURL(String brokerName, String brokerPort){

        if (ipPattern.matcher(brokerName).find())
            return "tcp://"+brokerName+":"+brokerPort;
        else
            return "tcp://"+brokerName;
    }
}
