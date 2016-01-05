package eu.linksmart.gc.utils.configuration;


import eu.linksmart.gc.utils.function.Utils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Configurator extends ConfigurationConst {

    static protected Configurator def= init();

    static public Configurator getDefaultConfig(){

            for(String confFile: ConfigurationConst.DEFAULT_CONFIGURATION_FILE)
                if(confFile!=null)
                    if(!DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile))
                        def.appendConfig(new Configurator(confFile));


        return def;
    }
    static public boolean addConfFile(String filePath) {
        return !DEFAULT_CONFIGURATION_FILE.contains(filePath) && DEFAULT_CONFIGURATION_FILE.add(filePath);
    }
    static protected void setDefaultConfig(Configurator configurator){
         def= configurator;
    }
    static protected Configurator appendDefaultConfig(Configurator configurator){

        appendConfig(def,configurator);

        return def;

    }
    static protected void appendConfig( Configurator conf, Configurator configurator){

        Iterator<String> stringIterator = configurator.config.getKeys();
        for(String key = null ;stringIterator.hasNext(); key = stringIterator.next() ) {
            if(key!=null )
                if (!conf.config.containsKey(key)){
                    conf.config.addProperty(key, configurator.config.get(Object.class, key));
                }else
                    conf.config.setProperty(key, configurator.config.get(Object.class, key));

        }

    }
     public Configurator appendConfig(Configurator configurator){


         appendConfig(this,configurator);

         return this;

    }
    static protected Configurator init(){
        Configurator configurator = new Configurator();


        for(String confFile: DEFAULT_CONFIGURATION_FILE)
            if(!DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile))
                if(confFile!=null)
                    configurator.appendConfig(new Configurator(confFile));


        return configurator;

    }
    protected Configurator() {
        Configurations  configs = new Configurations();
        File propertiesFile = new File(DEFAULT_INTERN_CONFIGURATION_FILE);


        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(propertiesFile);


        try
        {
            PropertiesConfiguration aux = builder.getConfiguration();
            aux.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

            config = aux;


        }
        catch(ConfigurationException cex)
        {
            //NOTE: The loading configuration has not loaded yet. Therefore, I print directly on the standard error output
            cex.printStackTrace();
        }
    }
    protected Configurator(String configurationFile) {
        Parameters params = new Parameters();


        builder = new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                        .setFileName(configurationFile));

        try
        {
            //NOTE: The loading configuration has not loaded yet. Therefore, I print directly on the standard error output
            PropertiesConfiguration aux = builder.getConfiguration();
            aux.setListDelimiterHandler(new DefaultListDelimiterHandler(','));

            config = aux;


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

    public Date getDate(String key){

        try {
            return Utils.getDateFormat().parse( config.getString(key));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    public List<String> getList(String key){

        return  config.getList(String.class, key);

    }
}
