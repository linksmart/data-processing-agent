package eu.linksmart.services.payloads.ogc.sensorthing;

/**
 * Created by José Ángel Carvajal on 06.12.2017 a researcher of Fraunhofer FIT.
 */

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;

/**
 * This class configure some features related to the Sensor things inside the agent.
 * */
public class LinksBlocker {
    protected static transient Logger loggerService = Utils.initLoggingConf(LinksBlocker.class);
    protected static transient Configurator conf =  Configurator.getDefaultConfig();
    static {
        CommonControlInfo.setNavigationLinkEnabled(false);
        CommonControlInfo.setSelfLinkEnabled(false);
    }

}
