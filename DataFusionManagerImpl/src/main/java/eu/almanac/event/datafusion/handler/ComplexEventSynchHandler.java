package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.epl.intern.StatementInstance;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.Map;

/**
 * Created by José Ángel Carvajal on 16.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComplexEventSynchHandler extends BaseEventHandler<Map> implements ComplexEventHandler<Map> {
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
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
}
