package eu.linksmart.services.event.handler.base;

import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.api.event.types.Statement;

/**
 * Created by José Ángel Carvajal on 09.08.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseArrayObjectEventHandler extends BaseEventHandler {
    public BaseArrayObjectEventHandler(Statement statement) {
        super(statement);
    }

    @Override
    protected void processMessage(Object events) {
        if(events!=null){
            if(events.getClass().isAssignableFrom(Object[][].class)){
                processMessage((Object[][])events);
            }else if(events.getClass().isAssignableFrom(Object[].class)){
                processMessage((Object[])events);
            }else {
                processMessage(new Object[]{events});
            }
        }

    }
    protected abstract void processMessage(Object[][] events);
    protected abstract void processMessage(Object[] events);
    public void update(Object[][] insertStream, Object[][] removeStream) {
        loggerService.debug(AgentUtils.getDateNowString() + " update Object[][] w/ handler " + this.getClass().getSimpleName() + " & query: " + query.getName());
        if (insertStream != null)
            eventExecutor.stack(insertStream);
        if (removeStream != null)
            eventExecutor.stack(removeStream);
    }

}
