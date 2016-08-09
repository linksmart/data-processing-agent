package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.handler.base.BaseEventHandler;
import eu.almanac.event.datafusion.handler.base.BaseMapEventHandler;
import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.epl.intern.StatementInstance;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.ComplexEventSyncHandler;
import eu.linksmart.api.event.datafusion.Statement;
import org.slf4j.Logger;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 16.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComplexEventSynchHandler extends BaseMapEventHandler implements ComplexEventSyncHandler {
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected StatementInstance statement;
    public ComplexEventSynchHandler(Statement statement) {
        super(statement);
        this.statement =(StatementInstance)statement;

    }

    @Override
    protected void processMessage(Map events) {
        statement.setSynchronousResponse(events);
    }



    @Override
    public synchronized void destroy() {

    }

    @Override
    public void update(Object event) {
        statement.setSynchronousResponse(event);
    }
}
