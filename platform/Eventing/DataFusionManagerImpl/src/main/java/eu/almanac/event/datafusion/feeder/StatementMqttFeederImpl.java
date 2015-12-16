package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.event.datafusion.utils.handler.FixForJava7Handler;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttFeederImpl extends MqttFeederImpl {

    private Gson parser = new Gson();

    @SuppressWarnings("UnusedDeclaration")
    public StatementMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic,StatementMqttFeederImpl.class.getSimpleName(),"Provides a statement MQTT API",MqttFeederImpl.class.getSimpleName(), Feeder.class.getSimpleName());

    }


    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {

                Statement statement = parser.fromJson(new String(rawEvent), EPLStatement.class);


            if (statement != null) {


                boolean success = true;
                if (!processInternStatement(statement))
                    for (CEPEngine i : dataFusionWrappers.values()) {
                        try {

                            i.addStatement(statement);

                        } catch (StatementException e) {
                            brokerService.publish(e.getErrorTopic(), e.getMessage());
                            loggerService.error(e.getMessage(), e);
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(), e);

                            success = false;
                        }
                        if (success)
                            brokerService.publish(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + statement.getHash(), "Statement " + statement.getHash() + " was successful");

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

    // NOTE: consider move this code to an Feeder abstract class if not TODO: document the function
    @SuppressWarnings("SynchronizeOnNonFinalField")
    protected boolean processInternStatement(Statement statement) throws StatementException {
        if (statement.getStatement().toLowerCase().equals("shutdown")) {
            synchronized (toShutdown) {
                toShutdown = true;
            }
            return true;
        }
        for (CEPEngine i : dataFusionWrappers.values()) {
            if (statement.getStatement() == null || statement.getStatement().toLowerCase().equals("")) {
                switch (statement.getStateLifecycle()) {
                    case RUN:
                        i.startStatement(statement.getHash());
                        break;
                    case PAUSE:
                        i.pauseStatement(statement.getHash());
                        break;
                    case REMOVE:
                        i.removeStatement(statement.getHash());
                        break;

                }

            } else if (statement.getStatement().toLowerCase().contains("add instance ")) {
                FixForJava7Handler.addKnownLocations(statement.getStatement());

            } else if (statement.getStatement().toLowerCase().contains("remove instance ")) {
                FixForJava7Handler.removeKnownLocations(statement.getStatement());

            } else
                return false;
        }
        return true;
    }
}
