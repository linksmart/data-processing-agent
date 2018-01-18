package eu.linksmart.services;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.linksmart.services.event.intern.Const;
import org.hibernate.validator.internal.engine.ConfigurationImpl;

import java.util.Properties;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {


    public static void main(String[] args) {
        com.google.common.collect.UnmodifiableIterator l;

        String confFile = Const.DEFAULT_CONFIGURATION_FILE;
        System.setProperty("log4j.configuration",Const.DEFAULT_CONFIGURATION_FILE);
        System.setProperty("log4j.configurationFile",Const.DEFAULT_DEBUG_CONFIGURATION_FILE);
        if(args.length>0)
            confFile= args[0];

        DataProcessingCore.start(confFile);



    }

}
