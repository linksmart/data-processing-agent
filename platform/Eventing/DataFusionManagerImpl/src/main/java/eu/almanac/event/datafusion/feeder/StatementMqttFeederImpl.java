package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttFeederImpl extends EventMqttFeederImpl {

    private Gson parser = new Gson();

    @SuppressWarnings("UnusedDeclaration")
    public StatementMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException {
        super(brokerName, brokerPort, topic);
    }


    @SuppressWarnings("SynchronizeOnNonFinalField")
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
                    for (DataFusionWrapper i : dataFusionWrappers.values()) {
                        try {
                            i.addStatement(statement);

                        }catch (StatementException e){
                            brokerService.publish(e.getErrorTopic(),e.getMessage());
                            loggerService.error(e.getMessage(),e);
                        }
                        catch (Exception e) {
                            loggerService.error(e.getMessage(),e);

                            success = false;
                        }
                        if (success)
                            brokerService.publish(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + statement.getHash(), "Statement " + statement.getHash() + " was successful");

                    }

                }

            }
        }catch(JsonParseException e){
            try {
                brokerService.publish(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"errors",e.getMessage());
            } catch (Exception e1) {
                loggerService.error(e1.getMessage(),e1);
            }
            loggerService.error(e.getMessage(),e);



        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }
    }
}
