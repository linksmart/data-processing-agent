package eu.linksmart.gc.utils.configuration;


import eu.linksmart.gc.utils.function.Utils;
import org.apache.commons.configuration.*;

import javax.naming.ConfigurationException;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Configurator extends  CompositeConfiguration {

    static final protected Configurator def= init();

    static public Configurator getDefaultConfig(){


        ConfigurationConst.DEFAULT_CONFIGURATION_FILE.stream().filter(confFile -> confFile != null).filter(confFile -> !ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile)).forEach(confFile -> {

            def.addConfiguration(new Configurator(confFile));
        });


        return def;
    }
    static public boolean addConfFile(String filePath) {
        return !ConfigurationConst.DEFAULT_CONFIGURATION_FILE.contains(filePath) && ConfigurationConst.DEFAULT_CONFIGURATION_FILE.add(filePath);
    }


    static protected Configurator init(){
        Configurator configurator = new Configurator();


        ConfigurationConst.DEFAULT_CONFIGURATION_FILE.stream().filter(confFile -> !ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile)).filter(confFile -> confFile != null).forEach(confFile -> configurator.addConfiguration(new Configurator(confFile)));


        return configurator;

    }
    protected Configurator() {
        this(ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE);

    }
    protected Configurator(String configurationFile) {
        super();
        String filename= configurationFile,extension=null;


        if(!fileExists(filename)) {
            filename ="etc/" +configurationFile;

            if(!fileExists(filename)){
                filename ="etc/linksmart/" +configurationFile;
            }else {
                System.err.println("File named " + configurationFile + " was not found in '.', './etc/' and 'etc/linksmart/'");
                System.err.println("Trying to load configuration from environmental variables ");
                filename =null;
                addConfiguration(new EnvironmentConfiguration());
            }
        }
        if(filename!=null){
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                extension = filename.substring(i + 1);
                try {
                    switch (extension) {
                        case "properties":
                        case "cfg":
                            addConfiguration(new PropertiesConfiguration(filename));
                            break;
                        case "xml":
                            try {
                                addConfiguration(new XMLPropertiesConfiguration(filename));
                            } catch (Exception e) {
                                addConfiguration(new XMLConfiguration(filename));
                            }
                            break;
                        default:
                            System.err.println("Trying to load configuration from environmental variables ");
                            addConfiguration(new EnvironmentConfiguration());
                            break;
                    }
                } catch (Exception e) {

                    e.printStackTrace();

                }
            }else
                System.err.println("File named " + configurationFile + " doesn't have an extension");
        }
    }



    private static boolean fileExists(String filename){
        File f = new File(filename);
        URL u = Utils.class.getClassLoader().getResource(filename);
        return (f.exists() && f.isDirectory())|| u!=null;
    }

    public Date getDate(String key){

        try {
            return Utils.getDateFormat().parse( getString(key));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
}
