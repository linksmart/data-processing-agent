package eu.linksmart.services.event.cep.engines;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.UnknownException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.SiddhiCEPHandler;
import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.linksmart.services.event.cep.engines.intern.Const;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.math.NumberUtils;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by José Ángel Carvajal on 08.06.2016 a researcher of Fraunhofer FIT.
 */
public class SiddhiEngine extends Component implements CEPEngine {
    private static String STATEMENT_INOUT_BASE_TOPIC = "queries/", DEFAULT_TYPE = "almanacDefault";
    private transient static Configurator conf =  Configurator.getDefaultConfig();
    private transient Logger loggerService = LogManager.getLogger(this.getClass());
    static transient protected SiddhiManager siddhiManager = new SiddhiManager();
    protected Map<String,ExecutionPlanRuntime> hashExecutionPlanRuntime = new ConcurrentHashMap<>();

    protected Map<String,Statement> hashStatement = new ConcurrentHashMap<>();
    protected Map<String,InputHandler> hashInputHandler = new ConcurrentHashMap<>();
    protected Map<String,String> typeNameSiddhiDeffinition = new ConcurrentHashMap<>();
    protected BidiMap topicTypeName = new DualHashBidiMap();

    protected SiddhiEngine(String implName, String desc, String implOf) {
        super(implName, desc, implOf);
    }

    protected SiddhiEngine(String implName, String desc, String... implOf) {
        super(implName, desc, implOf);
    }


    static SiddhiEngine init(){
        SiddhiEngine SE= new SiddhiEngine();

        instancedEngines.put(SE.getName(),SE);

        return SE;
    }
    static protected SiddhiEngine engine= init();


    protected  SiddhiEngine(){
        super(SiddhiEngine.class.getSimpleName(),"Siddhi handler for complex events", CEPEngine.class.getSimpleName(),CEPEngineAdvanced.class.getSimpleName());

        // Add configuration file of the local package
        Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();


        //load values
        STATEMENT_INOUT_BASE_TOPIC = conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH);

        // TODO: remove this hardcore type and create part of the config file
        typeNameSiddhiDeffinition.put(DEFAULT_TYPE,"@config(async = 'true') define stream Observation (id string, time long, stringValue string, numericValue string);");

        loggerService.info("Siddhi engine has started!");
    }
    @Override
    public String getName() {
        return SiddhiEngine.class.getSimpleName();
    }

    @Override
    public boolean addEvent( EventEnvelope event, Class type) {
        Object[] rawEvent = packEvent(event);
        for(InputHandler inputHandler: hashInputHandler.values()){
            try {
                inputHandler.send(rawEvent);
            } catch (InterruptedException e) {
                loggerService.error(e.getMessage(),e);
                return false;
            }
        }

        return true;
    }
    public Double getNumeric(Object numeric){
        if(numeric== null)
            loggerService.error("the given learning attribute is null");
        if(numeric instanceof Double)
            return (Double) numeric;
        else if(numeric instanceof Integer)
            return Double.valueOf((Integer)numeric);
        else if(numeric instanceof Float)
            return Double.valueOf((Float)numeric);
        else if(numeric instanceof Long)
            return Double.valueOf((Long)numeric);
        else if(numeric instanceof Short)
            return Double.valueOf((Short)numeric);
        else if(numeric instanceof String && NumberUtils.isNumber((String) numeric))
            return NumberUtils.createDouble((String) numeric);

        loggerService.debug("the object "+numeric.toString()+" is not a number");


        return null;


    }
    private Object[] packEvent(EventEnvelope eventEnvelope){
        Object[] rawEvent = new Object[4];
        rawEvent[0]= eventEnvelope.getId();
        rawEvent[1]= eventEnvelope.getDate().getTime();
        rawEvent[2]=getNumeric(eventEnvelope.getValue());
        rawEvent[3]= eventEnvelope.getValue().toString();

        return rawEvent;
    }
    @Override
    public boolean addEventType(String nameType, String[] eventSchema, Class[] eventTypes)throws StatementException {
        if(eventSchema.length != eventTypes.length && !typeNameSiddhiDeffinition.containsKey(nameType))
            return false;

        String siddhiHeaderStreamDefinition ="@config(async = 'true') define stream "+nameType+" (";//symbol string, price float, volume long);";
        for(int i=0; i<eventSchema.length; i++){
            String name= eventSchema[i];
            Class type = eventTypes[i];
            if(isSupportedType(type)) {
                siddhiHeaderStreamDefinition += name + " " + type.getSimpleName();
                if (i + 1 < eventSchema.length) {
                    siddhiHeaderStreamDefinition += ", ";
                } else {
                    siddhiHeaderStreamDefinition += ");";
                }
            }else //TODO: check which is the correct general error topic
                throw new StatementException(this.getName(),"CEEngine","The type "+type+" of attribute "+name+" is not supported by Siddhi");
        }
        typeNameSiddhiDeffinition.put(nameType,siddhiHeaderStreamDefinition);

        return true;
    }

    @Override
    public <T> boolean addEventType(String nameType, Class<T> type) {

        loggerService.error("addEventType(String nameType, Object type) function not implemented");
        return false;
    }

    @Override
    public synchronized boolean  addStatement(Statement query) throws StatementException {

       if(!hashStatement.containsKey(query.getId())){
            hashStatement.put(query.getId(), query);
            hashExecutionPlanRuntime.put(query.getId(),siddhiManager.createExecutionPlanRuntime(typeNameSiddhiDeffinition.get(DEFAULT_TYPE)+query.getStatement()));
            ExecutionPlanRuntime executionPlanRuntime = hashExecutionPlanRuntime.get(query.getId());

            try {
                executionPlanRuntime.addCallback(query.getName(), new SiddhiCEPHandler(query));
            } catch (Exception e) {
                throw new StatementException(STATEMENT_INOUT_BASE_TOPIC+query.getId(),e.getMessage(),e);
            }

            hashInputHandler.put(query.getName(), executionPlanRuntime.getInputHandler("Observation"));

            executionPlanRuntime.start();
        }

        return true;
    }



    private String cachedStatements ="";
    private int i=0;
    private synchronized String getAllStatement(){

        if(i!= hashStatement.size()) {
            for (Statement statement : hashStatement.values()) {
                cachedStatements += statement.getStatement() + "\n";
                i++;
            }
        }

        return cachedStatements;

    }

    @Override
    public boolean removeStatement(String id, Statement statement)  {

        loggerService.error("removeStatement(String id, Statement statement) function not implemented");
        return false;
    }

    @Override
    public boolean updateHandler(Statement statement)  {

        loggerService.error("updateHandler(Statement statement) function not implemented");
        return false;
    }

    @Override
    public boolean pauseStatement(String id) throws StatementException {

        loggerService.error("pauseStatement(String id) function not implemented");
        return false;
    }

    @Override
    public boolean startStatement(String id) throws StatementException {

        loggerService.error("startStatement(String id) function not implemented");
        return false;
    }

    @Override
    public void destroy() {

        loggerService.error(" destroy() function not implemented");
    }

    @Override
    public Map<String, Statement> getStatements() {

        loggerService.error("getStatements() function not implemented");
        return null;
    }
    public boolean executeStatement(Statement statement) throws StatementException, InternalException{

        loggerService.error("executeStatement(Statement statement) function not implemented");
        return false;
    }
    @Override
    public CEPEngineAdvanced getAdvancedFeatures() {

        loggerService.error("addEventType(String nameType, Object type) function not implemented");
        return null;
    }

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isSupportedType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(String.class);
        return ret;
    }
}
