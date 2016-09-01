package eu.linksmart.services.event.datafusion.handler.base;

import eu.linksmart.services.event.datafusion.intern.Utils;
import eu.linksmart.api.event.types.Statement;

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
        loggerService.debug(Utils.getDateNowString() + " update map[] w/ handler " + this.getClass().getSimpleName() + " & query: " + query.getName());
        if(insertStream!=null)
            for (Map m: insertStream)
                eventExecutor.stack(m);
        if(removeStream!=null)
            for (Map m: removeStream)
                eventExecutor.stack(m);
    }

    protected abstract void processMessage(Map events);
    protected void processMessage(Object events){

        if(events!=null) {
            if (events instanceof Map){
                processMessage((Map)events);
            }else if (events instanceof Map[]){
                for (Object o: (Object[])events)
                    processMessage((Map)o);
            } else {
                Map map = new Hashtable<>();
                map.put(events.getClass().getName(),events);
                processMessage(map);
            }
        }
    }
}
