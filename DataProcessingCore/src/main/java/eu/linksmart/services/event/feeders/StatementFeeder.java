package eu.linksmart.services.event.feeders;

import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.handler.ComplexEventSynchHandler;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by angel on 26/11/15.
 */
public class StatementFeeder implements Feeder<Statement> {
    protected static Logger loggerService = LogManager.getLogger(StatementFeeder.class);
    protected static Configurator conf = Configurator.getDefaultConfig();

    static {
        SharedSettings.getDeserializer().defineClassToInterface(Statement.class, StatementInstance.class);
        Feeder.feeders.put(StatementFeeder.class.getCanonicalName(), new StatementFeeder());
    }

    private StatementFeeder() {
        // super(StatementFeeder.class.getSimpleName(), "Provide intern infrastructure to feed the CEP with learning DF statements ", Feeder.class.getSimpleName(),"CEML");
    }

    public static MultiResourceResponses<Statement> feedStatements(Collection<Statement> statements) {
        boolean success = true;
        String retur = "";
        MultiResourceResponses<Statement> response = new MultiResourceResponses<Statement>();
        for (Statement statement : statements) {

            response.addAllResponses(addNewStatement(statement, null, null, null).getResponses());
        }

        return response;
    }

    public static MultiResourceResponses<Statement> feedStatement(Statement statement) {
        boolean success = true;
        String retur = "";
        MultiResourceResponses<Statement> response;

        response = addNewStatement(statement, null, null, null);

        return response;
    }

    static public MultiResourceResponses<Statement> pauseStatements(Collection<Statement> statements) {

        MultiResourceResponses<Statement> response = new MultiResourceResponses<Statement>();

        for (Statement statement : statements) {

            MultiResourceResponses<Statement> aux = pauseStatement(statement);
            response.addAllResponses(aux.getResponses());
        }

        return response;
    }

    static public MultiResourceResponses<Statement> pauseStatement(Statement statement) {

        MultiResourceResponses<Statement> response = pauseStatement(statement.getId());
        response.addResources(statement.getId(), statement);

        return response;
    }

    static public MultiResourceResponses<Statement> pauseStatement(String hash) {
        return changeStatement(hash, Statement.StatementLifecycle.PAUSE, null);
    }

    static public MultiResourceResponses<Statement> removeStatements(Collection<Statement> statements) {
        MultiResourceResponses<Statement> response = new MultiResourceResponses<Statement>();

        for (Statement statement : statements) {
            MultiResourceResponses<Statement> aux = removeStatement(statement);
            response.addAllResponses(aux.getResponses());
        }

        return response;
    }

    static public MultiResourceResponses<Statement> removeStatement(Statement statement) {

        MultiResourceResponses<Statement> response;

        response = removeStatement(statement.getId(), statement);

        response.addResources(statement.getId(), statement);
        return response;
    }

    static public MultiResourceResponses<Statement> removeStatement(String hash, Statement statement) {
        return changeStatement(hash, Statement.StatementLifecycle.REMOVE, statement);
    }

    static public MultiResourceResponses<Statement> startStatements(Collection<Statement> statements) {

        MultiResourceResponses<Statement> response = new MultiResourceResponses<>();

        for (Statement statement : statements) {
            MultiResourceResponses<Statement> aux = startStatement(statement);
            response.addAllResponses(aux.getResponses());
        }

        return response;
    }

    static public MultiResourceResponses<Statement> startStatement(Statement statement) {

        MultiResourceResponses<Statement> response = startStatement(statement.getId());
        response.addResources(statement.getId(), statement);
        return response;
    }

    static public MultiResourceResponses<Statement> startStatement(String hash) {
        return changeStatement(hash, Statement.StatementLifecycle.RUN, null);
    }

    static public MultiResourceResponses<Statement> changeStatement(String hash, Statement.StatementLifecycle action, Statement statement) {
        boolean success = true;
        MultiResourceResponses<Statement> response = new MultiResourceResponses<>();

        try {
            boolean res = false;
            switch (action) {
                case RUN:
                    res = CEPEngine.instancedEngine.getValue().startStatement(hash);
                    break;
                case PAUSE:
                    res = CEPEngine.instancedEngine.getValue().pauseStatement(hash);
                    break;
                case REMOVE:
                    res = CEPEngine.instancedEngine.getValue().removeStatement(hash, statement);
                    break;
            }
            if (res)
                response.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(), hash, "Agent", "Unexpected error in statement with id " + hash + " in engine " + CEPEngine.instancedEngine.getValue().getName(), 500));
        } catch (StatementException e) {
            loggerService.error(e.getMessage(), e);
            response.addResponse(new GeneralRequestResponse("Bad Request", SharedSettings.getId(), hash, "Agent", e.getMessage(), 400));
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            response.addResponse(new GeneralRequestResponse("Internal Server Error", SharedSettings.getId(), null, "Agent", e.getMessage(), 500));

            success = false;
        }
        if (success) {
            loggerService.info("Statement " + hash + " was successful");

            response.addResponse(new GeneralRequestResponse("OK", SharedSettings.getId(), null, "Agent", "Statement  with id " + hash + " in engine " + CEPEngine.instancedEngine.getValue().getName() + " was processed successfully", 200));
        }
        return response;
    }

    public static MultiResourceResponses<Statement> parseStatement(String statementString, String id) {
        Statement statement = null;

        try {
            statement = SharedSettings.getDeserializer().parse(statementString, Statement.class);
        } catch (IOException e) {
            MultiResourceResponses<Statement> result = new MultiResourceResponses<Statement>();
            loggerService.error(e.getMessage(), e);
            result.getResponses().add(createErrorMapMessage(
                    null, "none", 400, "Bad Request", "The send statement cannot be parsed: " + e.getMessage()));

            return result;
        }

        if (!statement.getId().equals(id))
            statement.setId(id);

        return prepareResponse(statement);
    }

    public static MultiResourceResponses<Statement> prepareResponse(Statement statement) {

        MultiResourceResponses<Statement> result = new MultiResourceResponses<Statement>();

        Map<String, Object> resource = new HashMap<>();
        resource.put(statement.getId(), statement);
        result.getResources().put(statement.getId(), statement);

        return result;
    }

    public static void processRequestInWrapper(CEPEngine dfw, MultiResourceResponses<Statement> result, Statement org) {

        String id = result.getHeadResource().getId();
        try {
            //create new statement (also if statement object exists but statement text is supposed to be updated)
            if (org == null || !result.getHeadResource().getStatement().equals(org.getStatement())) {

                if (org != null) {
                    dfw.removeStatement(org.getId(), org);
                }

                if (!dfw.addStatement(result.getHeadResource())) {
                    if (dfw.getStatements().containsKey(result.getHeadResource().getId()))
                        result.addResponse(createErrorMapMessage(result.getHeadResource().getId(), "Statement", 304, "Not modified", "This exact statement already exists in this agent"));
                    else if (result.getHeadResource().getStateLifecycle() == Statement.StatementLifecycle.REMOVE)
                        result.addResponse(createErrorMapMessage(result.getHeadResource().getId(), "Statement", 400, "Bad Request", "The remove statement with id " + result.getHeadResource().getId() + " does not exist"));
                    else
                        result.addResponse(createErrorMapMessage(result.getHeadResource().getId(), "Statement", 500, "Internal Server Error", dfw.getName() + ": Oops we have a problem"));
                } else {
                    loggerService.info("Statement " + result.getHeadResource().getId() + " was successful");
                    result.addResponse(createSuccessMapMessage(dfw.getName(), "CEPEngine", result.getHeadResource().getId(), 201, "Created", "Statement " + result.getHeadResource().getId() + " was successful", result.getHeadResource().getOutput()));
                }
                //update statement
            } else {

                org.isEssential(result.getHeadResource().isEssential());
                org.isPersistent(result.getHeadResource().isPersistent());
                org.isRegistrable(result.getHeadResource().isRegistrable());

                //change lifecycle state
                if (org.getStateLifecycle() != null && !org.getStateLifecycle().equals(result.getHeadResource().getStateLifecycle())) {
                    switch (result.getHeadResource().getStateLifecycle()) {
                        case RUN:
                            dfw.startStatement(result.getHeadResource().getId());
                            break;
                        case PAUSE:
                            dfw.pauseStatement(result.getHeadResource().getId());
                            break;
                        case REMOVE:
                            dfw.removeStatement(result.getHeadResource().getId(), result.getHeadResource());
                            break;
                    }
                }

                if (result.getHeadResource().getName() != null && !"".equals(result.getHeadResource().getName()) && !org.getName().equals(result.getHeadResource().getName())) {
                    // the name of the statement had being change. Then we update it.
                    dfw.getStatements().get(id).setName(result.getHeadResource().getName());
                }

                //update resultType, Output, Scope or CEHandler; this needs a re-creation of the handler
                if (!result.getHeadResource().getResultType().equals(org.getResultType()) || !result.getHeadResource().getOutput().equals(org.getOutput()) || !result.getHeadResource().getCEHandler().equals(org.getCEHandler()) || !result.getHeadResource().getScope().equals(org.getScope())) {

                    if (result.getHeadResource().getResultType() != null && !result.getHeadResource().getResultType().isEmpty())
                        org.setResultType(result.getHeadResource().getResultType());

                    if (result.getHeadResource().getOutput() != null && !result.getHeadResource().getOutput().isEmpty())
                        org.setOutput(result.getHeadResource().getOutput());

                    if (result.getHeadResource().getScope() != null && !result.getHeadResource().getScope().isEmpty())
                        org.setScope(result.getHeadResource().getScope());

                    if (result.getHeadResource().getCEHandler() != null && !result.getHeadResource().getCEHandler().isEmpty())
                        org.setCEHandler(result.getHeadResource().getCEHandler());

                    if (dfw.updateHandler(org)) {
                        loggerService.info("Statement handler " + result.getHeadResource().getId() + " was updated successfully");
                        result.addResponse(createSuccessMapMessage(result.getHeadResource().getId(), "Statement", result.getHeadResource().getId(), 200, "OK", "Statement " + result.getHeadResource().getId() + " was updated successfully", result.getHeadResource().getOutput()));
                    } else {
                        result.addResponse(createErrorMapMessage(result.getHeadResource().getId(), "Statement", 500, "Internal Server Error", dfw.getName() + ": Oops we have a problem"));
                    }
                }

                if (result.getResponses().isEmpty()) {
                    // if there is any other change in other property is irrelevant, so is consider successful.
                    loggerService.info("Statement " + result.getHeadResource().getId() + " was successful");
                    result.addResponse(createSuccessMapMessage(result.getHeadResource().getId(), "Statement", result.getHeadResource().getId(), 200, "OK", "Statement " + result.getHeadResource().getId() + " was successful", result.getHeadResource().getOutput()));
                }
            }
        } catch (StatementException se) {
            result.addResponse(createErrorMapMessage(se.getErrorProducerId(), se.getErrorProducerType(), 400, "Bad Request", se.getMessage()));
            loggerService.error(se.getMessage(), se);
        } catch (InternalException e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(e.getErrorProducerId(), e.getErrorProducerType(), 500, "Intern Server Error", e.getMessage()));
        } catch (UnknownException e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(e.getErrorProducerId(), e.getErrorProducerType(), 500, "Unexpected Intern Server Error", e.getMessage()));
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Unknown Source Intern Server Error", e.getMessage()));
        }
    }

    public static MultiResourceResponses<Statement> addNewStatement(/*@NotNull*/ String stringStatement,/*@Nullable*/ String id,/*@Nullable*/ String cepEngine) {
        // creating return structures structures
        MultiResourceResponses<Statement> result = parseStatement(stringStatement, id);

        if (result.getResources() != null && !result.getResources().isEmpty())
            return addNewStatement(result.getHeadResource(), id, cepEngine, result);
        return result;
    }

    public static MultiResourceResponses<Statement> addNewStatement(/*@NotNull*/ Statement statement, String orgID, String cepEngine, MultiResourceResponses<Statement> result) {
        GeneralRequestResponse error;
        if (result == null) {
            result = new MultiResourceResponses<>();
            result.addResources(statement.getId(), statement);
        } else {

            if (result.getResources().isEmpty())
                result.addResources(statement.getId(), statement);
        }

        String id = statement.getId();
        String workingCEP = null;

        boolean update = orgID != null && CEPEngine.instancedEngine.getValue().getStatements().containsKey(orgID);

        // checking which wrappers exist
        if (CEPEngine.instancedEngine.getValue() == null) {
            error = createErrorMapMessage(id, "Statement", 503, "Service Unavailable", "No CEP engine found to deploy statement");
            result.addResponse(error);
            loggerService.error(error.getMessage());
        } else if (!((workingCEP = getCEPwithStatement(id)) == null) && !update) { // if the id exists but is not an update
            error = createErrorMapMessage(id, "Statement", 409, "Conflict", "The id sent in the request exists already. If want to be updated make an update/PUT request");
            result.addResponse(error);
            loggerService.error(error.getMessage());
        } else {

            Statement org = null;

            if (workingCEP == null)
                if (cepEngine == null || (cepEngine.equals("")))
                    workingCEP = CEPEngine.instancedEngine.getKey();
                else
                    workingCEP = cepEngine;
            if (update) {
                org = CEPEngine.instancedEngine.getValue().getStatements().get(orgID);

                if (statement.equals(org)) {
                    result.addResponse(createSuccessMapMessage(id, "Statement", id, 304, "Not Modified", "Resource is identical to the update. No changes have been made", statement.getOutput()));
                    return result;
                }

                if (!statement.getPublisher().equals(org.getPublisher())) {
                    result.addResponse(createErrorMapMessage(id, "Statement", 400, "Bad Request", "The field publisher cannot be updated currently. Delete and re-create the statement if necessary."));
                    return result;
                }
            }
            try {
                if (orgID != null && !id.equals(orgID))
                    statement.setId(orgID);

                processRequestInWrapper(CEPEngine.instancedEngine.getValue(), result, org);
            } catch (Exception e) {
                if (CEPEngine.instancedEngine.getKey() == cepEngine) {
                    loggerService.error(e.getMessage(), e);
                    result.addResponse(createErrorMapMessage(id, "Statement", 500, "Internal Server Error", e.getMessage()));
                } else {
                    error = createErrorMapMessage(id, "Statement", 400, "Bad Request", "The cep engine named " + cepEngine + " doesn't exists");
                    result.addResponse(error);
                    loggerService.error(error.getMessage());
                }
            }
        }
        return result;
    }

    protected static boolean existsStatementInCep(/*@NotNull*/String id,/*@NotNull*/ String cepName) {
        return (CEPEngine.instancedEngine.getKey() == cepName) && CEPEngine.instancedEngine.getValue().getStatements().containsKey(id);
    }

    protected static String getCEPwithStatement(String id) {
        String CEP = null;
        if (existsStatementInCep(id, CEPEngine.instancedEngine.getKey())) {
            CEP = CEPEngine.instancedEngine.getKey();
        }
        return CEP;
    }

    public static GeneralRequestResponse createErrorMapMessage(String generatedBy, String producerType, int codeNo, String codeTxt, String message) {
        List<String> topics = new ArrayList<String>();
        if (SharedSettings.getId().equals(generatedBy)) {
            topics.add(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + conf.getString(Const.STATEMENT_OUT_TOPIC_ERROR_CONF_PATH) + generatedBy + "/");
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), null, producerType, message, codeNo, topics);
        } else if (generatedBy != null) {
            topics.add(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + generatedBy + SharedSettings.getId() + "/");
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), null, producerType, message, codeNo, topics);
        }
        topics.add(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + conf.getString(Const.STATEMENT_OUT_TOPIC_ERROR_CONF_PATH) + SharedSettings.getId() + "/");
        return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), null, producerType, message, codeNo, topics);
    }

    public static GeneralRequestResponse createSuccessMapMessage(String processedBy, String producerType, String id, int codeNo, String codeTxt, String message, List<String> topics) {
        if (SharedSettings.getId().equals(id)) {
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), processedBy, producerType, message, codeNo, topics);
        } else if (id != null) {
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), processedBy, producerType, message, codeNo, topics);
        }
        return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), null, producerType, message, codeNo, topics);
    }

    public static GeneralRequestResponse createSuccessMapMessage(String processedBy, String producerType, String id, int codeNo, String codeTxt, String message) {
        List<String> topics = new ArrayList<String>();
        if (SharedSettings.getId().equals(id)) {
            topics.add(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + "/" + id + "/");
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), processedBy, producerType, message, codeNo, topics);
        } else if (id != null) {
            topics.add(DefaultMQTTPublisher.defaultOutput(id));
            return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), processedBy, producerType, message, codeNo, topics);
        }
        topics.add(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + SharedSettings.getId() + "/");
        return new GeneralRequestResponse(codeTxt, SharedSettings.getId(), null, producerType, message, codeNo, topics);
    }

    public static MultiResourceResponses<Statement> deleteStatement(String id, String deleteStatement) {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();

        int count = 0;

        Statement statement = null, nativeStatement = null;

        if (deleteStatement != null) {
            nativeStatement = new StatementInstance();
            nativeStatement.setOutput(null);
            nativeStatement.setCEHandler(ComplexEventSynchHandler.class.getCanonicalName());
            nativeStatement.setStatement(deleteStatement);
            nativeStatement.setScope(null);
            nativeStatement.setName(id + "delete");
            nativeStatement.setStateLifecycle(Statement.StatementLifecycle.REMOVE);
        }

        if (CEPEngine.instancedEngine.getValue() == null) {
            result.addResponse(createErrorMapMessage(id, "Statement", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        } else {

            try {
                if (statement == null && CEPEngine.instancedEngine.getValue().getStatements().containsKey(id)) {
                    statement = CEPEngine.instancedEngine.getValue().getStatements().get(id);

                    result.addResources(id, statement);
                }
                if (CEPEngine.instancedEngine.getValue().removeStatement(id, nativeStatement))
                    count++;
                else
                    result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 404, "Not Found", "In the CEP engine " + CEPEngine.instancedEngine.getValue().getName() + " there is no statement with ID: " + id));
            } catch (StatementException e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(e.getErrorProducerId(), "Statement", 400, e.getErrorProducerType(), e.getMessage()));
            } catch (TraceableException e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(e.getErrorProducerId(), "Statement", 500, e.getErrorProducerType(), e.getMessage()));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 500, "Internal Server Error", e.getMessage()));
            }
        }
        if (count == 0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + id));
        } else if (count != 0)
            result.addResponse(StatementFeeder.createSuccessMapMessage(id, "Statement", id, 200, "OK", "It was deleted the statement with ID: " + id, statement.getOutput()));

        return result;
    }

    public static MultiResourceResponses<Statement> executeStatement(String deleteStatement) {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();

        int count = 0;

        Statement statement = null, nativeStatement = null;

        if (deleteStatement != null) {
            nativeStatement = new StatementInstance();
            nativeStatement.setOutput(null);
            nativeStatement.setCEHandler(ComplexEventSynchHandler.class.getCanonicalName());
            nativeStatement.setStatement(deleteStatement);
            nativeStatement.setScope(null);
            nativeStatement.setName(UUID.randomUUID().toString());
            nativeStatement.setStateLifecycle(Statement.StatementLifecycle.SYNCHRONOUS);
        }

        if (CEPEngine.instancedEngine.getValue() == null) {
            result.addResponse(createErrorMapMessage(statement.getId(), "Statement", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        } else {

            try {
                CEPEngine.instancedEngine.getValue().executeStatement(statement);
            } catch (StatementException e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(e.getErrorProducerId(), "Statement", 400, e.getErrorProducerType(), e.getMessage()));
            } catch (TraceableException e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(e.getErrorProducerId(), "Statement", 500, e.getErrorProducerType(), e.getMessage()));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(statement.getId(), "Statement", 500, "Internal Server Error", e.getMessage()));
            }
        }
        if (count == 0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(statement.getId(), "Statement", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + statement.getId()));
        } else if (count != 0)
            result.addResponse(StatementFeeder.createSuccessMapMessage(statement.getId(), "Statement", statement.getId(), 200, "OK", "It was deleted the statement with ID: " + statement.getId(), statement.getOutput()));

        return result;
    }

    public static MultiResourceResponses<Statement> getStatement(String id) {

        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();

        if (CEPEngine.instancedEngine.getValue() == null) {
            result.addResponse(createErrorMapMessage(id, "Statement", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        } else {

            try {
                Map<String, Statement> aux = CEPEngine.instancedEngine.getValue().getStatements();
                if (!aux.isEmpty() && aux.containsKey(id)) {
                    result.addResources(CEPEngine.instancedEngine.getValue().getName(), aux.get(id));
                    result.addResponse(StatementFeeder.createSuccessMapMessage(id, "Statement", id, 200, "OK", "GET Statement ID: " + id + " result found in  'Resources' ", aux.get(id).getOutput()));
                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 500, "Internal Server Error", e.getMessage()));
            }
        }

        if (result.getResources().size() == 0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + id));
        }

        return result;
    }

    public static MultiResourceResponses<Object> getStatementLastOutput(String id) {

        MultiResourceResponses<Object> result = new MultiResourceResponses<>();

        if (CEPEngine.instancedEngine.getValue() == null) {
            result.addResponse(createErrorMapMessage(id, "Statement", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        } else {

            try {
                Map<String, Statement> aux = CEPEngine.instancedEngine.getValue().getStatements();
                if (!aux.isEmpty() && aux.containsKey(id)) {
                    result.addResources(CEPEngine.instancedEngine.getValue().getName(), aux.get(id).getLastOutput());
                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 500, "Internal Server Error", e.getMessage()));
            }
        }

        if (result.getResources().size() == 0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(id, "Statement", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + id));
        } else if (result.getResources().size() != 0)
            result.addResponse(StatementFeeder.createSuccessMapMessage(id, "Statement", id, 200, "OK", "GET Statement ID: " + id + " result found in  'Resources' "));

        return result;
    }

    public static MultiResourceResponses<Statement> getStatements() {
        MultiResourceResponses<Statement> result = new MultiResourceResponses<>();

        Map<String, Statement> resource = new Hashtable<>();

        if (CEPEngine.instancedEngine.getValue() == null) {
            result.addResponse(createErrorMapMessage(SharedSettings.getId(), "Agent", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        } else {
            try {

                resource.putAll(CEPEngine.instancedEngine.getValue().getStatements());
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                result.addResponse(StatementFeeder.createErrorMapMessage(CEPEngine.instancedEngine.getValue().getName(), "CEPEngine", 500, "Internal Server Error", e.getMessage()));
            }
        }

        result.setResources(resource);
        result.addResponse(StatementFeeder.createSuccessMapMessage("Agent", SharedSettings.getId(), "", 200, "OK", "Resources found are in 'Resources' section"));

        return result;
    }

    public static MultiResourceResponses<Statement> update(String rawStatement, String id, String targetCep) {
        // creating return structures structures
        MultiResourceResponses<Statement> result = parseStatement(rawStatement, id);
        if (result.getResources() == null || result.getResources().isEmpty())
            return result;
        return result;

        // todo there are some properties can be updated that are not yet implemented

    }

    @Override
    public void feed(String id, String payload) throws TraceableException, UntraceableException {
        StatementFeeder.addNewStatement(payload, id, null);
    }

    @Override
    public void feed(String id, Statement payload) throws TraceableException, UntraceableException {
        StatementFeeder.addNewStatement(payload, id, null, new MultiResourceResponses<>());
    }
}
