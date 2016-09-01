package eu.linksmart.services;

import eu.linksmart.services.event.datafusion.feeder.FeederConst;
import eu.linksmart.services.event.datafusion.handler.HandlerConst;

/**
 * Created by José Ángel Carvajal on 15.03.2016 a researcher of Fraunhofer FIT.
 */
public interface Const extends  eu.linksmart.services.utils.constants.Const, FeederConst, HandlerConst {
    public static final String DEFAULT_CONFIGURATION_FILE ="conf.cfg";
}
