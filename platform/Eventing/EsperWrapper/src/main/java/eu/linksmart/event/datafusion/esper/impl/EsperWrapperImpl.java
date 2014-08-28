package eu.linksmart.event.datafusion.esper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import com.espertech.esper.client.*;

import eu.linksmart.api.event.datafusion.*;


public class EsperWrapperImpl implements  DataFusionWrapper {

	private EPServiceProvider epService;
	private Logger LOG = Logger.getLogger(EsperWrapperImpl.class);
	private Map<String,String> topicType;
	Map<String, ArrayList<EsperQuery>> topicQuery;
	private ComplexEventHandler handler;
	
	private static final String SERVICE_ID = EsperWrapperImpl.class.getSimpleName();
	protected void activate(ComponentContext context) throws IOException {
			
			epService = EPServiceProviderManager.getDefaultProvider();
			
			topicType = new HashMap<String,String>();
			topicQuery = new HashMap<String,ArrayList<EsperQuery> >();

			LOG.info("EsperWrapper is ativated!");
			
		}
	protected synchronized void bindFeeder(EventsFeeder feeder) {
		feeder.dataFusionWrapperSignIn(this);
		
	}
	protected synchronized void unbindFeeder(EventsFeeder feeder) {
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
	public boolean addEvent(String topic, Map<String, Object> event) {
		
		try{

			String esperTopic = topic.replace('/', '.');
			
			// if the topic type is already defined, then the event is send
			if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)){
				Object [] esperEvent = new Object[event.size()];
				int i=0;
				for (String key : event.keySet()){
					esperEvent[i] = event.get(key);
					i++;
				}
				epService.getEPRuntime().sendEvent(esperEvent,esperTopic);
				
			}else{
				// The type is of the topic is not defined, then is defined now
				String [] eventNames = new String[event.size()];
				Object [] eventType = new Object[event.size()], esperEvent = new Object[event.size()];
				int i=0;
				for (String key : event.keySet()){
					eventNames[i]= key;
					eventType[i]= String.class;
					esperEvent[i] = event.get(key);
					i++;
				}
				epService.getEPAdministrator().getConfiguration().addEventType(esperTopic, eventNames,eventType);
				
				// Search if there is queries in this topic pending to be added
				if (topicQuery.containsKey(esperTopic)){
					for(EsperQuery q : topicQuery.get(esperTopic)){
							EPStatement statement = epService.getEPAdministrator().createEPL(q.getQuery());
			
							statement.addListener(new QueryListener(q,handler));
						}
				
					topicQuery.remove(esperTopic);	
				}

				// The new event is send
				epService.getEPRuntime().sendEvent(esperEvent,esperTopic);
			}
			
		
			return true;
		
		}catch(Exception e){

			LOG.debug(e);
			
			return false;
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
		topicType.put(topic, eventType);
		
		return true;
	}
	@Override
	public boolean addQuery(String name, String query, String topic) {
		try{
			// Adapt the topic to a Esper topic
			String esperTopic = topic.replace('/', '.');
		
			// if the type of the topic is defined
			if (epService.getEPAdministrator().getConfiguration().isEventTypeExists(esperTopic)){
						
				// add the query and the listener for the query
				EPStatement statement = epService.getEPAdministrator().createEPL(query, name);
				statement.addListener(new QueryListener(new EsperQuery(name,query,topic),handler));
				
			}else{
				// the type of the topic is not defined 
			
				// check if already are queries in this topic  
				if (topicQuery.containsKey(esperTopic))// if, add the query
					topicQuery.get(esperTopic).add(new EsperQuery(name,query,topic));
				else{
					//if not add a new list of queries for this topic
					ArrayList<EsperQuery> aux =new ArrayList<EsperQuery>();
					aux.add(new EsperQuery(name,query,topic));
					topicQuery.put(esperTopic, aux);
				}
			}
			
			return true;
		
		}catch(Exception e){

			LOG.debug(e);
			
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