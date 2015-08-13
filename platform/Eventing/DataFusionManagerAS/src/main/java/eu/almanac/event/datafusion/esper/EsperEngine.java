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
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class EsperEngine implements DataFusionWrapper {
    private EPServiceProvider epService;
    Map<String, Map<String,String>> topicName = new HashMap<>();
    Map<String, Map<String,String>> nameTopic = new HashMap<>();
    Map<String, Boolean> queryReady = new HashMap<>();
    private  LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    private Configurator conf =  Configurator.getDefaultConfig();
    public EsperEngine() throws MalformedURLException, MqttException {
        Configuration config = new Configuration();

        config.addImport("java.security.*");
        config.addImport(Tools.class);
        config.addImport(UUID.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);
        defineIoTTypes("observation", Observation.class);
        loggerService.info("Esper engine has started!");



    }
    private void defineIoTTypes(String esperTopic, Class type) {


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






           // addEsperEvent(esperTopic, event);


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
    public boolean addStatement(Statement statement) throws StatementException{
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
             addQuery(statement);
        }

        return ret;
    }

    @Override
    public void destroy() {
        for (String i: epService.getEPAdministrator().getStatementNames())
            removeQuery(i);
        loggerService.info(getName() + " logged off");

    }

    public void addQuery(Statement query) throws StatementException {




        if(epService.getEPAdministrator().getStatement(query.getName())!=null) {

            throw new StatementException(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH) + query.getHash(), ("Query with name" + query.getName() + " already added"));

        }

            queryReady.put(query.getName(), true);
            /* if (query.haveInput()) {
                for (String topic : query.getInput()) {

                    // Adapt the topic to a Esper topic
                    //esperTopic = topic.substring(1).replace('/', '.');

                    // changing state of the queries this could be made in several places in several threads!
                   synchronized (this) {

                        // if the type of the topic is defined
                        if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists(topic) && topic.contains("/federation")) {
                            // ToDO: this must be dynamic decided!
                           // defineIoTTypes("observation", Observation.class);
                        }
                    }
                }
            }*/


        addEsperStatement(query);






    }
    private void addEsperStatement( Statement query){
        ComplexEventHandler handler;
        // add the query and the listener for the query
        EPStatement statement = epService.getEPAdministrator().createEPL(query.getStatement(), query.getName());
        try {
            handler = new ComplexEventHandlerImpl(query);

            statement.setSubscriber(handler);
        } catch (Exception e) {
           loggerService.error(e.getMessage(),e);

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

    public boolean removeQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;

        ((ComplexEventHandlerImpl)epService.getEPAdministrator().getStatement(name).getSubscriber()).destroy();

        epService.getEPAdministrator().getStatement(name).destroy();

        return true;
    }
}
