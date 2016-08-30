package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.handler.base.BaseEventHandler;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.api.event.datafusion.components.ComplexEventSyncHandler;
import org.slf4j.Logger;


/**
 * Created by José Ángel Carvajal on 16.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComplexEventSynchHandler extends BaseEventHandler implements ComplexEventSyncHandler {
    protected transient Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected Statement statement;


    public ComplexEventSynchHandler(Statement statement) {
        super(statement);
        this.statement =statement;

    }

    @Override
    protected void processMessage(Object events) {
        if(events!=null)
            statement.setSynchronousResponse(events);
    }
    @Override
    public void update(Object event) {
        eventExecutor.stack(event);
    }
}
