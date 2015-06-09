package eu.almanac.event.datafusion.intern;


import java.util.UUID;

/**
 * Created by Caravajal on 16.10.2014.
 */
public class ConfigurationManagement {
    public static String LOG_TOPIC ="/eu/alamanac/event/datafusion/";
    public static String BROKER_HOST ="localhost";
    public static String BROKER_PORT ="1883";
    public static String BASE_TOPIC ="/+/+/v2/";
    public static String EVENT_TOPIC = BASE_TOPIC+"/observation/#";
    public static String STATEMENT_BASE_TOPIC ="queries/";
    public static String STATEMENT_ADD_TOPIC =STATEMENT_BASE_TOPIC+"add";
    public static String FUSED_TOPIC = BASE_TOPIC +"/cep/";
    public static String DFM_ID = UUID.randomUUID().toString();

    private ConfigurationManagement(){ }


}