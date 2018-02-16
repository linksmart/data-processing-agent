package eu.linksmart.services.event.handler.base;

import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.api.event.types.Statement;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 09.08.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseMapEventHandler extends BaseEventHandler {
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
                processMessage(new Map[]{(Map) events});
            }else if (events instanceof Map[]){
               processMessage((Map[])events);
            } else {
                Map map = new Hashtable<>();
                map.put(events.getClass().getName(),events);
                processMessage(map);
            }
        }
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
