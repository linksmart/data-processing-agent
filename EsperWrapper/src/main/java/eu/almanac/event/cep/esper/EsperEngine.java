package eu.almanac.event.cep.esper;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.CurrentTimeSpanEvent;
import eu.almanac.event.cep.intern.Const;
import eu.almanac.event.datafusion.utils.epl.intern.StatementInstance;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.datafusion.EventType;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by Caravajal on 06.10.2014.
 */
 public class EsperEngine extends Component implements CEPEngineAdvanced {

    private static EPServiceProvider epService;
    @Deprecated
    private Map<String, Map<String,String>> topicName = new HashMap<>();
    @Deprecated
    private Map<String, Map<String,String>> nameTopic = new HashMap<>();
    @Deprecated
    private Map<String, Boolean> queryReady = new HashMap<>();

    private Map<String, String> fullTypeNameToAlias = new HashMap<>();

    private Map<String,Statement> deployedStatements = new Hashtable<>();
    private  Logger loggerService = Utils.initLoggingConf(this.getClass());
    private Configurator conf =  Configurator.getDefaultConfig();

    static final private EsperEngine ref= init();
    // configuration
    private String STATEMENT_INOUT_BASE_TOPIC = "queries/";
    private boolean SIMULATION_EXTERNAL_CLOCK = false;

    static EsperEngine init(){
        EsperEngine EE= new EsperEngine();

        instancedEngines.put(EE.getName(),EE);
        return EE;
    }


    public static EsperEngine getEngine(){
        return  ref;
    }
    protected EsperEngine(){
        super(EsperEngine.class.getSimpleName(),"Default handler for complex events", CEPEngine.class.getSimpleName(),CEPEngineAdvanced.class.getSimpleName());

        // Add configuration file of the local package
        Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();

        // add additional configuration
        Configuration config = new Configuration();
        config.configure("intern.esper.conf.xml");
        //load values
        STATEMENT_INOUT_BASE_TOPIC = conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH);
        SIMULATION_EXTERNAL_CLOCK = conf.getBoolean(Const.SIMULATION_EXTERNAL_CLOCK);

        // extern clock
        if(SIMULATION_EXTERNAL_CLOCK) {
            config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        }
        // load configuration into Esper
        epService = EPServiceProviderManager.getDefaultProvider(config);


        // extern clock
        if(SIMULATION_EXTERNAL_CLOCK)
            epService.getEPRuntime().sendEvent(new CurrentTimeEvent(conf.getDate(Const.SIMULATION_EXTERNAL_CLOCK_STARTING_TIME).getTime()));


        // default event type
        addEventType( EventType.class.getCanonicalName(), "Event");
        fullTypeNameToAlias.put("Event", EventType.class.getCanonicalName());

        loggerService.info("Esper engine has started!");

    }

    @Override
    public <T extends Object> void insertObject(String name,T variable) throws UnsupportedOperationException{

        epService.getEPAdministrator().getConfiguration().addImport(variable.getClass());
        epService.getEPAdministrator().getConfiguration().addVariable(name,variable.getClass(),variable);
    }

  /*  @Override
    public void insertObject(String name,Object variable) throws UnsupportedOperationException{

        epService.getEPAdministrator().getConfiguration().addImport(variable.getClass());
        epService.getEPAdministrator().getConfiguration().addVariable(name,variable.getClass(),variable);
    }*/

    @Override
    public boolean loadAdditionalPackages( String canonicalNameClassOrPkg) throws Exception{
//        Class cls= Class.forName(canonicalNameClassOrPkg);

        epService.getEPAdministrator().getConfiguration().addImport(canonicalNameClassOrPkg);
        return true;
    }

    @Override
    public synchronized boolean setEngineTimeTo(Date date) throws Exception {
        boolean done=false;
        Date engineTime = new Date(epService.getEPRuntime().getCurrentTime());
        if(done = date.after(engineTime))

            epService.getEPRuntime().sendEvent(new CurrentTimeSpanEvent(date.getTime()));
        else
            loggerService.warn("The provided date "+ Utils.getIsoTimestamp(date)+" is from an earlier time as the current engine date "+Utils.getIsoTimestamp(date)+". " +
                    "Setting the time back, it is not an accepted operation of the engine "+getName()+". Therefore no action had been taken");

        return done;

    }

    @Override
    public Date getEngineCurrentDate() {
        return new Date(epService.getEPRuntime().getCurrentTime());
    }

    @Override
    public void dropObject(String name) {

        epService.getEPAdministrator().getConfiguration().removeVariable(name,true);
    }

    @Override
    public boolean addEventType(String nameType,  Object type) {


        fullTypeNameToAlias.put(type.getClass().getName(),nameType);
        epService.getEPAdministrator().getConfiguration().addEventType(nameType, type.getClass());

        return true;

    }
    private void checkQueriesReadiness( String newEventWithTopic){

        if(topicName.containsKey(newEventWithTopic))
            for(String queryName: topicName.get(newEventWithTopic).values()) {
                nameTopic.get(queryName).remove(newEventWithTopic);
                queryReady.put(queryName, nameTopic.get(queryName).isEmpty());
            }



    }
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }







    /*public String[] getParentTopic(String topic){
        String esperParentTopic;
        if(topic.charAt(0)== '/')
            topic = topic.substring(1);

        String [] esperTopicArray = topic.split("/");

        esperParentTopic = esperTopicArray[0];
        for (int i=1; i<esperTopicArray.length-2;i++) {
            esperParentTopic += "."+esperTopicArray[i];

        }

        return new String[]{esperParentTopic, esperTopicArray[esperTopicArray.length-2]};
    }*/
    @Override
    public boolean addEvent(String topic, EventType event,Class type) {


           // String[] parentTopicAndHead = getParentTopic(topic);
            try {
                synchronized (ref) {
                    epService.getEPRuntime().getEventSender(fullTypeNameToAlias.get(type.getCanonicalName())).sendEvent(event);
                }
                if(SIMULATION_EXTERNAL_CLOCK)
                    setEngineTimeTo(event.getDate());
            }catch(Exception e){

                loggerService.error(e.getMessage(),e);

                return false;
            }
            //insertStream(((Observation)event).getId(),parentTopicAndHead[0]);
            //createPersistent(((Observation)event).getId(),parentTopicAndHead[0]);


        return true;
    }



    private boolean insertStream(String head, String paretTopic){
        if (epService.getEPAdministrator().getStatement(head)!=null)
            return false;
        EPStatement statement;
        try {
             statement = epService.getEPAdministrator().createEPL("insert into O"+head+" select * from observation(id = '"+head+"')" , head);


        }catch (EPStatementSyntaxException Esyn){
            loggerService.error(Esyn.getMessage(),Esyn);
            return false;
        }


        return statement.getState() != EPStatementState.STARTED;
    }

    @Override
    public boolean addEventType(String nameType, String[] eventSchema, Class[] eventTypes)throws StatementException {
        return false;
    }

    @Override
    public boolean setTopicToEventType(String topic, String eventType) {
        return false;
    }

    @Override
    public boolean addStatement(Statement statement) throws StatementException {
        Boolean ret = false;

        try {
            addQuery(statement);
            // if there is no exception set add statement as success
            ret = true;

        } catch (StatementException  e) {

            throw e;

        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            ret = false;
        }


        return ret;
    }

    @Override
    public boolean removeStatement(String id) throws StatementException {
        return removeQuery(id);
    }

    @Override
    public boolean pauseStatement(String id) throws StatementException {
       return pauseQuery(id);


    }
    @Override
    public boolean startStatement(String id) throws StatementException {
        return startQuery(id);


    }

    @Override
    public void destroy() {
        for (String i: epService.getEPAdministrator().getStatementNames())
            removeQuery(i);
        loggerService.info(getName() + " logged off");

    }

    @Override
    public Map<String,Statement>  getStatements() {
        return deployedStatements;
    }

    @Override
    public CEPEngineAdvanced getAdvancedFeatures() {
        return this;
    }

    public void addQuery(Statement query) throws Exception{




        if(epService.getEPAdministrator().getStatement(query.getID())!=null) {

            throw new StatementException(STATEMENT_INOUT_BASE_TOPIC+ query.getID(), ("Query with id " + query.getID() + " already added"));

        }

        queryReady.put(query.getName(), true);


        addEsperStatement(query);

    }
    private void addEsperStatement( Statement query) throws Exception {
        ComplexEventHandler handler=null;
        // add the query and the listener for the query
        if(query.getStateLifecycle()== Statement.StatementLifecycle.SYNCHRONOUS) {
            ((StatementInstance)query).setCEHandler("eu.almanac.event.datafusion.handler.ComplexEventSynchHandler");
            Class clazz = Class.forName(query.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(query);
        }else if (query.getCEHandler() != null && !query.getCEHandler().equals("")) {
            Class clazz = Class.forName(query.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(query);
        }

        switch (query.getStateLifecycle()) {
            case RUN:
            case PAUSE:
                EPStatement statement;
                try {

                    statement = epService.getEPAdministrator().createEPL(query.getStatement(), query.getID());
                }catch (EPStatementException e){
                    throw new StatementException(e.getMessage(),query.getID(),e.getCause());
                }

                if (handler != null) {

                    statement.setSubscriber(handler);
                }
                switch (query.getStateLifecycle()) {
                    case RUN:
                        statement.start();
                        break;
                    case PAUSE:
                        statement.stop();
                        break;

                }

                deployedStatements.put(query.getID(), query);
                break;
            case ONCE:
            case SYNCHRONOUS:
                EPOnDemandQueryResult result;
                try {
                     result = epService.getEPRuntime().executeQuery(query.getStatement());
                }catch (EPStatementException e){
                    throw new StatementException(e.getMessage(),query.getID(),e.getCause());
                }
                if (handler != null) {

                    for (EventBean event : result.getArray()) {

                        if ( handler.getClass().isAssignableFrom(ComplexEventSyncHandler.class))
                            ((ComplexEventSyncHandler)handler).update(event.getUnderlying());

                        else
                            throw new StatementException("Unsupported event in on-demand statement for the handler to generate an response ",query.getID());

                    }
                }
                break;
        }


    }

    public boolean pauseQuery(String id) {

        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;

        epService.getEPAdministrator().getStatement(id).stop();

        // TODO: deployedStatements.get(id) mark as stopped

        return true;
    }

    public boolean startQuery(String id) {

        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;
        epService.getEPAdministrator().getStatement(id).start();


        // TODO: deployedStatements.get(id) mark as stopped
        return true;
    }

    public boolean removeQuery(String id) {

        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;

        ComplexEventHandler handler = ((ComplexEventHandler)epService.getEPAdministrator().getStatement(id).getSubscriber());
        if(handler!=null)
            handler.destroy();

        epService.getEPAdministrator().getStatement(id).destroy();

        deployedStatements.remove(id);

        return true;
    }

}
