package eu.linksmart.services.utils.configuration;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface ConfigurationConst {

    final static ArrayList<String> DEFAULT_CONFIGURATION_FILE = initConst();
    static final String DEFAULT_DIRECTORY_CONFIGURATION_FILE = "conf.cfg";
   //final static String CONFIGURATION_FILE_PATH = "eu.linksmart.services.utils.configuration.CONFIGURATION_FILE_PATH";

    static ArrayList<String> initConst(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(DEFAULT_DIRECTORY_CONFIGURATION_FILE);
        return list;
    }
}
