package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.intern.Utils;
import eu.almanac.event.datafusion.utils.epl.intern.EPLStatement;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.Map;

/**
 * Created by José Ángel Carvajal on 16.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComplexEventSynchHandler extends Component implements ComplexEventHandler {
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected EPLStatement statement;
    public ComplexEventSynchHandler(Statement statement) {
        super(ComplexEventSynchHandler.class.getSimpleName(),"Handles the response of the CEP engines to generate sync response", ComplexEventHandler.class.getSimpleName());
        this.statement =(EPLStatement)statement;

    }


    protected Object waiter = new Object();
    @Override
    public synchronized void update(Map eventMap) {

        statement.setSynchronouseResponse(eventMap);

    }

    @Override
    public synchronized void destroy() {
        waiter.notify();
        waiter =null;
    }
}
