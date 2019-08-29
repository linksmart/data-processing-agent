package eu.linksmart.services;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.linksmart.services.event.intern.Const;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {


    public static void main(String[] args) {

        String confFile = Const.DEFAULT_CONFIGURATION_FILE;
        System.setProperty("log4j.configuration",Const.DEFAULT_CONFIGURATION_FILE);
        System.setProperty("log4j.configurationFile",Const.DEFAULT_DEBUG_CONFIGURATION_FILE);
        if(args.length>0)
            confFile= args[0];
        DataProcessingCore.start(confFile);



    }

}
