package eu.linksmart.services.event.handler.base;

import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.utils.serialization.DefaultSerializerDeserializer;
import eu.linksmart.services.utils.serialization.Serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 09.08.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseMapEventHandler extends BaseEventHandler {

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
    protected Publisher publisher;
    private Serializer  loggingSerializer = new DefaultSerializerDeserializer();
    protected long logEveryCount = 1;
    private boolean lastCorrect = false;
    private Date lastMessage = null;
    public BaseMapEventHandler(Statement statement) {
        super(statement);
    }
    public void update(Map[] insertStream, Map[] removeStream){
        loggerService.debug(AgentUtils.getDateNowString() + " update map[] w/ handler " + this.getClass().getSimpleName() + " & query: " + query.getName());
        if(insertStream!=null)
           eventExecutor.insertStack(insertStream);
        if(removeStream!=null)
            eventExecutor.removeStack(removeStream);
    }

    protected abstract void processMessage(Map[] events);

    protected abstract void processLeavingMessage(Map[] events);
    protected void processMessage(Object events){

        if(events!=null) {
            if (events instanceof Map){
                if( !query.isDiscardDataOnFailPolicyOn() || (query.isDiscardDataOnFailPolicyOn() && loggingValidation(new Map[]{(Map) events}) ) )
                    processMessage(new Map[]{(Map) events});
                else
                    loggerService.warn("The handle of statement id:" + query.getId() + " discarded an event");
            }else if (events instanceof Map[]) {
                if( !query.isDiscardDataOnFailPolicyOn() || (query.isDiscardDataOnFailPolicyOn() && loggingValidation((Map[]) events) ) )
                    processMessage((Map[]) events);
                else
                    loggerService.warn("The handle of statement id:" + query.getId() + " discarded an event");
            } else {
                Map map = new Hashtable<>();
                map.put(events.getClass().getName(),events);
                processMessage(map);
            }
        }
    }
    protected boolean loggingValidation(Map[] events) {
        Date current = new Date();
        if(lastMessage == null)
            lastMessage = current;

        boolean correct=true;
        if (events == null)
            loggerService.warn("The handle of statement id:" + query.getId() + " received an empty event");
        else if (query.getSchema() != null) {

            if (logEveryCount==1 || !lastCorrect || current.after(new Date(lastMessage.getTime() + query.getLogEventEvery()*60000L))) {
                if (!query.getSchema().validate(events))
                    try {
                        loggerService.warn("Handler of statement: " + query.getId() + " data do not expect structure below:  \n" + loggingSerializer.toString(events));

                    } catch (IOException e) {
                        loggerService.error(e.getMessage());
                    } finally {
                        correct = lastCorrect = false;
                    }
                else {
                    lastCorrect = true;
                    loggerService.info("Handler of statement: " + query.getId() + " had triggered n=" + logEveryCount + " processing process");
                }
                lastMessage = current;
            }

        } else if( current.after(new Date(lastMessage.getTime() + query.getLogEventEvery()*60000L)))
            loggerService.info("Handler of statement: " + query.getId() + " had triggered n=" + logEveryCount + " processing process");

        try {
            logEveryCount = Math.addExact(1, logEveryCount);
        } catch (ArithmeticException e) {
            logEveryCount = 0;
        }
        return correct;
    }

    protected void processLeavingMessage(Object events){

        if(events!=null) {
            if (events instanceof Map){
                processLeavingMessage(new Map[]{(Map) events});
            }else if (events instanceof Map[]){
                processLeavingMessage((Map[])events);
            } else {
                Map map = new Hashtable<>();
                map.put(events.getClass().getName(),events);
                processLeavingMessage(map);
            }
        }
    }
}
