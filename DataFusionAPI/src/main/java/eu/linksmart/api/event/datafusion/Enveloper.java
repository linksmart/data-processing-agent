package eu.linksmart.api.event.datafusion;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public interface Enveloper<IDType, ValueType> {
     public EventType<IDType, ValueType> pack(ValueType payload, Date date, IDType id, IDType idProperty, String description);
    void close();
}
