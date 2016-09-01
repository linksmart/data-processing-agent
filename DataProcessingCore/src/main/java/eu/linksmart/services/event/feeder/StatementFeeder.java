package eu.linksmart.services.event.feeder;


import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.UnknownException;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.StatementInstance;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * Created by angel on 26/11/15.
 */
public class StatementFeeder implements Feeder {
    protected static Logger loggerService = Utils.initLoggingConf(StatementFeeder.class);
    protected static Configurator conf =  Configurator.getDefaultConfig();


    private StatementFeeder() {
       // super(StatementFeeder.class.getSimpleName(), "Provide intern infrastructure to feed the CEP with learning DF statements ", Feeder.class.getSimpleName(),"CEML");
    }


   public static MultiResourceResponses<Statement> feedStatements(Collection<Statement> statements) {
        boolean success =true;
        String retur="";
       MultiResourceResponses<Statement> response = new MultiResourceResponses<Statement>();
            for (Statement statement : statements) {

                response.addAllResponses(addNewStatement(statement, null, null).getResponses());
            }

        return response;
    }
    public static MultiResourceResponses<Statement> feedStatement(Statement statement) {
        boolean success =true;
        String retur="";
        MultiResourceResponses<Statement> response ;


         response = addNewStatement(statement, null, null);


        return response;
    }

    static public  MultiResourceResponses<Statement> pauseStatements(Collection<Statement> statements) {

        MultiResourceResponses<Statement> response = new MultiResourceResponses<Statement>();


        for (Statement statement : statements) {

            MultiResourceResponses<Statement> aux =pauseStatement(statement);
            response.addAllResponses(aux.getResponses());
        }

        return response;
    }
    static public   MultiResourceResponses<Statement> pauseStatement(Statement statement){

        MultiResourceResponses<Statement> response = pauseStatement(statement.getID());
        response.addResources(statement.getID(),statement);


        return response;
    }
    static public  MultiResourceResponses<Statement> pauseStatement(String hash){
        return changeStatement(hash,Statement.StatementLifecycle.PAUSE);
    }

    static public  MultiResourceResponses<Statement> removeStatements(Collection<Statement> statements){
        MultiResourceResponses<Statement> response = new  MultiResourceResponses<Statement>();


        for (Statement statement : statements) {
            MultiResourceResponses<Statement> aux =removeStatement(statement);
            response.addAllResponses(aux.getResponses());
        }

        return response;
    }
    static public  MultiResourceResponses<Statement> removeStatement(Statement statement){

        MultiResourceResponses<Statement> response ;


        response=removeStatement(statement.getID());


        response.addResources(statement.getID(),statement);
        return response;
    }
    static public  MultiResourceResponses<Statement> removeStatement(String hash){
       return changeStatement(hash,Statement.StatementLifecycle.REMOVE);
    }
   static public  MultiResourceResponses<Statement> startStatements(Collection<Statement> statements){

       MultiResourceResponses<Statement> response =new MultiResourceResponses<>();

            for (Statement statement : statements) {
                MultiResourceResponses<Statement> aux =startStatement(statement);
                response.addAllResponses(aux.getResponses());
            }

        return response;
    }
    static public  MultiResourceResponses<Statement> startStatement(Statement statement) {

        MultiResourceResponses<Statement> response =startStatement(statement.getID());
        response.addResources(statement.getID(),statement);
        return response;
    }
    static public  MultiResourceResponses<Statement> startStatement(String hash){
        return changeStatement(hash, Statement.StatementLifecycle.RUN);
    }

    static public  MultiResourceResponses<Statement> changeStatement(String hash, Statement.StatementLifecycle action){
        boolean success =true;
        MultiResourceResponses<Statement> response = new MultiResourceResponses<>();

        for(CEPEngine dfw: CEPEngine.instancedEngines.values()) {

            try {
                boolean res= false;
                switch (action){
                    case RUN:
                        res=dfw.startStatement(hash);
                        break;
                    case PAUSE:
                        res=dfw.pauseStatement(hash);
                        break;
                    case REMOVE:
                        res=dfw.removeStatement(hash);
                        break;
                }
                if(res)
                    response.addResponse(new GeneralRequestResponse("Internal Server Error", DynamicConst.getId(),hash, "Agent", "Unexpected error in statement with id " + hash + " in engine " + dfw.getName(), 500));

            } catch (StatementException e) {
                loggerService.error(e.getMessage(), e);
                response.addResponse(new GeneralRequestResponse("Bad Request", DynamicConst.getId(),hash, "Agent", e.getMessage(), 400));

            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                response.addResponse(new GeneralRequestResponse("Internal Server Error", DynamicConst.getId(), null, "Agent", e.getMessage(), 500));

                success = false;
            }
            if (success) {
                loggerService.info("Statement " + hash + " was successful");

                response.addResponse(new GeneralRequestResponse("OK", DynamicConst.getId(), null, "Agent", "Statement  with id "+ hash+ " in engine "+dfw.getName()+" was processed successfully", 200));
            }

        }
        return response;
    }
;
    protected static ObjectMapper parser =new ObjectMapper();

    public static MultiResourceResponses<Statement> createReturnStructure(){
        MultiResourceResponses<Statement> result = new MultiResourceResponses<Statement>();

        return result;
    }
    public static MultiResourceResponses<Statement>  parseStatement(String statementString ){
        Statement statement = null;
        MultiResourceResponses<Statement> result = createReturnStructure();

        try {
            statement = parser.readValue(statementString, StatementInstance.class);
        } catch (IOException e) {
            loggerService.error(e.getMessage(), e);
            result.getResponses().add(createErrorMapMessage(
                    DynamicConst.getId(),"Agent",400,"Bad Request","The send statement cannot be parsed: " + e.getMessage()));

            return result;
        }

        Map<String, Object> resource = new HashMap<>();
        resource.put(statement.getID(),statement);
        result.getResources().put(statement.getID(),statement);

        return result;

    }
    public static void processRequestInWrapper(CEPEngine dfw,MultiResourceResponses<Statement> result, Statement org){

        String id= result.getHeadResource().getID();
        try {
            if (org==null) {
                if (!dfw.addStatement(result.getHeadResource())) {
                    if(dfw.getStatements().containsKey(result.getHeadResource().getID()))
                        result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 304, "Not modified", "This exact statement already exists in this agent"));
                    else if (result.getHeadResource().getStateLifecycle() == Statement.StatementLifecycle.REMOVE)
                        result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 400, "Bad Request", "The remove statement with id "+result.getHeadResource().getID()+" does not exist"));
                    else
                        result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Internal Server Error", "Oops we have a problem"));
                }
                loggerService.info("Statement " + result.getHeadResource().getID() + " was successful");
                result.addResponse(createSuccessMapMessage(dfw.getName(), "CEPEngine", result.getHeadResource().getID(), 201, "Created", "Statement " + result.getHeadResource().getID() + " was successful"));
            } else {

                if (result.getResponses().isEmpty() && org.getStateLifecycle() != null && !org.getStateLifecycle().equals(result.getHeadResource().getStateLifecycle())) {
                    switch (result.getHeadResource().getStateLifecycle()) {
                        case RUN:
                            dfw.startStatement(result.getHeadResource().getID());
                            break;
                        case PAUSE:
                            dfw.pauseStatement(result.getHeadResource().getID());
                            break;
                        case REMOVE:
                            dfw.removeStatement(result.getHeadResource().getID());
                            break;

                    }
                }
                if (result.getResponses().isEmpty() && org.getSource() != null && !org.getSource().equals(result.getHeadResource().getSource())) {
                    // if no response had being created, and the source had not being changed
                     dfw.getStatements().get(id).setSource(result.getHeadResource().getSource());
                }
                if (result.getResponses().isEmpty() && org.getSource() != null && !org.getSource().equals(result.getHeadResource().getSource())) {
                    // TODO: update the source property
                    loggerService.error("Statement " + result.getHeadResource().getID() + " try to change an outdated statement property");
                    result.addResponse(createErrorMapMessage(result.getHeadResource().getID(), "Statement", 500, "Internal Server Error", "The source property is not available in this agent version"));

                }
                if (result.getResponses().isEmpty() && org.getName() != null && !org.getName().equals(result.getHeadResource().getName())) {
                    // the name of the statement had being change. Then we update it.
                     dfw.getStatements().get(id).setName(result.getHeadResource().getName());
                }
                if (result.getResponses().isEmpty()) {
                    // if there is any other change in other property is irrelevant, so is consider successful.
                    loggerService.info("Statement " + result.getHeadResource().getID() + " was successful");
                    result.addResponse(createSuccessMapMessage(result.getHeadResource().getID(), "Statement", result.getHeadResource().getID(), 200, "OK", "Statement " + result.getHeadResource().getID() + " was successful"));
                }
                if (result.getResponses().isEmpty() && org.getTargetAgents() != null && !org.getTargetAgents().equals(result.getHeadResource().getTargetAgents()) && !result.getHeadResource().getTargetAgents().isEmpty()) {
                    if (result.getHeadResource().getTargetAgents().contains(DynamicConst.getId())) {
                        // the statement addresses me as processing agent
                         dfw.getStatements().get(id).setTargetAgents(result.getHeadResource().getTargetAgents());
                    } else {
                        // the statement doesn't address me as processing agent
                        loggerService.warn("Statement " + result.getHeadResource().getID() + " was not modified because I was not addressed in the request.");
                        result.addResponse(createErrorMapMessage(result.getHeadResource().getID(), "Statement", 100, "Not Modified", "The resource is located at the server but the request do not address me as processing agent of the request. If this request was intent to be an implicit DELETE request, this message should be read as Bad Request 400"));
                    }
                }

            }

        } catch (StatementException se) {
            result.addResponse(createErrorMapMessage(se.getErrorProducerId(), se.getErrorProducerType(), 400, "Bad Request", se.getMessage()));
            loggerService.error(se.getMessage(), se);

        }catch (InternalException e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(e.getErrorProducerId(), e.getErrorProducerType(), 500, "Intern Server Error", e.getMessage()));
        }catch (UnknownException e){
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(e.getErrorProducerId(), e.getErrorProducerType(), 500, "Unexpected Intern Server Error", e.getMessage()));
        }catch (Exception e){
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Unknown Source Intern Server Error", e.getMessage()));
        }
    }
    public static MultiResourceResponses<Statement>  addNewStatement(/*@NotNull*/ String stringStatement,/*@Nullable*/ String id,/*@Nullable*/ String cepEngine){
        // creating return structures structures
        MultiResourceResponses<Statement> result = parseStatement(stringStatement);

        return addNewStatement(result.getHeadResource(),cepEngine,result);
    }
    public static MultiResourceResponses<Statement>  addNewStatement(/*@NotNull*/ Statement statement, String cepEngine,MultiResourceResponses<Statement> result){

        if(result==null){
            result = new MultiResourceResponses<>();
            result.addResources(statement.getID(),statement);

        }else {

            if(result.getResources().isEmpty())
                result.addResources(statement.getID(),statement);

        }

        String id= statement.getID();


        Set<String> workingCEPsList;
        boolean update= statement.getID().equals(id);

        if(update)
            statement.setId(id);

        id = statement.getID();
        // checking which wrappers exist
        if (CEPEngine.instancedEngines.size() == 0){
            result.addResponse(createErrorMapMessage(DynamicConst.getId(),"Agent",503,"Service Unavailable","No CEP engine found to deploy statement"));

        }else if (!(workingCEPsList = getCEPwithStatement(id)).isEmpty() && !update) { // if the id exists but is not an update
            result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 409, "Conflict", "The id sent in the request exists already. If want to be updated make an update/PUT request"));
        }else {

            Statement org = null;

            if (workingCEPsList.isEmpty())
                if(cepEngine == null || (cepEngine.equals("")))
                    workingCEPsList = CEPEngine.instancedEngines.keySet();
                else
                    workingCEPsList.add(cepEngine);
            else {
                org = CEPEngine.instancedEngines.get(workingCEPsList.iterator().next()).getStatements().get(id);
                if (!statement.getStatement().equals(org.getStatement()) ||
                        (org.getInput() != null && !Arrays.deepEquals(org.getInput(),statement.getInput())) ||
                        (org.getCEHandler() != null && !org.getCEHandler().equals(statement.getCEHandler()))
                        ) {
                    result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 400, "Bad Request", "The 'statement string', 'CEHandler string', or 'input array' cannot be updated. It can be only removed and redeploy."));
                    return result;
                }else if (statement.equals(org)) {
                    result.addResponse(createSuccessMapMessage(DynamicConst.getId(), "Agent", id, 304, "Not Modified", "Resource is identically to the update. No changes had being made"));
                    return result;
                }
            }
            try {
                // if (!processInternStatement(statement))
                for (String key : workingCEPsList)
                    processRequestInWrapper(CEPEngine.instancedEngines.get(key),result,org);

            } catch (Exception e) {
                if(CEPEngine.instancedEngines.containsKey(cepEngine)) {
                    loggerService.error(e.getMessage(), e);
                    result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 500, "Internal Server Error", e.getMessage()));
                }else {

                    result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 400, "Bad Request", "The cep engine named " + cepEngine + " doesn't exists"));
                }
            }
        }
        return result;
    }
    protected static boolean existsStatementInCep(/*@NotNull*/String id,/*@NotNull*/ String cepName){
        return CEPEngine.instancedEngines.containsKey(cepName) && CEPEngine.instancedEngines.get(cepName).getStatements().containsKey(id);
    }
    protected static Set<String> getCEPwithStatement(String id){
        Set<String>  result = new HashSet<>();
        for (CEPEngine dfw : CEPEngine.instancedEngines.values())
            if(existsStatementInCep(id,dfw.getName()))
                result.add(dfw.getName());

        return result;
    }
   public static String toJsonString(Object message){

        try {
            return parser.writeValueAsString(message);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
            return "{\"Error\":\"500\",\"Error Text\":\"Internal Server Error\",\"Message\":\""+e.getMessage()+"\"}";
        }


    }


    public static GeneralRequestResponse createErrorMapMessage(String generatedBy,String producerType,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt,DynamicConst.getId(),null,producerType,message,codeNo, "");
    }
    public static GeneralRequestResponse createSuccessMapMessage(String processedBy,String producerType,String id,int codeNo, String codeTxt,String message){
        return new GeneralRequestResponse(codeTxt,DynamicConst.getId(),processedBy,producerType,message,codeNo, "");
    }




    public static  MultiResourceResponses<Statement> deleteStatement(String id) {
        MultiResourceResponses<Statement> result =createReturnStructure();

        int count = 0;

        Statement statement = null;

        if (CEPEngine.instancedEngines.size() == 0) {
            result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        }else {

            for (CEPEngine dfw : CEPEngine.instancedEngines.values())
                try {
                    if (statement == null && dfw.getStatements().containsKey(id)) {
                        statement = dfw.getStatements().get(id);

                        result.addResources(id, statement);

                    }
                    if (dfw.removeStatement(id))
                        count++;
                    else
                        result.addResponse(StatementFeeder.createErrorMapMessage(dfw.getName(), "CEPEngine", 404, "Not Found", "In the CEP engine " + dfw.getName() + " there is no statement with ID: " + id));


                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    result.addResponse(StatementFeeder.createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Internal Server Error", e.getMessage()));
                }

        }
        if (count==0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(DynamicConst.getId(), "Agent", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + id));
        }else if (count!=0 )
            result.addResponse(StatementFeeder.createSuccessMapMessage("Agent", DynamicConst.getId(), id, 200, "OK", "It was deleted the statement with ID: " + id));

        return result;
    }

    public static MultiResourceResponses<Statement> getStatement(String id) {

        MultiResourceResponses<Statement> result =createReturnStructure();


        if (CEPEngine.instancedEngines.size() == 0) {
            result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        }else {

            for (CEPEngine dfw : CEPEngine.instancedEngines.values())
                try {
                    Map<String, Statement> aux = dfw.getStatements();
                    if (!aux.isEmpty() && aux.containsKey(id)) {
                        result.addResources(dfw.getName(), aux.get(id));
                    }

                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    result.addResponse(StatementFeeder.createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Internal Server Error", e.getMessage()));
                }

        }


        if (result.getResources().size()==0 && result.getResponses().isEmpty()) {
            result.addResponse(StatementFeeder.createErrorMapMessage(DynamicConst.getId(), "Agent", 404, "Not Found", "Provided ID doesn't exist in any CEP engine. ID:" + id));
        }else if (result.getResources().size()!=0 )
            result.addResponse(StatementFeeder.createSuccessMapMessage("Agent", DynamicConst.getId(), id, 200, "OK", "GET Statement ID: " + id + " result found in  'Resources' "));

        return result;
    }
    public static MultiResourceResponses<Statement> getStatements() {
        MultiResourceResponses<Statement> result =createReturnStructure();

        Map<String,Statement> resource= new Hashtable<>();

        if (CEPEngine.instancedEngines.size() == 0) {
            result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 503, "Service Unavailable", "No CEP engine found to deploy statement"));
        }else {

            for (CEPEngine dfw : CEPEngine.instancedEngines.values())
                try {

                    resource.putAll(dfw.getStatements());

                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    result.addResponse(StatementFeeder.createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Internal Server Error", e.getMessage()));
                }

        }

        result.setResources(resource);
        result.addResponse(StatementFeeder.createSuccessMapMessage("Agent", DynamicConst.getId(), "", 200, "OK", "Resources found are in 'Resources' section"));

        return result;
    }
}
