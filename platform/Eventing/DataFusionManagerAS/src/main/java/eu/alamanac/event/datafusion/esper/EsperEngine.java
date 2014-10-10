package eu.alamanac.event.datafusion.esper;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import eu.alamanac.event.datafusion.esper.utils.Tools;
import eu.alamanac.event.datafusion.handler.ComplexEventHandlerImpl;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Query;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class EsperEngine implements DataFusionWrapper {
    private EPServiceProvider epService;
    Map<String, Map<String,String>> topicName = new HashMap<String, Map<String,String>>();
    Map<String, Map<String,String>> nameTopic = new HashMap<String, Map<String,String>>();
    Map<String, Boolean> queryReady = new HashMap<String,Boolean>();
    Map<String, Query> nameQuery = new HashMap<String,Query >();
    public EsperEngine(){
        Configuration config = new Configuration();
       // config.addImport("eu.almanac.event.datafusion.esper.utils.*");	// package import
        config.addImport(Tools.class);
        epService = EPServiceProviderManager.getDefaultProvider(config);

    }
    private void defineIoTTypes(String esperTopic) {


        epService.getEPAdministrator().getConfiguration().addEventType(esperTopic, IoTEntityEvent.class);

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
        return "Esper";
    }

    /*private boolean addEsperEvent(String esperTopic, String topic, IoTEntityEvent event){
        try {
        synchronized (this) {
            // if the topic type is already defined, then the event is send
            if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {


                epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);

            } else {
                // The type is of the topic is not defined, then is defined now

                defineIoTTypes(esperTopic);

                // check which query are ready to be deploy
                checkQueriesReadiness(topic);

                // add all queries ready to be deploy
                for(String queryName: queryReady.keySet())
                    if(queryReady.get(queryName) && !nameQuery.isEmpty()){
                        ComplexEventHandler handler = new ComplexEventHandlerImpl(nameQuery.get(queryName));
                        try {

                            EPStatement statement = epService.getEPAdministrator().createEPL(nameQuery.get(queryName).getQuery());

                            statement.setSubscriber(handler);
                            nameQuery.remove(queryName);
                            queryReady.remove(queryName);

                        }catch (Exception e){
                            handler.publishError(e.getMessage());
                        }

                    }

                // Send the new Event
                epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);
            }

        }
        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
        return true;
    }*/
    private boolean addEsperEvent(String esperTopic, String topic, IoTEntityEvent event){
        try {
            synchronized (this) {
                // if the topic type is already defined, then the event is send
                if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {


                    epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);

                } else {
                    // The type is of the topic is not defined, then is defined now

                    defineIoTTypes(esperTopic);

                    epService.getEPRuntime().getEventSender(esperTopic).sendEvent(event);

                }

            }
        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
        return true;
    }
    @Override
    public boolean addEvent(String topic, IoTEntityEvent event) {
        try {


            String esperParentTopic ="",parentTopic ="";
            String [] esperTopcArray = topic.substring(1).split("/");

            for (int i=0; i<esperTopcArray.length-1;i++) {
                parentTopic = esperTopcArray[i];

            }

            String esperTopic = topic.substring(1).replace('/', '.');
            esperParentTopic = parentTopic.replace('/', '.');

            addEsperEvent(esperTopic,topic,event);

            addEsperEvent(esperParentTopic,parentTopic,event);

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }
        return true;
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
  /*  public boolean addQuery(Query query) {

        ComplexEventHandler handler;

        try {
             handler = new ComplexEventHandlerImpl(query);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        if(epService.getEPAdministrator().getStatement(query.getName())!=null)

            handler.publishError("Query with name"+query.getName()+"already added");

        try{
            boolean allDefined = true, queryUpdate= false;

            String esperTopic;
            queryReady.put(query.getName(), true);
            for(String topic : query.getInput()){

                // Adapt the topic to a Esper topic
                esperTopic = topic.substring(1).replace('/', '.');

                // changing state of the queries this could be made in several places in several threads!
                synchronized (this) {

                    // if the type of the topic is defined
                    if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {
                        // the type of the topic is not defined

                        allDefined = false;

                        // set this query as not ready to be deploy
                        //update status
                        queryReady.put(query.getName(), false);
                        //update query
                        if(!queryUpdate) {
                            nameQuery.put(query.getName(), query);
                            queryUpdate = true;
                        }
                        // add the query to the wetting queries in this topic
                        if (!topicName.containsKey(topic)) {
                            topicName.put(topic, new HashMap<String, String>());
                            topicName.get(topic).put(query.getName(), query.getName());

                        } else {
                            topicName.get(topic).put(query.getName(), query.getName());
                        }
                        if (!nameTopic.containsKey(query.getName())) {
                            nameTopic.put(query.getName(), new HashMap<String, String>());
                            nameTopic.get(query.getName()).put(topic, topic);

                        } else {
                            nameTopic.get(query.getName()).put(topic, topic);
                        }


                    }
                }
            }
            if(allDefined){
                try {
                    // add the query and the listener for the query
                    EPStatement statement = epService.getEPAdministrator().createEPL(query.getQuery(), query.getName());

                    statement.setSubscriber(handler);

                }catch (Exception e){

                    handler.publishError(e.getMessage());
                }
            }


            return true;

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }

    }
*/
    public boolean addQuery(Query query) {

        ComplexEventHandler handler;

        try {
            handler = new ComplexEventHandlerImpl(query);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        if(epService.getEPAdministrator().getStatement(query.getName())!=null) {

            handler.publishError("Query with name" + query.getName() + "already added");
            return false;
        }
        try{
            boolean allDefined = true, queryUpdate= false;

            String esperTopic;
            queryReady.put(query.getName(), true);
            if (query.haveInput()) {
                for (String topic : query.getInput()) {

                    // Adapt the topic to a Esper topic
                    esperTopic = topic.substring(1).replace('/', '.');

                    // changing state of the queries this could be made in several places in several threads!
                    synchronized (this) {

                        // if the type of the topic is defined
                        if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {
                            defineIoTTypes(esperTopic);
                        }
                    }
                }
            }
                try {
                    // add the query and the listener for the query
                    EPStatement statement = epService.getEPAdministrator().createEPL(query.getQuery(), query.getName());

                    statement.setSubscriber(handler);

                }catch (Exception e){

                    handler.publishError(e.getMessage());
                }


            return true;

        }catch(Exception e){

            e.printStackTrace();

            return false;
        }

    }

    @Override
    public boolean pauseQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;

        epService.getEPAdministrator().getStatement(name).stop();

        return true;
    }

    @Override
    public boolean startQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;
        epService.getEPAdministrator().getStatement(name).start();

        return true;
    }

    @Override
    public boolean removeQuery(String name) {

        if(epService.getEPAdministrator().getStatement(name)==null)
            return false;
        epService.getEPAdministrator().getStatement(name).destroy();

        return true;
    }
}
