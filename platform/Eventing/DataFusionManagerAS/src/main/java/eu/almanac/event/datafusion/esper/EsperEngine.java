package eu.almanac.event.datafusion.esper;

import com.espertech.esper.client.*;
import eu.almanac.event.datafusion.esper.utils.Tools;
import eu.almanac.event.datafusion.handler.ComplexEventHandlerImpl;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Statement;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class EsperEngine implements DataFusionWrapper {
    private EPServiceProvider epService;
    Map<String, Map<String,String>> topicName = new HashMap<String, Map<String,String>>();
    Map<String, Map<String,String>> nameTopic = new HashMap<String, Map<String,String>>();
    Map<String, Boolean> queryReady = new HashMap<String,Boolean>();
    Map<String, Statement> nameQuery = new HashMap<String,Statement >();
    public EsperEngine(){
        Configuration config = new Configuration();
        // config.addImport("eu.almanac.event.datafusion.esper.utils.*");	// package import
        config.addImport("java.security.*");
        config.addImport(Tools.class);
        config.addImport(UUID.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);

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
                if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {


                    epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);

                } else {
                    // The type is of the topic is not defined, then is defined now

                    defineIoTTypes(esperTopic, type);

                    epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);

                }

            }
        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
        return true;
    }

    public String[] getParentTopic(String topic){
        String esperParentTopic ="";
        if(topic.charAt(0)== '/')
            topic = topic.substring(1);

        String [] esperTopicArray = topic.split("/");

        esperParentTopic = esperTopicArray[0];
        for (int i=1; i<esperTopicArray.length-1;i++) {
            esperParentTopic += "."+esperTopicArray[i];

        }

        return new String[]{esperParentTopic, esperTopicArray[esperTopicArray.length-1]};
    }
    @Override
    public boolean addEvent(String topic, Object event,Class type) {
        try {






           // addEsperEvent(esperTopic, event);


            String[] parentTopicAndHead = getParentTopic(topic);

            addEsperEvent(parentTopicAndHead[0]+ ".hash",event, type );
            insertStream('T'+((Observation)event).getSensor().getId(),parentTopicAndHead[0]);

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
        return true;
    }



    private Integer noTopics =0;
    private boolean insertStream(String head, String paretTopic){
        if (epService.getEPAdministrator().getStatement(head)!=null)
            return false;
        EPStatement statement =null;
        try {
             statement = epService.getEPAdministrator().createEPL("insert into "+paretTopic+"."+head+" select * from "+paretTopic+".hash (id = '"+head+"')" , head);


        }catch (EPStatementSyntaxException Esyn){
            Esyn.printStackTrace();
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
    public boolean addStatement(Statement statement) {
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

                ComplexEventHandler.knownInstances.put(nameURL[0],nameURL[1]);

            }else {
                LoggerHandler.report("syntax_error","Statement " + statement.getName()+" try to add a instance but the format is incorrect, the correct format is 'add instance <instanceName>=<instanceURL>'");
            }

        }else {
            ret =  addQuery(statement);
        }

        return ret;
    }

    @Override
    public void destroy() {
        for (String i: epService.getEPAdministrator().getStatementNames())
            removeQuery(i);

        LoggerHandler.report("info",getName()+" logged off");

    }

    public boolean addQuery(Statement query) {




        if(epService.getEPAdministrator().getStatement(query.getName())!=null) {

            LoggerHandler.publish("queries/"+query.getName(),"Query with name" + query.getName() + " already added",null, true);
            return false;
        }
        try{
            boolean allDefined = true, queryUpdate= false;

            String esperTopic;
            queryReady.put(query.getName(), true);
            if (query.haveInput()) {
                for (String topic : query.getInput()) {

                    // Adapt the topic to a Esper topic
                    //esperTopic = topic.substring(1).replace('/', '.');

                    // changing state of the queries this could be made in several places in several threads!
                    synchronized (this) {

                        // if the type of the topic is defined
                        if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists(topic)) {
                            // ToDO: this must be dynamic decided!
                            defineIoTTypes(topic, Observation.class);
                        }
                    }
                }
            }
                try {

                    addEsperStatement(query);
                }catch (Exception e){
                    try {

                        if(e.getMessage().startsWith("Failed to resolve event type:")) {
                            defineIoTTypes(e.getMessage().split("'")[1], Observation.class);
                            addEsperStatement(query);
                        }
                    }catch (Exception ex){
                        LoggerHandler.publish("queries/"+query.getName(),ex.getMessage(),null,true);
                    }
                    LoggerHandler.publish("queries/"+query.getName(),e.getMessage(),null,true);
                }


            return true;

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }

    }
    private void addEsperStatement( Statement query){
        ComplexEventHandler handler;
        // add the query and the listener for the query
        EPStatement statement = epService.getEPAdministrator().createEPL(query.getStatement(), query.getName());
        try {
            handler = new ComplexEventHandlerImpl(query);

            statement.setSubscriber(handler);
        } catch (RemoteException e) {
            e.printStackTrace();

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
