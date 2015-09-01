package eu.almanac.event.datafusion.esper;

import com.espertech.esper.client.*;
import eu.almanac.event.datafusion.esper.utils.Tools;
import eu.almanac.event.datafusion.handler.ComplexEventHandlerImpl;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class EsperEngine implements DataFusionWrapper {
    private static EPServiceProvider epService;
    @Deprecated
    Map<String, Map<String,String>> topicName = new HashMap<>();
    @Deprecated
    Map<String, Map<String,String>> nameTopic = new HashMap<>();
    @Deprecated
    Map<String, Boolean> queryReady = new HashMap<>();
    Map<String,Statement> deployedStatements = new Hashtable<>();
    private  LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    private Configurator conf =  Configurator.getDefaultConfig();
    static private EsperEngine ref= new EsperEngine();

    public static EsperEngine getEngine(){
        return  ref;
    }
    private EsperEngine(){
        Configuration config = new Configuration();

        config.addImport("java.security.*");
        config.addImport(Tools.class);
        config.addImport(UUID.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        defineIoTTypes("observation", Observation.class);
        loggerService.info("Esper engine has started!");

    }
    private static void defineIoTTypes(String esperTopic, Class type) {


        epService.getEPAdministrator().getConfiguration().addEventType(esperTopic, type);


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




    private boolean addEsperEvent(String esperTopic, Object event, Class type){
        try {
            synchronized (this) {
                // if the topic type is already defined, then the event is send
                //if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists("observation"))

                  //  defineIoTTypes("observation", type);


                epService.getEPRuntime().getEventSender("observation").sendEvent(event);
            }
        }catch(Exception e){

           loggerService.error(e.getMessage(),e);

            return false;
        }
        return true;
    }

    public String[] getParentTopic(String topic){
        String esperParentTopic;
        if(topic.charAt(0)== '/')
            topic = topic.substring(1);

        String [] esperTopicArray = topic.split("/");

        esperParentTopic = esperTopicArray[0];
        for (int i=1; i<esperTopicArray.length-2;i++) {
            esperParentTopic += "."+esperTopicArray[i];

        }

        return new String[]{esperParentTopic, esperTopicArray[esperTopicArray.length-2]};
    }
    @Override
    public boolean addEvent(String topic, Object event,Class type) {
        try {

            String[] parentTopicAndHead = getParentTopic(topic);

            addEsperEvent(parentTopicAndHead[0]+ ".hash",event, type );
            insertStream(((Observation)event).getId(),parentTopicAndHead[0]);
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
        if(statement.getStatement().toLowerCase().equals("pause")){
            ret = pauseQuery(statement.getName());

        }else if(statement.getStatement().toLowerCase().equals("start") ){

            ret = startQuery(statement.getName());

        }else if(statement.getStatement().toLowerCase().equals("remove") ){

            ret = removeQuery(statement.getName());

        } else if (statement.getStatement().toLowerCase().contains("add instance ") ) {
            String[] nameURL = statement.getStatement().toLowerCase().replace("add instance","").trim().split("=");
            if(nameURL.length==2){
                String namePort[] = nameURL[1].split(":");

                ComplexEventMqttHandler.knownInstances.put(nameURL[0],new AbstractMap.SimpleImmutableEntry<>(namePort[0],namePort[1]));

            }else {
                throw new StatementException(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + statement.getHash(), ("Statement " + statement.getName() + " try to add a instance but the format is incorrect, the correct format is 'add instance <instanceName>=<instanceURL>'"));

            }

        }else {
            try {
                addQuery(statement);
                // if there is no exception set add statement as success
                ret =true;

            }catch (MalformedURLException | RemoteException | NullPointerException e ){
                loggerService.error(e.getMessage(),e);
                ret =false;
            }

        }

        return ret;
    }

    @Override
    public boolean removeStatement(String id) throws StatementException {
        return removeQuery(id);
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

    public void addQuery(Statement query) throws StatementException, MalformedURLException, RemoteException {




        if(epService.getEPAdministrator().getStatement(query.getHash())!=null) {

            throw new StatementException(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + query.getHash(), ("Query with id " + query.getHash() + " already added"));

        }

        queryReady.put(query.getName(), true);


        addEsperStatement(query);

    }
    private void addEsperStatement( Statement query) throws StatementException, MalformedURLException, RemoteException {
        ComplexEventHandler handler;
        // add the query and the listener for the query
        try {
            handler = new ComplexEventHandlerImpl(query);
            EPStatement statement = epService.getEPAdministrator().createEPL(query.getStatement(), query.getHash());

            statement.setSubscriber(handler);
            statement.start();

            deployedStatements.put(query.getHash(),query);
        } catch (Exception e) {
            if(e instanceof StatementException)
                throw (StatementException)e;
           loggerService.error(e.getMessage(),e);
            throw e;



        }
    }

    public boolean pauseQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;

        epService.getEPAdministrator().getStatement(name).stop();

        return true;
    }

    public boolean startQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;
        epService.getEPAdministrator().getStatement(name).start();

        return true;
    }

    public boolean removeQuery(String id) {

        if(epService.getEPAdministrator().getStatement(id)==null)
            return false;

        ((ComplexEventHandlerImpl)epService.getEPAdministrator().getStatement(id).getSubscriber()).destroy();

        epService.getEPAdministrator().getStatement(id).destroy();

        deployedStatements.remove(id);

        return true;
    }
}
