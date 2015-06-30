package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.intern.ConfigurationManagement;
import eu.almanac.event.datafusion.intern.LoggerService;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Statement;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */
public class QueryFeederImpl extends EventFeederImpl {

    private Gson parser = new Gson();

    public QueryFeederImpl(String brokerName, String brokerPort, String topic) {
        super(brokerName, brokerPort, topic);
    }


    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {

                Statement statement = parser.fromJson(new String(rawEvent), EPLStatement.class);


            if (statement != null) {
                if (statement.getStatement().toLowerCase().equals("shutdown")) {
                    synchronized (toShutdown) {
                        toShutdown = true;
                    }
                } else {

                    boolean success = true;
                    try {
                        for (DataFusionWrapper i : dataFusionWrappers.values())
                            i.addStatement(statement);

                    }catch (Exception e){
                        LoggerService.publish(ConfigurationManagement.STATEMENT_BASE_TOPIC + statement.getHash(), e.getMessage(), null, true);
                        success =false;
                    }
                    if (success)
                        LoggerService.publish(ConfigurationManagement.STATEMENT_BASE_TOPIC + statement.getHash(), "Statement " + statement.getHash() + " was successful", null, true);

                }

            }
        }catch(JsonParseException e){

            LoggerService.report("JsonParseError", "No IoTEvent received instead received :" + rawEvent.toString(), e.getStackTrace().toString());


        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
