package eu.linksmart.services.event.connectors.observers;

import eu.linksmart.api.event.exceptions.ErrorResponseException;
import eu.linksmart.api.event.types.impl.AsyncRequest;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 22.05.2015 a researcher of Fraunhofer FIT.
 */

public class StatementMqttObserver extends IncomingMqttObserver {


    public StatementMqttObserver(List<String> topics) {
        super(topics);

        loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") waiting for queries from BASE/[OPERATION] topic: " + AgentUtils.topicReplace(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)) + "[" + conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "]");

    }

    public StatementMqttObserver(String topic) {
        super(topic);
        loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") waiting for queries from BASE/[OPERATION] topic: " + AgentUtils.topicReplace(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)) + "[" + conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "|" + conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH).replace("<id>", SharedSettings.getId()) + "]");

    }

    @Override
    protected void mangeEvent(String topic, byte[] rawEvent) {
        AsyncRequest request;

        try {

            request = SharedSettings.getDeserializer().deserialize(rawEvent,AsyncRequest.class);
            if(request.getTargets()==null || (!request.getTargets().isEmpty() && !request.getTargets().contains(SharedSettings.getId())))
                return;
        }catch (Exception e){
            return;
        }
        try {

        MultiResourceResponses<Statement> responses = null;
        String targetCEP;
        Statement result = null;
        /* topic structure should be one of the following:
        *       BASE/add/
        *       BASE/<discriminator>/<statementID>/
        *       BASE/<targetCEP>/add/
        *       BASE/<targetCEP>/<discriminator>/<statementID>/
        *  <discriminator> is on of the followings: new, update, delete
        * */
        if (topic.contains(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH))) {  // contains BASE

            String op = topic.replace(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH), "");
            int nParts = topicPartSize(op);
            if (nParts == 1 && getN(op, 0).equals(conf.getString(Const.STATEMENT_IN_TOPIC_GET_CONF_PATH)) ){ // case BASE/get/
                responses = StatementFeeder.getStatements();

            } else if (nParts == 1 && getN(op, 0).equals(conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH)) ) {// case BASE/add/
                responses = StatementFeeder.addNewStatement(new String(request.getResource()), null, null);

            } else if (nParts == 2 && !getN(op, -1).equals(conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH))) { // case BASE/OP/ID

                String des = getN(op, -2);
                String id = getN(op, -1);

                if (des.contains(conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH))) { //  BASE/new/<statementID>/
                    responses = StatementFeeder.addNewStatement(new String(request.getResource()), id, null);


                } else if (des.contains(conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH))) {//  BASE/delete/<statementID>/
                    responses = StatementFeeder.deleteStatement(id);

                } else if (des.contains(conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH))) {//  BASE/update/<statementID>/
                    responses = StatementFeeder.addNewStatement(new String(request.getResource()), id, null);
                }else if (des.contains(conf.getString(Const.STATEMENT_IN_TOPIC_GET_CONF_PATH))) {//  BASE/update/<statementID>/
                    responses = StatementFeeder.getStatement(id);
                }

            } else if (nParts == 2 && getN(op, -1).equals(conf.getString(Const.STATEMENT_IN_TOPIC_ADD_CONF_PATH))) { //  BASE/<targetCEP>/add/ or
                responses = StatementFeeder.addNewStatement(new String(request.getResource()), null, getN(op, -2)); // BASE/<targetCEP>/add/
            }else if ((nParts == 3 && CEPEngine.instancedEngines.containsKey(targetCEP = getN(topic, -3)))) { //  BASE/<targetCEP>/<discriminator>/ID/

                String des = getN(op, -2);
                String id = getN(op, -1);
                if (des.equals(conf.getString(Const.STATEMENT_IN_TOPIC_CREATE_CONF_PATH))) { //BASE/<targetCEP>/new/<statementID>/
                    responses = StatementFeeder.addNewStatement(new String(request.getResource()), id, targetCEP);
                } else if (des.equals(conf.getString(Const.STATEMENT_IN_TOPIC_DELETE_CONF_PATH))) { //BASE/<targetCEP>/delete/<statementID>/
                    responses = StatementFeeder.deleteStatement(id);
                } else if (des.equals(conf.getString(Const.STATEMENT_IN_TOPIC_UPDATE_CONF_PATH))) { //BASE/<targetCEP>/update/<statementID>/
                    responses = StatementFeeder.addNewStatement(new String(request.getResource()), id, targetCEP);
                }
            }

        }

        if (responses != null)
            publishFeedback(request.getReturnEndpoint(),responses);

    } catch (Exception e) {
        publishFeedback(
                new ErrorResponseException(
                        new GeneralRequestResponse(
                                "Internal Error",
                                SharedSettings.getId(),request.getId(),
                                StatementMqttObserver.class.getCanonicalName(),
                                e.getMessage(),
                                500,
                                request.getReturnErrorEndpoint())
                )
        );
    }

    }
    private boolean topicEquals(String t1, String t2){
        return removeLeadingSlash(t1).equals(removeLeadingSlash(t2));
    }
    private String removeLeadingSlash(String topic){
        if(topic.charAt(topic.length()-1) == '/')
            return topic.substring(0,topic.length()-1);
        return topic;
    }
    private int topicPartSize(String topic){
        int i = 0;
        for (String part : topic.split("/")) {
            if ("".equals(part))
                continue;
            i++;
        }
        return i;
    }


    private String getN(String topic, int n) {
        String[] parts = topic.split("/");
        List<String> aux = Arrays.asList(parts).stream().filter(i->!"".equals(i)).collect(Collectors.toList());

        return aux.get(Math.floorMod(Math.abs(aux.size()+n),aux.size()));
     //   if(n>-1)
       //     return aux.get(n);
       // else
         //   return aux.get(aux.size()+n);

    }
}
