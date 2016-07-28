package eu.almanac.event.datafusion.handler.base;

import eu.linksmart.api.event.datafusion.Statement;

import java.util.Map;

/**
 * Created by José Ángel Carvajal on 27.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseListEventHandler extends BaseMapEventHandler<Map> {
    public BaseListEventHandler(Statement statement) {
        super(statement);
    }
}
