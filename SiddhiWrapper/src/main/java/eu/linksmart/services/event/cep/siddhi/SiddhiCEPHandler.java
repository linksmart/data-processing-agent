package eu.linksmart.services.event.cep.siddhi;

import eu.almanac.event.datafusion.handler.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.types.Statement;
import eu.linksmart.api.event.datafusion.exceptions.StatementException;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.06.2016 a researcher of Fraunhofer FIT.
 */
public class SiddhiCEPHandler extends QueryCallback {
    protected ComplexEventHandler mqttHandler;
    public SiddhiCEPHandler(Statement query) throws Exception, StatementException {
        mqttHandler = new ComplexEventHandler(query);
    }

    @Override
    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
        mqttHandler.update(packingArray(inEvents), packingArray(removeEvents));
    }
    protected Map<String,Object> extractMap(Event[] inEvents){
        Map<String, Object> eventMap = new Hashtable<>();
        for(Event event: inEvents)
            for(int i=0; i< event.getData().length;i++) {
                switch (i) {
                    case 0:
                        eventMap.put("id", event.getData()[i]);
                        break;
                    case 1:
                        eventMap.put("time", event.getData()[i]);
                        break;
                    case 2:
                        eventMap.put("stringValue", event.getData()[i]);
                        break;
                    case 3:
                        eventMap.put("numericValue", event.getData()[i]);
                        break;
                }

        }
        return  eventMap;
    }
    protected Map[] packingArray(Event[] events){
        Map[] eventMap = null;

        if (events!=null) {
            eventMap = new Hashtable[1];

            eventMap[0] = extractMap(events);
        }
        return eventMap;


    }
}
