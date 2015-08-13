package eu.linksmart.gc.utils.configuration;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Configurator {

    static protected Configurator def= init();

    static public Configurator getDefaultConfig(){
        if(
                !ConfigurationConst.DEFAULT_CONFIGURATION_FILE.equals(ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE) &&
                        ( !def.confPath.contains(ConfigurationConst.DEFAULT_CONFIGURATION_FILE)||!def.confPath.contains(ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE))
                ){
            def.appendConfig(new Configurator(ConfigurationConst.DEFAULT_CONFIGURATION_FILE));
        }

        return def;
    }
    static public void setDefaultConfig(Configurator configurator){
         def= configurator;
    }
    static public Configurator appendDefaultConfig(Configurator configurator){

        appendConfig(def,configurator);

        return def;

    }
    static public void appendConfig( Configurator conf, Configurator configurator){

        Iterator<String> stringIterator = configurator.config.getKeys();
        for(String key = null ;stringIterator.hasNext(); key = stringIterator.next() ) {
            if(key!=null )
                if (!conf.config.containsKey(key)){
                    conf.config.addProperty(key, configurator.config.get(Object.class, key));
                }else
                    conf.config.setProperty(key, configurator.config.get(Object.class, key));

        }
        conf.confPath.addAll(configurator.confPath);

    }
     public Configurator appendConfig(Configurator configurator){


         appendConfig(this,configurator);

         return this;

    }
    static protected Configurator init(){

        if(!ConfigurationConst.DEFAULT_CONFIGURATION_FILE.equals(ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE)){
            return new Configurator().appendConfig(new Configurator(ConfigurationConst.DEFAULT_CONFIGURATION_FILE));
        }
        return new Configurator();

    }
    protected Configurator() {
        confPath.add( ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE);
        Configurations  configs = new Configurations();
        File propertiesFile = new File(ConfigurationConst.DEFAULT_INTERN_CONFIGURATION_FILE);

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);


        try
        {
             config = builder.getConfiguration();


        }
        catch(ConfigurationException cex)
        {
            //NOTE: The loading configuration has not loaded yet. Therefore, I print directly on the standard error output
            cex.printStackTrace();
        }
    }
    public Configurator(String configurationFile) {
        confPath.add( configurationFile);
        Parameters params = new Parameters();

        builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                        .setFileName(configurationFile));
        try
        {
            //NOTE: The loading configuration has not loaded yet. Therefore, I print directly on the standard error output
            config = builder.getConfiguration();

        }
        catch(ConfigurationException cex)
        {
            cex.printStackTrace();
        }
    }
    private  Configuration config = null;
    private  FileBasedConfigurationBuilder<PropertiesConfiguration> builder =null;

    public  Object get(String key){

        return  config.getProperty(key);

    }
    public  int getInt(String key){

        return  config.getInt(key);

    }
    public  double getDouble(String key){

        return  config.getDouble(key);

    }
    public  String getString(String key){

        return  config.getString(key);

    }
    public  boolean getBool(String key){

        return  config.getBoolean(key);

    }

    public List<String> getList(String key){

        return  config.getList(String.class, key);

    }
    protected ArrayList<String> confPath = new ArrayList<String>();
}
