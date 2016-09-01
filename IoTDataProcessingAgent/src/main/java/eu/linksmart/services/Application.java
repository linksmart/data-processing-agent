package eu.linksmart.services;

import eu.linksmart.services.event.datafusion.core.DataProcessingCore;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {
    public static void main(String[] args) {
        String confFile = Const.DEFAULT_CONFIGURATION_FILE;
        if(args.length>0)
            confFile= args[0];

        DataProcessingCore.run(confFile);


    }
}
