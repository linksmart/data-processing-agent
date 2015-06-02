package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.esper.EsperQuery;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Statement;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class QueryFeeder extends Feeder {

    private Gson parser = new Gson();

    public QueryFeeder(String brokerName, String brokerPort, String topic) {
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
                        LoggerHandler.publish("queries/" + statement.getHash(), e.getMessage(), null, true);
                        success =false;
                    }
                    if (success)
                        LoggerHandler.publish("queries/"+statement.getHash(),"Statement "+statement.getHash() +" was successfully added",null,true);

                }

            }
        }catch(JsonParseException e){

            LoggerHandler.report("JsonParseError", "No IoTEvent received instead received :" + rawEvent, e.getStackTrace().toString());


        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
