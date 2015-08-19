package eu.linksmart.gc.utils.configuration;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class ConfigurationConst {

    protected static ArrayList<String> DEFAULT_CONFIGURATION_FILE = initConst();
    protected static final String DEFAULT_INTERN_CONFIGURATION_FILE = "conf.cfg";
    protected static ArrayList<String> initConst(){
        ArrayList<String> list = new ArrayList<String>();
        list.add(DEFAULT_INTERN_CONFIGURATION_FILE);
        return list;
    }
}
