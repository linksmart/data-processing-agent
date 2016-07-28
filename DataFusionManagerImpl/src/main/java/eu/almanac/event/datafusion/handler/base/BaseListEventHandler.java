package eu.almanac.event.datafusion.handler.base;

import eu.linksmart.api.event.datafusion.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 27.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseListEventHandler extends BaseArrayEventHandler {
    public BaseListEventHandler(Statement statement) {
        super(statement);
    }

    @Override
    protected void processMessage(Object[] events) {
        processMessage(Arrays.asList(events));
    }
    protected abstract void processMessage(List<Object> events) ;
}
