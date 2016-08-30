package eu.almanac.event.datafusion.feeder;

import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.api.event.datafusion.components.CEPEngine;
import eu.linksmart.api.event.datafusion.components.Feeder;
import eu.linksmart.api.event.datafusion.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.datafusion.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.datafusion.types.Statement;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttFeederImpl extends MqttFeederImpl {

    // configuration
    private String STATEMENT_INOUT_BASE_TOPIC ="queries/";
    @SuppressWarnings("UnusedDeclaration")
    public StatementMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic,StatementMqttFeederImpl.class.getSimpleName(),"Provides a statement MQTT API",MqttFeederImpl.class.getSimpleName(), Feeder.class.getSimpleName());

        STATEMENT_INOUT_BASE_TOPIC = conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH);
    }


    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        ArrayList<String> topicParts = new ArrayList<>(Arrays.asList(topic.split("/")));
        topicParts.remove("");

        MultiResourceResponses<Statement> responses = null;

        /* topic structure should be one of the following:
        *       BASE/add/
        *       BASE/<discriminator>/<statementID>/
        *       BASE/<targetCEP>/add/
        *       BASE/<targetCEP>/<discriminator>/<statementID>/
        *  <discriminator> is on of the followings: new, update, delete
        * */
        if(topicParts.size()>1){ // case BASE/add/
            if(topicParts.get(1).equals("add")){
                responses = StatementFeeder.addNewStatement(new String(rawEvent),null,null);
            }else if(topicParts.get(1).equals("new") && topicParts.size()>2){ //  BASE/new/<statementID>/
                responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(2),null);

            } else if(topicParts.get(1).equals("delete") && topicParts.size()>2){//  BASE/delete/<statementID>/
                responses = StatementFeeder.deleteStatement(topicParts.get(2));
            } else if(topicParts.get(1).equals("update") && topicParts.size()>2){//  BASE/update/<statementID>/
                responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(2),null);
            }else if( CEPEngine.instancedEngines.containsKey(topicParts.get(1)) && topicParts.size()>3){ //  BASE/<targetCEP>/add/ or BASE/<targetCEP>/<discriminator>/<statementID>/

                    String targetCEP = CEPEngine.instancedEngines.get(topicParts.get(1)).getName();

                    if(topicParts.get(2).equals("add")){// BASE/<targetCEP>/add/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent),null,targetCEP);
                    }else if(topicParts.get(2).equals("new")){ //BASE/<targetCEP>/new/<statementID>/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(3),targetCEP);

                    } else if(topicParts.get(2).equals("delete") ){ //BASE/<targetCEP>/delete/<statementID>/
                        responses = StatementFeeder.deleteStatement(topicParts.get(3));
                    } else if(topicParts.get(2).equals("update") ){ //BASE/<targetCEP>/update/<statementID>/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(3),targetCEP);
                    }

            }
        }
        if (responses==null){
            responses = new MultiResourceResponses<>();
            responses.addResponse(new GeneralRequestResponse("Bad Request", DynamicConst.getId(),null,"Agent","The topic "+topic+" is not a known endpoint for receiving requests for agent this agent",400));

        }
        for(GeneralRequestResponse response : responses.getResponses()){

            try {
                brokerService.publish(response.getTopic(), response.getMessage());
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }


    }
    // NOTE: consider move this code to an Feeder abstract class if not TODO: document the function
/*
    protected boolean processInternStatement(Statement statement) throws StatementException {
        if (statement.getStatement().toLowerCase().equals("shutdown")) {
            synchronized (lockToShutdown) {
                toShutdown = true;
            }
            return true;
        }

        if (statement.getStatement() == null || statement.getStatement().toLowerCase().equals("")) {
            StatementFeeder.changeStatement(statement.getID(),statement.getStateLifecycle());

        } else if (statement.getStatement().toLowerCase().contains("add instance ")) {
            FixForJava7Handler.addKnownLocations(statement.getStatement());

        } else if (statement.getStatement().toLowerCase().contains("remove instance ")) {
            FixForJava7Handler.removeKnownLocations(statement.getStatement());

        } else
            return false;

        return true;
    }
*/
}
