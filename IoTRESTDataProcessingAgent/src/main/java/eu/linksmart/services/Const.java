package eu.linksmart.services;

import eu.almanac.event.datafusion.feeder.FeederConst;
import eu.almanac.event.datafusion.handler.HandlerConst;

/**
 * Created by José Ángel Carvajal on 15.03.2016 a researcher of Fraunhofer FIT.
 */
public interface Const extends  eu.almanac.event.datafusion.intern.Const, FeederConst, HandlerConst {
    public static final String DEFAULT_CONFIGURATION_FILE ="conf.cfg";
}
