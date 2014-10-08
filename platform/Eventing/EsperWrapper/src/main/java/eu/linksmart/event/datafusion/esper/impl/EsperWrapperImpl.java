package eu.linksmart.event.datafusion.esper.impl;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component(name="DataFusionCoreImpl", immediate = true)
@Service({DataFusionWrapper.class})
public class EsperWrapperImpl implements  DataFusionWrapper {

	private EPServiceProvider epService;
	private Logger LOG = Logger.getLogger(EsperWrapperImpl.class);
//	private Map<String,String> topicType;
	Map<String, Map<String,String>> topicName = new HashMap<String, Map<String,String>>();
    Map<String, Map<String,String>> nameTopic = new HashMap<String, Map<String,String>>();
    Map<String, Boolean> queryReady = new HashMap<String,Boolean>();
    Map<String, EsperQuery> nameQuery = new HashMap<String,EsperQuery >();
    @Reference(name="ComplexEventHandler",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindCEH",
            unbind="unbindCEH",
            policy= ReferencePolicy.DYNAMIC)
	private ComplexEventHandler handler;
    @Reference(name="EventFeeder",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindFeeder",
            unbind="unbindFeeder",
            policy= ReferencePolicy.DYNAMIC)
    EventFeeder feeder;
	private static final String SERVICE_ID = EsperWrapperImpl.class.getSimpleName();
    @Activate
	protected void activate(ComponentContext context) throws IOException {
			
			epService = EPServiceProviderManager.getDefaultProvider();

			LOG.info("EsperWrapper is ativated!");
			
		}
	protected synchronized void bindFeeder(EventFeeder feeder) {
		feeder.dataFusionWrapperSignIn(this);
		
	}
	protected synchronized void unbindFeeder(EventFeeder feeder) {
		feeder.dataFusionWrapperSignOut(this);
	}
	protected synchronized void bindCEH(ComplexEventHandler handler) {
		this.handler = handler;
		handler.dataFusionWrapperSignIn(this);
		
	}
	protected synchronized void unbindCEH(ComplexEventHandler handler) {
		handler.dataFusionWrapperSignOut(this);
	}
	@Override
	public boolean addEvent(String topic, Map<String, String> event) {
		
		try {

            String esperTopic = topic.replace('/', '.');

            synchronized (this) {
                // if the topic type is already defined, then the event is send
                if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {
                    Object[] esperEvent = new Object[event.size()];
                    int i = 0;
                    for (String key : event.keySet()) {
                        esperEvent[i] = event.get(key);
                        i++;
                    }
                    epService.getEPRuntime().sendEvent(esperEvent, esperTopic);

                } else {
                    // The type is of the topic is not defined, then is defined now
                    String[] eventNames = new String[event.size()];
                    Object[] eventType = new Object[event.size()], esperEvent = new Object[event.size()];
                    int i = 0;
                    for (String key : event.keySet()) {
                        eventNames[i] = key;
                        eventType[i] = String.class;
                        esperEvent[i] = event.get(key);
                        i++;
                    }
                    epService.getEPAdministrator().getConfiguration().addEventType(esperTopic, eventNames, eventType);

                    // check which query are ready to be deploy
                    checkQueriesReadiness(topic);

                    // add all queries ready to be deploy
                    for(String queryName: queryReady.keySet())
                        if(queryReady.get(queryName)){
                            try {

                                EPStatement statement = epService.getEPAdministrator().createEPL(nameQuery.get(queryName).getQuery());
                                statement.addListener(new QueryListener(nameQuery.get(queryName), handler));
                                nameQuery.remove(queryName);
                                queryReady.remove(queryName);
                                LOG.info("Query added in esper!");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    // Send the new Event
                    epService.getEPRuntime().sendEvent(esperEvent, esperTopic);
                }

            }

            return true;

        }catch(Exception e){

			LOG.debug(e);
			
			return false;
		}	
		
	}
    private void checkQueriesReadiness( String newEventWithTopic){

        for(String queryName: topicName.get(newEventWithTopic).values()) {
            nameTopic.get(queryName).remove(newEventWithTopic);
            queryReady.put(queryName, nameTopic.get(queryName).isEmpty());
        }

    }



    @Override
	public boolean addEventType(String nameType, String[] eventSchema, Object[] eventTypes) {
		
		//TODO: it this really necessary?
		try{

			epService.getEPAdministrator().getConfiguration().addEventType(nameType, eventSchema, eventTypes);
		
			return true;
		
		}catch(Exception e){

			LOG.debug(e);
			
			return false;
		}	
		
	}
	@Override
	public boolean setTopicToEventType(String topic, String eventType) {

		//TODO: it this really necessary?
		//topicType.put(topic, eventType);
		
		return true;
	}
	@Override
	public boolean addQuery(String name, String query, String[] topics) {
		try{
            boolean allDefined = true, queryUpdate= false;

            String esperTopic;
            queryReady.put(name, true);
            for(String topic : topics){

                // Adapt the topic to a Esper topic
                 esperTopic = topic.replace('/', '.');

                // changing state of the queries this could be made in several places in several threads!
                synchronized (this) {
                    // if the type of the topic is defined
                    if (!epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)) {
                        // the type of the topic is not defined

                        allDefined = false;

                        // set this query as not ready to be deploy
                        //update status
                        queryReady.put(name, false);
                        //update query
                       if(!queryUpdate) {
                           nameQuery.put(name, new EsperQuery(name, query, topics));
                           queryUpdate = true;
                       }
                        // add the query to the wetting queries in this topic
                        if (!topicName.containsKey(topic)) {
                            topicName.put(topic, new HashMap<String, String>());
                            topicName.get(topic).put(name, name);

                        } else {
                            topicName.get(topic).put(name, name);
                        }
                        if (!nameTopic.containsKey(name)) {
                            nameTopic.put(name, new HashMap<String, String>());
                            nameTopic.get(name).put(topic, topic);

                        } else {
                            nameTopic.get(name).put(topic, topic);
                        }


                    }
                }
            }
            if(allDefined){
                // add the query and the listener for the query
                EPStatement statement = epService.getEPAdministrator().createEPL(query, name);
                statement.addListener(new QueryListener(new EsperQuery(name, query, topics), handler));
            }


			return true;
		
		}catch(Exception e){

			LOG.debug(e.getStackTrace());
			
			return false;
		}	
		
		
		
	}


    @Override
	public String getName() {
		return "Esper.CEP.Engine";
	}
	@Override
	public boolean puseQuery(String name) {
		epService.getEPAdministrator().getStatement(name).stop();
		
		return epService.getEPAdministrator().getStatement(name).isStopped();
	}
	@Override
	public boolean startQuery(String name) {
		epService.getEPAdministrator().getStatement(name).start();
		
		return epService.getEPAdministrator().getStatement(name).isStarted();
	}
	@Override
	public boolean removeQuery(String name) {
		epService.getEPAdministrator().getStatement(name).destroy();
		
		return epService.getEPAdministrator().getStatement(name).isDestroyed();
	}

	
}