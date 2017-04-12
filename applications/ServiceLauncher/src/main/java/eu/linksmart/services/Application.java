package eu.linksmart.services;
import eu.linksmart.services.event.core.DataProcessingCore;
import eu.linksmart.services.event.intern.Const;

import java.util.Properties;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */

public class Application {
    static Properties info = null;


    public static void main(String[] args) {

        String confFile = Const.DEFAULT_CONFIGURATION_FILE;
        if(args.length>0)
            confFile= args[0];

        DataProcessingCore.start(confFile);



    }

}
