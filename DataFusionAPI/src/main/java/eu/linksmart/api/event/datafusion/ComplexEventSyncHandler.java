package eu.linksmart.api.event.datafusion;

/**
 * Created by José Ángel Carvajal on 09.08.2016 a researcher of Fraunhofer FIT.
 */
public interface ComplexEventSyncHandler extends ComplexEventHandler {
    void update(Object events);

}
