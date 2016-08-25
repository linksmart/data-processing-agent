package eu.linksmart.api.event.datafusion;

import java.util.Collection;

/**
 * Created by José Ángel Carvajal on 15.07.2016 a researcher of Fraunhofer FIT.
 */
public interface HTTPResponses<ResourceObject> extends Responses<ResourceObject> {


    public int getOverallStatus();

}
