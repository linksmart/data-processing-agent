package eu.linksmart.services.event.cep.engines;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.CurrentTimeSpanEvent;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.UnknownException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.CEPEngineAdvanced;
import eu.linksmart.api.event.components.ComplexEventHandler;
import eu.linksmart.api.event.components.ComplexEventSyncHandler;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.cep.engines.intern.Const;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static eu.linksmart.services.event.cep.tooling.Tools.ObservationFactory;

/**
 * Created by Caravajal on 06.10.2014.
 */
 public class EsperEngine extends Component implements CEPEngineAdvanced {

    private static EPServiceProvider epService;
    static final private EsperEngine ref= init();

    private Map<String, String> fullTypeNameToAlias = new HashMap<>();
    private Map<String,Statement> deployedStatements = new Hashtable<>();
    private  Logger loggerService = Utils.initLoggingConf(this.getClass());
    private Configurator conf =  Configurator.getDefaultConfig();

    private boolean SIMULATION_EXTERNAL_CLOCK = false;

    static protected EsperEngine init(){
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
        addEventType( EventEnvelope.class.getCanonicalName(), EventEnvelope.class);
        fullTypeNameToAlias.put("Event", EventEnvelope.class.getCanonicalName());

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
    public boolean loadAdditionalPackages( String canonicalNameClassOrPkg) throws InternalException{
//        Class cls= Class.forName(canonicalNameClassOrPkg);

        try {
            epService.getEPAdministrator().getConfiguration().addImport(canonicalNameClassOrPkg);

        }catch (Exception e){
            throw new InternalException(getName(),getClass().getCanonicalName(),e.getMessage(),e);

        }
        return true;
    }

    @Override
    public synchronized boolean setEngineTimeTo(Date date) throws InternalException {
        try {
            boolean done=false;
            Date engineTime = new Date(epService.getEPRuntime().getCurrentTime());
            if(done = date.after(engineTime))

                epService.getEPRuntime().sendEvent(new CurrentTimeSpanEvent(date.getTime()));
            else
                loggerService.warn("The provided date "+ Utils.getIsoTimestamp(date)+" is from an earlier time as the current engine date "+Utils.getIsoTimestamp(date)+". " +
                        "Setting the time back, it is not an accepted operation of the engine "+getName()+". Therefore no action had been taken");

            return done;

        }catch (Exception e){
            throw new InternalException(getName(),getClass().getCanonicalName(),e.getMessage(),e);

        }

    }

    @Override
    public Date getEngineCurrentDate() {
        return new Date(epService.getEPRuntime().getCurrentTime());
    }

    @Override
    public boolean dropObject(String name) {

        try {

            return epService.getEPAdministrator().getConfiguration().removeVariable(name,true);

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            return false;
        }
    }

    @Override
    public <T> boolean addEventType(String nameType,  Class<T> type) {


        fullTypeNameToAlias.put(type.getCanonicalName() ,nameType);
        epService.getEPAdministrator().getConfiguration().addEventType(nameType, type);

        return true;

    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
    private long lastValue = 0, nMessages = 0, n=0;
    private synchronized void logMessagePerSecond(){
        nMessages++;
        if(lastValue ==0)
            lastValue = System.currentTimeMillis();
        double aux =(  System.currentTimeMillis() - lastValue)/1000;
        if( aux>=1.0) {
            n++;
            loggerService.debug("Event send: " + nMessages + " msg,  " + nMessages / n * aux + " msg/s");
            loggerService.debug("Event evaluated: "+epService.getEPRuntime().getNumEventsEvaluated() +" msg,  " + + epService.getEPRuntime().getNumEventsEvaluated()/n+" msg/s");

            //nMessages =0;
            lastValue = System.currentTimeMillis();
        }


    }
    @Override
    public boolean addEvent( EventEnvelope event,Class type) {


           // String[] parentTopicAndHead = getParentTopic(topic);
            try {
                synchronized (ref) {
                    epService.getEPRuntime().getEventSender(fullTypeNameToAlias.get(type.getCanonicalName())).sendEvent(event);
                    logMessagePerSecond();
                }
                if(SIMULATION_EXTERNAL_CLOCK)
                    setEngineTimeTo(event.getDate());
            }catch(Exception e){

                loggerService.error(e.getMessage(),e);

                return false;
            }

        return true;
    }
    @Override
    public boolean addEventType(String nameType, String[] eventSchema, Class[] eventTypes)throws StatementException {
        return false;
    }


    @Override
    public boolean addStatement(Statement statement) throws StatementException, UnknownException, InternalException {
        boolean result = false;
        try {
            // if the statement does not exists
            if(epService.getEPAdministrator().getStatement(statement.getID())==null) {

                result = addEsperStatement(statement);
            }
        } catch (StatementException  e) {

            loggerService.error(e.getMessage(), e);
            throw e;

        }catch (ClassNotFoundException e){

            loggerService.error(e.getMessage(), e);

            throw new UnknownException(statement.getID(), "Statement","The given handler of the statement ID:"+statement.getID()+" is unknown by this agent. If no handler was given this is an internal server error, otherwise the user provide wrong handler name");

        }catch (Exception e){
            loggerService.error(e.getMessage(), e);
            throw new InternalException(statement.getID(),e.getMessage(),e.getCause());

        }

        return result;
    }
    private boolean addEsperStatement( Statement statement) throws StatementException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ComplexEventHandler handler=null;
        // add the query and the listener for the query
        if(statement.getStateLifecycle()== Statement.StatementLifecycle.SYNCHRONOUS) {
            //statement.setCEHandler("eu.linksmart.services.event.datafusion.handler.ComplexEventSynchHandler");
            Class clazz = Class.forName(statement.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(statement);
        }else if (statement.getCEHandler() != null && ! "".equals(statement.getCEHandler()) && statement.getStateLifecycle() != Statement.StatementLifecycle.REMOVE) {
            Class clazz = Class.forName(statement.getCEHandler());
            Constructor constructor = clazz.getConstructor(Statement.class);
            handler = (ComplexEventHandler) constructor.newInstance(statement);
        }
       return manageStatementLifeCycle(statement, handler);
    }

    private boolean manageStatementLifeCycle(Statement statement, ComplexEventHandler handler) throws StatementException{
        boolean end = false;
        switch (statement.getStateLifecycle()) {
            case ONCE:
            case SYNCHRONOUS:
                EPOnDemandQueryResult result;
                try {
                    result = epService.getEPRuntime().executeQuery(statement.getStatement());
                }catch (EPStatementException e){
                    throw new StatementException(e.getMessage(),statement.getID(),e.getCause());
                }
                if (handler != null) {

                    for (EventBean event : result.getArray()) {

                        if ( handler.getClass().isAssignableFrom(ComplexEventSyncHandler.class))
                            ((ComplexEventSyncHandler)handler).update(event.getUnderlying());
                        else
                            throw new StatementException("Unsupported event in on-demand statement for the handler to generate an response ",statement.getID(), "Statement");
                    }
                }
                end = true;
                break;
            case REMOVE:
                end = removeStatement(statement.getID());
                break;
            case RUN:
            case PAUSE:
            default:
                EPStatement epl;
                try {
                    epl = epService.getEPAdministrator().createEPL(statement.getStatement(), statement.getID());

                }catch (EPStatementException e){
                    throw new StatementException(e.getMessage(),statement.getID(),e );
                }
                if (handler != null) {
                    epl.setSubscriber(handler);
                }
                switch (statement.getStateLifecycle()) {
                    case PAUSE:
                        epl.stop();
                        break;
                    case RUN:
                    default:
                        epl.start();
                }
                deployedStatements.put(statement.getID(), statement);
                end = true;
        }
        return end;
    }

    @Override
    public boolean removeStatement(String id)  {

        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;

        ComplexEventHandler handler = ((ComplexEventHandler)epService.getEPAdministrator().getStatement(id).getSubscriber());
        if(handler!=null)
            handler.destroy();

        epService.getEPAdministrator().getStatement(id).destroy();

        deployedStatements.remove(id);

        return true;
    }

    @Override
    public boolean pauseStatement(String id)  {
        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;

        epService.getEPAdministrator().getStatement(id).stop();

        deployedStatements.get(id).setStateLifecycle(Statement.StatementLifecycle.PAUSE);

        return true;
    }
    @Override
    public boolean startStatement(String id)  {
        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;
        epService.getEPAdministrator().getStatement(id).start();


        deployedStatements.get(id).setStateLifecycle(Statement.StatementLifecycle.RUN);
        return true;

    }

    @Override
    public void destroy() {
        Arrays.stream(epService.getEPAdministrator().getStatementNames()).forEach((id) -> {
            try {
                removeStatement(id);
            }catch (Exception e){
                loggerService.error(e.getMessage(),e);
            }
        });
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


    static public long getTimeNow(){
        return EsperEngine.getEngine().getEngineCurrentDate().getTime();
    }

    static public Date getDateNow(){
        return EsperEngine.getEngine().getEngineCurrentDate();
    }
    static public boolean insertMultipleEvents(long noEvents,Object event){
        EsperEngine engine =EsperEngine.getEngine();
        EventEnvelope eventEnvelope;
        if(! (event instanceof EventEnvelope) )
            eventEnvelope = ObservationFactory(event, event.getClass().getCanonicalName(), UUID.randomUUID().toString(), engine.getName(), engine.getAdvancedFeatures().getEngineCurrentDate().getTime());
        else
            eventEnvelope = (EventEnvelope) event;
        for (int i=0; i< noEvents; i++) {

            engine.addEvent(  eventEnvelope, eventEnvelope.getClass());
            eventEnvelope.setDate(DateUtils.addHours(eventEnvelope.getDate(), 1));
        }
        return true;

    }
    static public EventEnvelope[] creatMultipleEvents(long noEvents,Object event){
        EventEnvelope eventEnvelope;
        EsperEngine engine =EsperEngine.getEngine();
        EventEnvelope[] result = new Observation[(int)noEvents];
        if(! (event instanceof EventEnvelope) )
            eventEnvelope = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),engine.getAdvancedFeatures().getEngineCurrentDate().getTime());
        else
            eventEnvelope = (EventEnvelope) event;

        result[0] = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),DateUtils.addHours(eventEnvelope.getDate(), 1).getTime());

        for (int i=1; i< noEvents; i++) {

            //engine.addEvent("", eventEnvelope, eventEnvelope.getClass());
            result[i] = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),DateUtils.addHours(result[i-1].getDate(), 1).getTime());

        }
        return result;

    }



}
