package eu.almanac.event.datafusion.feeder;


import eu.almanac.event.datafusion.intern.DynamicConst;
import eu.linksmart.api.event.datafusion.StatementInstance;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
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

    @Override
    public boolean dataFusionWrapperSignIn(CEPEngine dfw) {
        return false;
    }

    @Override
    public boolean dataFusionWrapperSignOut(CEPEngine dfw) {
        return false;
    }

    @Override
    public boolean isDown() {
        return true;
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
     /*
    public static ArrayList<StatementResponse> feedStatement(Statement statement){
        boolean success =true;

        ArrayList<StatementResponse> response = new ArrayList<>();

        if(!statement.getTargetAgents().isEmpty()) {
            success = false;
            for (int i = 1; i < statement.getTargetAgents().size(); i++)
                if (statement.getTargetAgents().get(i).equals(DynamicConst.getId()))
                    success = true;
        }


        if(!success) {
            loggerService.info("Discarding statement " + statement.getID() +" is not addressed to me (ID ="+ DynamicConst.getId()+")");
            response.add(new StatementResponse("Discarding statement " + statement.getID() +" is not addressed to me (ID ="+ DynamicConst.getId()+")", HttpStatus.NOT_ACCEPTABLE, true));

        }else {
            for (CEPEngine dfw : CEPEngine.instancedEngines.values()) {

                try {
                    if (!dfw.addStatement(statement))
                        response.add(new StatementResponse("Unexpected error in statement " + statement.getName() + " with hash " + statement.getID() + " in engine " + dfw.getName(), HttpStatus.INTERNAL_SERVER_ERROR, false));

                } catch (StatementException e) {

                    loggerService.error(e.getMessage(), e);
                    response.add(new StatementResponse(e.getMessage(), HttpStatus.BAD_REQUEST, e.getErrorTopic(), false));
                    success = false;

                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                    response.add(new StatementResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, false));

                    success = false;
                }
                if (success) {
                    loggerService.info("Statement " + statement.getID() + " was successful");
                    response.add(new StatementResponse("Statement " + statement.getName() + " with hash " + statement.getID() + " in engine " + dfw.getName() + " was processed successfully", HttpStatus.OK, true));

                }

            }
        }
        return response;
    }*/
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
                    result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Intern Server Error", "Ups we have a problem"));
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
                     dfw.getStatements().get(id).setSource(result.getHeadResource().getSource());

                }
                if (result.getResponses().isEmpty() && org.getSource() != null && !org.getSource().equals(result.getHeadResource().getSource())) {
                    loggerService.error("Statement " + result.getHeadResource().getID() + " try to change an outdated statement property");
                    result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 400, "Bad Request", "The source property is an deprecated property"));

                }
                if (result.getResponses().isEmpty() && org.getName() != null && !org.getName().equals(result.getHeadResource().getName())) {
                     dfw.getStatements().get(id).setName(result.getHeadResource().getName());

                }
                if (result.getResponses().isEmpty()) {
                    loggerService.info("Statement " + result.getHeadResource().getID() + " was successful");
                    result.addResponse(createSuccessMapMessage(dfw.getName(), "CEPEngine", result.getHeadResource().getID(), 200, "OK", "Statement " + result.getHeadResource().getID() + " was successful"));
                }
                if (result.getResponses().isEmpty() && org.getTargetAgents() != null && !org.getTargetAgents().equals(result.getHeadResource().getTargetAgents()) && !result.getHeadResource().getTargetAgents().isEmpty()) {
                    if (result.getHeadResource().getTargetAgents().contains(DynamicConst.getId())) {
                         dfw.getStatements().get(id).setTargetAgents(result.getHeadResource().getTargetAgents());
                    } else {
                        loggerService.warn("Statement " + result.getHeadResource().getID() + " was not modified because I was not addressed in the request.");
                        result.addResponse(createErrorMapMessage(DynamicConst.getId(), "Agent", 100, "Not Modified", "The resource is located at the server but the request do not address my as processing agent of the request. If this request was intent to be an implicit DELETE request, this message should be read as Bad Request 400"));
                    }
                }

            }

        } catch (StatementException se) {
            result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 400, se.getErrorTopic(), se.getMessage()));
            loggerService.error(se.getMessage(), se);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            result.addResponse(createErrorMapMessage(dfw.getName(), "CEPEngine", 500, "Intern Server Error", e.getMessage()));
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
