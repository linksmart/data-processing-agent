package eu.linksmart.services.event.handler.base;

import eu.linksmart.api.event.types.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 27.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseListEventHandler extends BaseArrayObjectEventHandler {
    public BaseListEventHandler(Statement statement) {
        super(statement);
    }

    @Override
    protected void processMessage(Object[][] events) {

        processMessage(toList(events));
    }
    @Override
    protected void processLeavingMessage(Object[][] events) {

        processLeavingMessage(toList(events));
    }
    private List<Object> toList(Object events){
        List<Object> response = new ArrayList<>();
        if(events instanceof Object[])
            for (Object o : ( Object[])events)
                response.addAll(Arrays.asList(( Object[])o));
        else
            response.add(events);
        return response;
    }


    @Override
    protected void processMessage(Object[] events) {
        processMessage(Arrays.asList(events));
    }

    @Override
    protected void processLeavingMessage(Object[] events) {
        processMessage(Arrays.asList(events));
    }

    protected abstract void processMessage(List<Object> events) ;

    protected abstract void processLeavingMessage(List<Object> events) ;
}
