package eu.almanac.event.datafusion.feeder;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.linksmart.api.event.datafusion.StatementResponse;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.almanac.event.datafusion.utils.handler.FixForJava7Handler;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttFeederImpl extends MqttFeederImpl {

    private Gson parser = new Gson();
    // configuration
    private String STATEMENT_INOUT_BASE_TOPIC ="queries/";
    @SuppressWarnings("UnusedDeclaration")
    public StatementMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic,StatementMqttFeederImpl.class.getSimpleName(),"Provides a statement MQTT API",MqttFeederImpl.class.getSimpleName(), Feeder.class.getSimpleName());

        STATEMENT_INOUT_BASE_TOPIC = conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH);
    }


    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        Statement statement = null;
        try {

            statement = parser.fromJson(new String(rawEvent), EPLStatement.class);


        } catch (JsonParseException e) {
            try {
                brokerService.publish(STATEMENT_INOUT_BASE_TOPIC + "errors/", e.getMessage());
            } catch (Exception e1) {
                loggerService.error(e1.getMessage(), e1);
            }
            loggerService.error(e.getMessage(), e);


        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);


        }
        try {

            statement = parser.fromJson(new String(rawEvent), EPLStatement.class);


        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);


        }
        if (statement != null) {

            boolean  isProcessed;
            try {
                isProcessed = processInternStatement(statement);
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                return;

            }
            if (!isProcessed) {
                ArrayList<StatementResponse> responses = StatementFeeder.feedStatement(statement);
                for (StatementResponse response : responses)
                    if (response.getTopic() != null && !response.getTopic().equals("")) {
                        try {
                            brokerService.publish(response.getTopic(), response.getMessage());
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(), e);
                        } finally {
                            if (response.isSuccess())
                                loggerService.info(response.getMessage());
                            else
                                loggerService.error(response.getMessage());
                        }
                    } else {
                        try {
                            if (response.isSuccess())
                                brokerService.publish(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + statement.getHash(), response.getMessage());
                            else
                                brokerService.publish(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + "error/" + statement.getHash(), response.getMessage());
                        } catch (Exception e) {
                            loggerService.error(e.getMessage(), e);
                        } finally {
                            if (response.isSuccess())
                                loggerService.info(response.getMessage());
                            else
                                loggerService.error(response.getMessage());
                        }
                    }
            }
        }


    }

    // NOTE: consider move this code to an Feeder abstract class if not TODO: document the function

    protected boolean processInternStatement(Statement statement) throws StatementException {
        if (statement.getStatement().toLowerCase().equals("shutdown")) {
            synchronized (lockToShutdown) {
                toShutdown = true;
            }
            return true;
        }

        if (statement.getStatement() == null || statement.getStatement().toLowerCase().equals("")) {
            StatementFeeder.changeStatement(statement.getHash(),statement.getStateLifecycle());

        } else if (statement.getStatement().toLowerCase().contains("add instance ")) {
            FixForJava7Handler.addKnownLocations(statement.getStatement());

        } else if (statement.getStatement().toLowerCase().contains("remove instance ")) {
            FixForJava7Handler.removeKnownLocations(statement.getStatement());

        } else
            return false;

        return true;
    }

}
