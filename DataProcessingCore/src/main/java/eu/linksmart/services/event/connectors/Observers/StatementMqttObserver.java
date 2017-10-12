package eu.linksmart.services.event.connectors.Observers;

import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttObserver extends IncomingMqttObserver {


    public StatementMqttObserver(List<String> topics) {
        super(topics);
        loggerService.info("The Agent(ID:"+ SharedSettings.getId()+") waiting for queries from BASE/[OPERATION] topic: " + conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH).replace("<id>", SharedSettings.getId())+ "["+conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH).replace("<id>", SharedSettings.getId())+"]");

    }
    public StatementMqttObserver(String topic)  {
        super(topic);
        loggerService.info("The Agent(ID:"+ SharedSettings.getId()+") waiting for queries from BASE/[OPERATION] topic: " + conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH).replace("<id>", SharedSettings.getId())+ "["+conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH).replace("<id>", SharedSettings.getId())+"|"+conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH).replace("<id>", SharedSettings.getId())+"]");

    }
    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        ArrayList<String> topicParts = new ArrayList<>(Arrays.asList(topic.split("/")));
        topicParts.remove("");
        String part1 = topicParts.get(1)+"/";

        MultiResourceResponses<Statement> responses = null;

        /* topic structure should be one of the following:
        *       BASE/add/
        *       BASE/<discriminator>/<statementID>/
        *       BASE/<targetCEP>/add/
        *       BASE/<targetCEP>/<discriminator>/<statementID>/
        *  <discriminator> is on of the followings: new, update, delete
        * */
        if(topicParts.size()>1){ // case BASE/add/
            if(part1.equals(conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH))){
                responses = StatementFeeder.addNewStatement(new String(rawEvent), null, null);
            }else if(part1.equals(conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH)) && topicParts.size()>2){ //  BASE/new/<statementID>/
                responses = StatementFeeder.addNewStatement(new String(rawEvent), topicParts.get(2), null);

            } else if(part1.equals(conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH)) && topicParts.size()>2){//  BASE/delete/<statementID>/
                responses = StatementFeeder.deleteStatement(topicParts.get(2));
            } else if(part1.equals(conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH)) && topicParts.size()>2){//  BASE/update/<statementID>/
                responses = StatementFeeder.addNewStatement(new String(rawEvent), topicParts.get(2), null);
            }else if( CEPEngine.instancedEngines.containsKey(topicParts.get(1)) && topicParts.size()>3){ //  BASE/<targetCEP>/add/ or BASE/<targetCEP>/<discriminator>/<statementID>/

                    String targetCEP = CEPEngine.instancedEngines.get(topicParts.get(1)).getName();
                    String part2 = topicParts.get(2)+"/";

                    if(part2.equals(conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH))){// BASE/<targetCEP>/add/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent), null, targetCEP);
                    }else if(part2.equals(conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH))){ //BASE/<targetCEP>/new/<statementID>/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(3),targetCEP);

                    } else if(part2.equals(conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH)) ){ //BASE/<targetCEP>/delete/<statementID>/
                        responses = StatementFeeder.deleteStatement(topicParts.get(3));
                    } else if(part2.equals(conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH)) ){ //BASE/<targetCEP>/update/<statementID>/
                        responses = StatementFeeder.addNewStatement(new String(rawEvent),topicParts.get(3),targetCEP);
                    }

            }
        }
        if(responses!=null)
            responses.getResponses().forEach(this::publishFeedback);


    }

}
