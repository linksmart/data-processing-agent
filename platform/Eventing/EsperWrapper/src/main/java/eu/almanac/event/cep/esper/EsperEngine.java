package eu.almanac.event.cep.esper;

import com.espertech.esper.client.*;
import eu.almanac.event.cep.intern.Const;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

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
    private  LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    private Configurator conf =  Configurator.getDefaultConfig();

    static private EsperEngine ref= init();

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


        // load configuration into Esper
        epService = EPServiceProviderManager.getDefaultProvider(config);

        loggerService.info("Esper engine has started!");

    }

    @Override
    public void insertObject(String name,Object variable) throws UnsupportedOperationException{
        epService.getEPAdministrator().getConfiguration().addVariable(name,variable.getClass(),variable);
    }

    @Override
    public boolean loadAdditionalPackages( String canonicalNameClassOrPkg) throws Exception{
        epService.getEPAdministrator().getConfiguration().addImport(Class.forName(canonicalNameClassOrPkg));
        return true;
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




    private boolean addEsperEvent( Object event, Class type){
        try {
            synchronized (this) {


                epService.getEPRuntime().getEventSender(fullTypeNameToAlias.get(type.getCanonicalName())).sendEvent(event);
            }
        }catch(Exception e){

           loggerService.error(e.getMessage(),e);

            return false;
        }
        return true;
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
    public boolean addEvent(String topic, Object event,Class type) {
        try {

           // String[] parentTopicAndHead = getParentTopic(topic);

            addEsperEvent(event, type );
            //insertStream(((Observation)event).getId(),parentTopicAndHead[0]);
            //createPersistent(((Observation)event).getId(),parentTopicAndHead[0]);

        }catch(Exception e){

            loggerService.error(e.getMessage(),e);

            return false;
        }
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
    public boolean addEventType(String nameType, String[] eventSchema, Object[] eventTypes) {
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

        } catch (StatementException e) {

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




        if(epService.getEPAdministrator().getStatement(query.getHash())!=null) {

            throw new StatementException(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + query.getHash(), ("Query with id " + query.getHash() + " already added"));

        }

        queryReady.put(query.getName(), true);


        addEsperStatement(query);

    }
    private void addEsperStatement( Statement query) throws Exception {
        ComplexEventHandler handler=null;
        // add the query and the listener for the query
        if(query.getStateLifecycle()== Statement.StatementLifecycle.SYNCHRONOUS) {
            ((EPLStatement)query).setCEHandler("eu.almanac.event.datafusion.handler.ComplexEventSynchHandler");
            Class clazz = Class.forName(query.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(query);
        }else if (query.getCEHandler() != null || query.getCEHandler().equals("")) {
            Class clazz = Class.forName(query.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(query);
        }

        switch (query.getStateLifecycle()) {
            case RUN:
            case PAUSE:

                EPStatement statement = epService.getEPAdministrator().createEPL(query.getStatement(), query.getHash());

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

                deployedStatements.put(query.getHash(), query);
                break;
            case ONCE:
            case SYNCHRONOUS:
                EPOnDemandQueryResult result = epService.getEPRuntime().executeQuery(query.getStatement());
                if (handler != null) {

                    for (EventBean event : result.getArray()) {

                        if (event.getUnderlying() instanceof Map)
                            handler.update((Map<String, Object>) event.getUnderlying());
                        else
                            throw new Exception("Unsupported event in on-demand statement for the handler to generate an response ");
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

        ((ComplexEventHandler)epService.getEPAdministrator().getStatement(id).getSubscriber()).destroy();

        epService.getEPAdministrator().getStatement(id).destroy();

        deployedStatements.remove(id);

        return true;
    }

}
